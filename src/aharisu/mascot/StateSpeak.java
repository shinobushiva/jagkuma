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
	
	private float mCurX;
	private float mCurY;
	
	private int mIndex;
	
	private int mDirection = 0;
	private int mRunCount;
	private int mTotalRunCount;
	private float mStepX;
	private float mStepY;
	
	private String mText;
	
	public StateSpeak(IMascot mascot) {
		super(mascot);
	}
	
	/**
	 * @throws IllegalArgumentException 
	 * @param loader
	 */
	public void setLoader(BitmapLoader loader) {
		if(loader.getNumSplitVertical() != 5) {
			throw new IllegalArgumentException("number of vertical split require 5");
		}
		if(loader.getNumSplitHorizontal() != 1) {
			throw new IllegalArgumentException("number of horizontal split require 1");
		}
		
		setBitmapLoader(loader);
	}
	
	public boolean isEnable() {
		return getBitmapLoader() != null;
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
		return getImageAt(mIndex);
	}
	
	@Override public void getBounds(Rect outRect) {
		Bitmap image = getImageAt(mIndex);
		
		outRect.set((int)mCurX, (int)mCurY, 
				(int)mCurX + image.getWidth(), (int)mCurY + image.getHeight());
	}
	
	@Override public int getUpdateInterval() {
		if(mRunCount <= mTotalRunCount) {
			return 150;
		} else {
			return 8000;
		}
	}
	
	@Override public void entry(Rect bounds) {
		super.entry(bounds);
		
		mIndex = 0;
		mRunCount = 0;
		
		Bitmap image = getImageAt(mIndex);
		
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();
		
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
			
			Bitmap image = getImageAt(mIndex);
			
			int left, top, right, bottom;
			
			right = (int)(mCurX + image.getWidth());
			mCurX += mStepX;
			left = (int)mCurX;
			
			top = (int)mCurY;
			mCurY += mStepY;
			bottom = (int)mCurY + image.getHeight();
			
			mMascot.redraw(left, top, right, bottom);
			
			return true;
		} else if(mRunCount == mTotalRunCount) {
			Bitmap image = getImageAt(mIndex);
			mMascot.showText(mText, 
				new Rect((int)mCurX, (int)mCurY, 
						(int)mCurX + image.getWidth(),
						(int)mCurY + image.getHeight()));
			
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
