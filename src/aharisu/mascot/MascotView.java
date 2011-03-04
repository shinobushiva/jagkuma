package aharisu.mascot;

import jag.kumamoto.apps.gotochi.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;


/**
 * 
 * Mascotクラスのインスタンスを表示するためのビュークラス
 * 
 * @author aharisu
 *
 */
@SuppressWarnings("deprecation") 
public class MascotView extends FrameLayout {
	private static final int TextBalloonViewId = 10;
	
	final class ShowMascotView extends View 
			implements OnGestureListener, OnDoubleTapListener{
		
		private final Mascot mMascot;
		
		private final GestureDetector mGestureDetector;
		private boolean mIsTouched = false;
		
		private boolean mIsScrolling = false;
		
		public ShowMascotView(Context context) {
			super(context);
			
			mGestureDetector = new GestureDetector(context, this);
			mGestureDetector.setOnDoubleTapListener(this);
			mMascot = new Mascot(this);
		}
		
		
		@Override public boolean onTouchEvent(MotionEvent event) {
			boolean isHandling = false;
			int action = event.getAction();
			
			if(action == MotionEvent.ACTION_DOWN &&
						mMascot.hitTest((int)event.getX(), (int)event.getY())) {
					mIsTouched =  true;
			}
			
			if(mIsTouched && mGestureDetector.onTouchEvent(event)) {
				isHandling = true;
			}
			
			if(action == MotionEvent.ACTION_UP ||
						action == MotionEvent.ACTION_CANCEL) {
				mIsTouched = false;
				
				if(mIsScrolling) {
					mIsScrolling = false;
					
					mMascot.onScrollEnd();
				}
			}
			
			return isHandling ? true :  super.onTouchEvent(event);
		}
		
		
		@Override public boolean onDown(MotionEvent e) {
			return true;
		}
		
		@Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if(mIsScrolling) {
				mIsScrolling = false;
				
				mMascot.onScrollEnd();
			}
			
			mMascot.onFling(velocityX, velocityY);
			return true;
		}
		
		@Override public void onLongPress(MotionEvent e) {
			mMascot.onLongPress();
		}
		
		@Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			mIsScrolling = true;
			
			mMascot.onScroll(distanceX, distanceY);
			return true;
		}
		
		@Override public void onShowPress(MotionEvent e) {
		}
		
		@Override public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
		@Override public boolean onDoubleTap(MotionEvent e) {
			mMascot.onDoubleTap();
			return false;
		}
		
		@Override public boolean onDoubleTapEvent(MotionEvent e) {
			return true;
		}
		
		@Override public boolean onSingleTapConfirmed(MotionEvent e) {
			mMascot.onSingleTap();
			return true;
		}
		
		
		@Override public void draw(Canvas canvas) {
			super.draw(canvas);
			
			mMascot.draw(canvas);
		}
		
		
		/*
		 * Mascotクラスから呼ばれるためのメソッド
		 */
		
		void showText(String text, Rect mascotBounds) {
			MascotView.this.showText(text, mascotBounds);
		}
		
		void hideText() {
			MascotView.this.hideText();
		}
		
		void redraw(int left, int top, int right, int bottom) {
			this.invalidate(left, top, right, bottom);
			MascotView.this.mLayout.invalidate();
		}
		
		/*
		 * MascotViewクラスから呼ばれるためのメソッド
		 */
		
		private void start() {
			mMascot.start();
		}
		
		private void stop() {
			mMascot.stop();
		}
	}
	
	private final ShowMascotView mShowMascotView;
	private final AbsoluteLayout mLayout;
	
	public MascotView(Context context) {
		super(context);
		
		mShowMascotView = new ShowMascotView(context);
		mLayout = new AbsoluteLayout(context);
		
		initialize();
	}
	
	public MascotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mShowMascotView = new ShowMascotView(context);
		mLayout = new AbsoluteLayout(context);
		
		initialize();
	}
	
	public MascotView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mShowMascotView = new ShowMascotView(context);
		mLayout = new AbsoluteLayout(context);
		
		initialize();
	}
	
	
	private void initialize() {
		this.addView(mShowMascotView,
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
		
		this.addView(mLayout,
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
	}		
	
	/**
	 * @throws IllegalStateException BasicStateを一つも持っていないとき
	 */
	public void start() {
		mShowMascotView.start();
	}
	
	public void stop() {
		mShowMascotView.stop();
	}
	
	public IMascot getMascot() {
		return mShowMascotView.mMascot;
	}
	
	public void addBasicState(MascotState state) {
		mShowMascotView.mMascot.addBasicState(state);
	}
	
	public void addUserInteractionState(UserInteractionState state) {
		mShowMascotView.mMascot.addUserInteractionState(state);
	}
	
	public void addTimeZoneState(TimeZoneState state) {
		mShowMascotView.mMascot.addTimeZoneState(state);
	}
	
	public void addMascotEvent(MascotEvent event) {
		mShowMascotView.mMascot.addEvent(event);
	}
	
	/**
	 * 画像は横一列で５パーツなければならない
	 * @param loader
	 * @exception IllegalArgumentException 画像が既定のフォーマットを満たしていなければ例外
	 */
	public void setSpeakStateBitmapLoader(BitmapLoader loader) {
		mShowMascotView.mMascot.setSpeakStateBitmapLoader(loader);
	}
	
	/**
	 * 画像は横一列でなければならない
	 * @param loader
	 * @exception IllegalArgumentException 画像が既定のフォーマットを満たしていなければ例外
	 */
	public void setScrollStateBitmapLoader(BitmapLoader loader) {
		mShowMascotView.mMascot.setScrollStateBitmapLoader(loader);
	}
	
	
	/*
	 * ShowMascotViewから呼ばれるメソッド
	 */
	
	private void showText(String text, Rect bounds) {
		int layoutWidth = mLayout.getWidth();
		
		TextBalloonView textBalloon = (TextBalloonView)findViewById(TextBalloonViewId);
		AbsoluteLayout.LayoutParams params;
		boolean isRecycle;
		if(textBalloon == null) {
			textBalloon = (TextBalloonView)(
				(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.mascot_text_balloon, null);
			
			params = new AbsoluteLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT,
					bounds.left, bounds.top);
			textBalloon.setLayoutParams(params);
			textBalloon.setMaxWidth((int)(layoutWidth * 0.9f));
			textBalloon.setId(TextBalloonViewId);
			
			isRecycle = false;
		} else {
			params = (AbsoluteLayout.LayoutParams)textBalloon.getLayoutParams();
			params.x = bounds.left;
			params.y = bounds.top;
			
			isRecycle = true;
		}
		
		textBalloon.setText(text);
		textBalloon.setMascotPosition(bounds);
		
		textBalloon.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		params.y -= textBalloon.getMeasuredHeight();
		
		int balloonRight = params.x + textBalloon.getMeasuredWidth();
		if(layoutWidth < balloonRight) {
			params.x -= balloonRight - layoutWidth;
		}
		
		if(isRecycle) {
			invalidate();
		} else {
			mLayout.addView(textBalloon);
		}
	}
	
	private void hideText() {
		mLayout.removeView(mLayout.findViewById(TextBalloonViewId));
	}
	

}
