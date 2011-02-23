package aharisu.mascot;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * 
 * 一連のパラパラ漫画風アニメーションをするステートクラス
 * 
 * @author aharisu
 *
 */
public class StateRepetition extends UserInteractionState{
	
	private static final int HeaderInterval = 700;
	private static final int RepetitionInterval = 500;
	private static final int FotterInterval = 750;
	
	
	private int mNumHeaderFrame = 0;
	private int mNumFotterFrame = 0;
	private int mNumRepetition = 1;
	
	private int mImageIndex;
	private int mRepeatCount;
	
	private int mCurX = 0;
	private int mCurY = 0;
	
	public StateRepetition(IMascot mascot, Mascot.ActionType actionType,
			Bitmap image, int numSplit) {
		super(mascot, actionType, image, numSplit);
	}
	
	public void setNumHeaderFrame(int num) {
		mNumHeaderFrame = num;
	}
	
	public void setNumFotterFrame(int num) {
		mNumFotterFrame = num;
	}
	
	public void setNumRepetition(int num) {
		mNumRepetition = num;
	}
	
	@Override public void getBounds(Rect outRect) {
		Bitmap img = getImageAt(mImageIndex);
		outRect.set(mCurX, mCurY, mCurX + img.getWidth(), mCurY + img.getHeight());
	}
	
	@Override public Bitmap getImage() {
		return getImageAt(mImageIndex);
	}
	
	@Override public int getUpdateInterval() {
		if(mImageIndex < mNumHeaderFrame) {
			//始まり部分
			return HeaderInterval;
		} else if(mImageIndex >= getImageCount() - mNumFotterFrame) {
			//終わり部分
			return FotterInterval;
		} else {
			//リピート部分
			return RepetitionInterval;
		}
	}
	
	@Override public void entry(Rect bounds) {
		mImageIndex = -1;
		mRepeatCount = 0;
		
		
		Bitmap img=  getImageAt(0);
		centering(bounds, img.getWidth(), img.getHeight());
		
		mCurX = bounds.left;
		mCurY = bounds.top;
	}
	
	@Override public boolean update() {
		int imageCount = getImageCount();
		
		if(mImageIndex < mNumHeaderFrame) {
			//始まり部分
			++mImageIndex;
		} else if(mImageIndex >= imageCount - mNumFotterFrame) {
			//終わり部分
			++mImageIndex;
		} else {
			//リピート部分
			
			++mImageIndex;
			if(mImageIndex >= imageCount - mNumFotterFrame) {
				++mRepeatCount;
				
				if(mRepeatCount < mNumRepetition) {
					//リピートを続ける
					mImageIndex = mNumHeaderFrame;
				}
			}
		}
		
		if(imageCount <= mImageIndex) {
			//アニメーション終了
			mImageIndex = imageCount - 1;
			//一連の動作が終わったので状態変更
			mMascot.stateChange();
			
			return false;
		} else {
			//再描画
			Bitmap img = getImageAt(mImageIndex);
			
			mMascot.redraw(mCurX, mCurY, mCurX + img.getWidth(), mCurY + img.getHeight());
			
			return true;
		}
		
	}

}
