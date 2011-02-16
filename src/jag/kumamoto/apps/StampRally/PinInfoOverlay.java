package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.StampPin;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


/**
 * 
 * スタンプラリーのピン情報をマップ上に表示するオーバーレイ
 * 
 * @author aharisu
 *
 */
public class PinInfoOverlay extends ItemizedOverlay<OverlayItem>{
	public static interface OnClickListener {
		public void onClick(StampPin pin);
	}
	
	private static class InfoMarker extends OverlayItem {
		
		private static final int MinInfomationWidth = 250;
		private static final int MaxOneLineTextLength = 10;
		private static final int InformationMargin = 22;
		private static final int InformationTitleTextSize = 48;
		
		public static final int InfomationOffset = -75;
		
		private static final Path mPath = new Path();
		private static final Paint mPaint = new Paint();
		private static final Rect mTextBounds = new Rect();
		
		private final Drawable mNormalDrawable;
		private final Drawable  mPressedDrawable;
		
		public final StampPin stampPin;
		
		static {
			mPaint.setAntiAlias(true);
			mPaint.setTextSize(InformationTitleTextSize);
			mPaint.setStrokeWidth(1);
			mPaint.setStyle(Style.FILL);
		}
		
		public InfoMarker(StampPinOverlay.StampRallyMarker marker) {
			super(marker.getPoint(), marker.getTitle(), marker.getSnippet());
			
			this.stampPin = marker.stampPin;
			
			mNormalDrawable = createInfoDrawable(marker, 0xffefefef);
			boundCenterBottom(mNormalDrawable).getBounds().offset(0,InfomationOffset);
			mPressedDrawable = createInfoDrawable(marker, 0xff00ffff);
			boundCenterBottom(mPressedDrawable).getBounds().offset(0, InfomationOffset);
			
			setMarker(mNormalDrawable);
		}
		
		private Drawable createInfoDrawable(StampPinOverlay.StampRallyMarker marker,
				int background) {
			
			String name = marker.getTitle();
			int len = name.length();
			int numLines = ((len - 1) / MaxOneLineTextLength) + 1;
			int maxLength = numLines == 1 ? len : MaxOneLineTextLength;
			
			
			//インフォメーションプレートのサイズを調べる
			mPaint.getTextBounds(name, 0, maxLength, mTextBounds);
			int width = mTextBounds.width();
			if(width < MinInfomationWidth)
				width = MinInfomationWidth;
			int height = mTextBounds.height() * numLines;
			
			
			float descent = mPaint.descent();
			Bitmap bitmap = Bitmap.createBitmap(
					width + InformationMargin * 2, 
					(int)(height + InformationMargin * 2 + (numLines - 1) * descent),
					Bitmap.Config.ARGB_4444);
			
			Canvas canvas = new Canvas(bitmap);
			
			//クリッピングパスを作成
			mPath.reset();
			mPath.addRoundRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
					20, 30, Direction.CW);
			canvas.clipPath(mPath);
			
			
			//インフォメーションの背景描画
			mPaint.setColor(background);
			mPaint.setStyle(Style.FILL);
			canvas.drawPaint(mPaint);
			
			mPaint.setARGB(0xff, 0x33, 0x33, 0x33);
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(6);
			canvas.drawRoundRect(new RectF(3, 3, bitmap.getWidth() - 3, bitmap.getHeight() - 3),
					20, 25, mPaint);
			
			//タイトルテキスト描画
			mPaint.setTextSize(InformationTitleTextSize);
			mPaint.setStrokeWidth(1);
			mPaint.setStyle(Style.FILL);
			float ascent = mPaint.ascent();
			
			float top = InformationMargin - ascent - 3;
			
			float left;
			if(numLines == 1) {
				left = (bitmap.getWidth() - mTextBounds.width() - InformationMargin / 2.0f) / 2;
			} else {
				left = InformationMargin / 2.0f;
			}
				
			int start = 0;
			int end = maxLength;
			for(int i = 0;i < numLines;++i) {
				canvas.drawText(name, start, end, left, top, mPaint);
				
				top += mTextBounds.height() + descent;
				
				start = end;
				end = MaxOneLineTextLength * (i+1) + (len - end);
				if(MaxOneLineTextLength * (i+2) < end)
					end = MaxOneLineTextLength * (i+2);
			}
			
			return new BitmapDrawable(bitmap);
		}
	};
	
	private  InfoMarker mItem = null;
	private boolean mTouchDown = false;
	private boolean mTouchOutside = true;
	private boolean mLastHitTest = false;
	
	private final OnClickListener mListener;
	
	
	private final MapView mMap;
	private final Drawable mDrawable;
	private final StampPinOverlay mOverlay;
	
	public PinInfoOverlay(OnClickListener listener,
			StampPinOverlay overlay, MapView map, Drawable drawble) {
		super(drawble);
		
		mListener = listener;
		
		mMap = map;
		mDrawable = drawble;
		mOverlay = overlay;
		
		populate();
	}
	
	@Override protected OverlayItem createItem(int i) {
		return mItem;
	}
	
	@Override public int size() {
		return mItem == null ? 0 : 1;
	}
	
	public void setMarkerInfo(StampPinOverlay.StampRallyMarker marker) {
		mItem = new InfoMarker(marker);
		
		populate();
	}
	
	@Override public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		if(mItem == null)
			return super.onTouchEvent(event, mapView);
		
		switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mTouchDown = true;
				break;
			case MotionEvent.ACTION_UP:
				mTouchDown = false;
				if(mTouchOutside || mLastHitTest || mItem != null) {
					mItem.setMarker(mItem.mNormalDrawable);
				} else {
					mItem = null;
					
					//ここでpopulate()を呼ぶと落ちる時がある
					//populate();
				}
				break;
		}
		
		return super.onTouchEvent(event, mapView);
	}
	
	@Override public boolean onTap(GeoPoint p, MapView mapView) {
		try {
			//よくわからない例外があるので、関係ないけどここで例外を捕捉する
			
			return super.onTap(p, mapView);
		} catch(java.lang.ArrayIndexOutOfBoundsException e) {
			
			List<Overlay> overlays = mMap.getOverlays();
			overlays.remove(this);
			
			PinInfoOverlay overlay = new PinInfoOverlay(mListener, mOverlay, mMap,  mDrawable);
			
			mOverlay.setInfoOverlay(overlay);
			overlays.add(overlay);
			
			mItem = null;
			
			e.printStackTrace();
			return true;
		}
	}
	
	@Override protected boolean onTap(int index) {
		
		if(mItem != null) {
			mListener.onClick(mItem.stampPin);
		}
		
		
		return true;
	}
	
	
	@Override protected boolean hitTest(OverlayItem item, Drawable marker, int hitX, int hitY) {
		boolean hit = super.hitTest(item, marker, hitX, hitY);
		
		if(mTouchDown) {
			mTouchDown = false;
			mTouchOutside = hit;
			
			if(hit)
				mItem.setMarker(mItem.mPressedDrawable);
			else
				mItem = null;
		} 
		
		
		return hit;
	}
	
	@Override public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if(shadow) {
			canvas.save();
			canvas.translate(InfoMarker.InfomationOffset / 2.0f, InfoMarker.InfomationOffset / 2.0f);
		}
		super.draw(canvas, mapView, shadow);
		
		if(shadow) {
			canvas.restore();
		}
	}

}
