package aharisu.mascot;

import jag.kumamoto.apps.gotochi.R;
import aharisu.util.ImageUtill;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;


/**
 * 
 * Mascotクラスのインスタンスを表示するためのビュークラス
 * 
 * @author aharisu
 *
 */
public class MascotView extends View implements OnGestureListener, OnDoubleTapListener{
	
	private final Mascot mMascot;
	
	private final GestureDetector mGestureDetector;
	private boolean mIsTouched = false;
	
	private boolean mIsScrolling = false;
	
	public MascotView(Context context) {
		super(context);
		
		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setOnDoubleTapListener(this);
		mMascot = new Mascot(this);
		
		initializeMascotState();
	}
	
	public MascotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setOnDoubleTapListener(this);
		mMascot = new Mascot(this); 
		
		initializeMascotState();
	}
	
	public MascotView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
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
		StateRepetition falling = new StateRepetition(mMascot, Mascot.ActionType.DoubleTap, 
				ImageUtill.loadImage(res.openRawResource(R.raw.koke), 1024, 1024) , 3);
		//一つ導入画像がある
		falling.setNumHeaderFrame(1);
		//導入画像を以外を3回リピートする
		falling.setNumRepetition(3);
		mMascot.addUserInteractionState(falling);
		
	}
	
	
	@Override protected void onFinishInflate() {
		super.onFinishInflate();
		
		mMascot.start();
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
		mIsScrolling = false;
		
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
	

}
