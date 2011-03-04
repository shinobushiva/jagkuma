package aharisu.mascot;

import jag.kumamoto.apps.gotochi.R;
import aharisu.util.ImageUtill;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
		private final class RawResourceBitmapLoader implements BitmapLoader {
			private final int mResId;
			private final int mNumSplitVertical;
			private final int mNumSplitHorizontal;
			
			public RawResourceBitmapLoader(int resId, int numSplitVertical, int numSplitHorizontal) {
				this.mResId = resId;
				this.mNumSplitVertical = numSplitVertical;
				this.mNumSplitHorizontal = numSplitHorizontal;
			}
			
			@Override public Bitmap getBitmap() {
				return ImageUtill.loadImage(getContext().getResources().
						openRawResource(mResId), 1024, 1024);
			}
			
			@Override public int getNumSplitVertical() {
				return mNumSplitVertical;
			}
			
			@Override public int getNumSplitHorizontal() {
				return mNumSplitHorizontal;
			}
		}

		
		private final Mascot mMascot;
		
		private final GestureDetector mGestureDetector;
		private boolean mIsTouched = false;
		
		private boolean mIsScrolling = false;
		
		public ShowMascotView(Context context) {
			super(context);
			
			mGestureDetector = new GestureDetector(context, this);
			mGestureDetector.setOnDoubleTapListener(this);
			mMascot = new Mascot(this);
			
			initializeMascotState();
		}
		
		
		private void initializeMascotState() {
			Resources res = getContext().getResources();
			
			//ランダム歩行の基本状態追加
			mMascot.addBasicState(new StateRandomWalk(mMascot, 
					ImageUtill.loadImage(res.openRawResource(R.raw.kumamon), 1024, 1024)));
			
			//ダブルタップのときのこけるアニメーション
			StateRepetition falling = new StateRepetition(mMascot, UserInteractionState.Type.DoubleTap, 
					new RawResourceBitmapLoader(R.raw.koke, 3, 1));
			//一つ導入画像がある
			falling.setNumHeaderFrame(1);
			//導入画像を以外を3回リピートする
			falling.setNumRepetition(3);
			mMascot.addUserInteractionState(falling);
			
			//テキスト表示状態用の画像を設定
			mMascot.setSpeakStateBitmapLoader(new RawResourceBitmapLoader(R.raw.speak, 5, 1));
			
			//スクロール中状態用の画像を設定
			mMascot.setScrollStateBitmapLoader(new RawResourceBitmapLoader(R.raw.scroll, 2, 1));
			
			//入浴中状態を設定	
			StateTimeZoneRepetition bathing = new StateTimeZoneRepetition(mMascot, TimeZoneState.Type.Evening,
					new RawResourceBitmapLoader(R.raw.ofuro, 2, 1),
					Mascot.Level.Middle, Mascot.Level.Low);
			bathing.setNumRepetition(-1);
			mMascot.addTimeZoneState(bathing);
			//昼・夜は入浴状態に移る確率は低い
			mMascot.addTimeZoneState(bathing.copy(TimeZoneState.Type.Daytime,
					Mascot.Level.Low, Mascot.Level.Middle));
			mMascot.addTimeZoneState(bathing.copy(TimeZoneState.Type.Night,
					Mascot.Level.Low, Mascot.Level.Low));
			
			//睡眠状態を設定
			StateTimeZoneRepetition sleeping = new StateTimeZoneRepetition(mMascot, TimeZoneState.Type.Night,
					new RawResourceBitmapLoader(R.raw.sleeping, 3, 1),
					Mascot.Level.Middle, Mascot.Level.Low);
			sleeping.setNumRepetition(-1);
			mMascot.addTimeZoneState(sleeping);
			//昼は睡眠状態に移る確率は低い
			mMascot.addTimeZoneState(sleeping.copy(TimeZoneState.Type.Daytime,
					Mascot.Level.Low, Mascot.Level.Middle));
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
	
	public void start() {
		mShowMascotView.start();
	}
	
	public void stop() {
		mShowMascotView.stop();
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
