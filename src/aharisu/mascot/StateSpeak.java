package aharisu.mascot;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * 
 * マスコットから吹き出しを出してテキストを表示するステートクラス
 * 
 * @author aharisu
 *
 */
public class StateSpeak extends MascotState {
	private static final int DirectionDown = 0;
	private static final int DirectionLeft = 1;
	
	private static final float WalkStep = 2.5f;
	
	private boolean mEnable = false;
	
	private float mCurX;
	private float mCurY;
	
	private final Bitmap[] mImages;
	private int mIndex;
	
	private int mDirection = 0;
	private int mRunCount;
	private int mTotalRunCount;
	private float mStepX;
	private float mStepY;
	
	private String mText;
	
	public StateSpeak(IMascot mascot) {
		super(mascot);
		
		mImages = new Bitmap[5];
	}
	
	public void setImage(Bitmap image) {
		splitImage(image, mImages, 5);
		mEnable = true;
	}
	
	public boolean isEnable() {
		return mEnable;
	}
	
	public void setText(String text) {
		mText = text;
	}
	
	@Override public boolean isAllowExist() {
		return false;
	}
	
	@Override public boolean isAllowInterrupt() {
		return mRunCount >= mTotalRunCount;
	}
	
	@Override public Bitmap getImage() {
		return mImages[mIndex];
	}
	
	@Override public void getBounds(Rect outRect) {
		outRect.set((int)mCurX, (int)mCurY, 
				(int)mCurX + mImages[mIndex].getWidth(), (int)mCurY + mImages[mIndex].getHeight());
	}
	
	@Override public int getUpdateInterval() {
		if(mRunCount <= mTotalRunCount) {
			return 150;
		} else {
			return 8000;
		}
	}
	
	@Override public void entry(Rect bounds) {
		mIndex = 0;
		mRunCount = 0;
		
		int imgWidth = mImages[mIndex].getWidth();
		int imgHeight = mImages[mIndex].getHeight();
		
		int viewHeight = mMascot.getViewHeight();
		
		centering(bounds, imgWidth, imgHeight);
		
		mCurX = bounds.left;
		mCurY = bounds.top;
		
		float distanceX = mCurX;
		float distanceY = viewHeight - (mCurY + imgHeight);
		if(distanceX < distanceY) {
			mStepY = imgHeight / (float)WalkStep;
			float tmp = distanceY / mStepY;
			mTotalRunCount = (int)tmp;
			mStepX = distanceX / tmp;
		} else {
			mStepX = imgWidth / (float)WalkStep;
			float tmp = distanceX / mStepX;
			mTotalRunCount = (int)tmp;
			mStepY = distanceY / tmp;
		}
		mStepX *= -1;
		
		if(Math.atan2(distanceY, distanceX) * 180 / Math.PI < 45) {
			mDirection = DirectionLeft;
		} else {
			mDirection = DirectionDown;
		}
	}
	
	@Override public boolean update() {
		if(mRunCount < mTotalRunCount) {
			++mRunCount;
		
			int direcOrigin = mDirection * 2 + 1;
			mIndex = (((mIndex - direcOrigin) + 1) % 2) + direcOrigin;
			
			int left, top, right, bottom;
			
			right = (int)(mCurX + mImages[mIndex].getWidth());
			mCurX += mStepX;
			left = (int)mCurX;
			
			top = (int)mCurY;
			mCurY += mStepY;
			bottom = (int)mCurY + mImages[mIndex].getHeight();
			
			mMascot.redraw(left, top, right, bottom);
			
			return true;
		} else if(mRunCount == mTotalRunCount) {
			mMascot.showText(mText, 
				new Rect((int)mCurX, (int)mCurY, 
						(int)mCurX + mImages[mIndex].getWidth(),
						(int)mCurY + mImages[mIndex].getHeight()));
			
			++mRunCount;
			mIndex = 0;
			return true;
		} else {
			mMascot.hideText();
			
			mMascot.stateChange();
			return false;
		}
	}
	
	@Override public void exist() {
		mMascot.hideText();
	}
	

}
