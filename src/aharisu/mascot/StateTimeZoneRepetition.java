package aharisu.mascot;

import aharisu.mascot.Mascot.Level;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class StateTimeZoneRepetition extends TimeZoneState{
	private static final int HeaderInterval = 700;
	private static final int RepetitionInterval = 800;
	private static final int FotterInterval = 750;
	
	private final Bitmap[] mImages;
	private int mIndex;
	private int mRepeatCount = 0;
	
	private int mCurX = 0;
	private int mCurY = 0;
	
	private int mNumHeaderFrame = 0;
	private int mNumFotterFrame = 0;
	private int mNumRepetition = 1;
	
	private final Level mEntryPriority;
	private final Level mExistProbability;
	
	public StateTimeZoneRepetition(IMascot mascot, 
			TimeZoneState.Type timeZoneType,
			Bitmap image, int numSplit) {
		super(mascot, timeZoneType);
		
		this.mImages = new Bitmap[numSplit];
		
		splitImage(image, mImages, numSplit);
		
		mEntryPriority = Level.Middle;
		mExistProbability = Level.Middle;
	}
	
	public StateTimeZoneRepetition(IMascot mascot, 
			TimeZoneState.Type timeZoneType,
			Bitmap image, int numSplit,
			Level entryPriority, Level existProbability) {
		super(mascot, timeZoneType);
		
		this.mImages = new Bitmap[numSplit];
		
		splitImage(image, mImages, numSplit);
		
		mEntryPriority = entryPriority;
		mExistProbability = existProbability;
	}
	
	private StateTimeZoneRepetition(IMascot mascot, 
			TimeZoneState.Type timeZoneType,
			Bitmap[] images,
			Level entryPriority, Level existProbability) {
		super(mascot, timeZoneType);
		
		this.mImages = images;
		
		mEntryPriority = entryPriority;
		mExistProbability = existProbability;
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
	
	public StateTimeZoneRepetition copySharedImages(TimeZoneState.Type timeZoneType) {
		return copySharedImages(timeZoneType, mEntryPriority, mExistProbability);
	}
	
	public StateTimeZoneRepetition copySharedImages(TimeZoneState.Type timeZoneType, 
			Level entryPriority, Level existProbability) {
		StateTimeZoneRepetition state = new StateTimeZoneRepetition(
				mMascot, timeZoneType, mImages, entryPriority, existProbability);
		
		state.mNumHeaderFrame = this.mNumHeaderFrame;
		state.mNumFotterFrame = this.mNumFotterFrame;
		state.mNumRepetition = this.mNumRepetition;
		
		return state;
	}
			
	
	@Override public Level getEntryPriority() {
		return mEntryPriority;
	}
	
	@Override public Level getExistProbability() {
		return mExistProbability;
	}
	
	@Override public void getBounds(Rect outRect) {
		outRect.set(mCurX, mCurY, 
				mCurX + mImages[mIndex].getWidth(), mCurY + mImages[mIndex].getHeight());
	}
	
	@Override public Bitmap getImage() {
		return mImages[mIndex];
	}
	
	@Override public int getUpdateInterval() {
		if(mIndex < mNumHeaderFrame) {
			//始まり部分
			return HeaderInterval;
		} else if(mIndex >= mImages.length - mNumFotterFrame) {
			//終わり部分
			return FotterInterval;
		} else {
			//リピート部分
			return RepetitionInterval;
		}
	}
	
	@Override public void entry(Rect bounds) {
		mIndex = -1;
		mRepeatCount = 0;
		
		
		Bitmap img=  mImages[0];
		centering(bounds, img.getWidth(), img.getHeight());
		
		mCurX = bounds.left;
		mCurY = bounds.top;
	}
	
	@Override public boolean update() {
		
		if(mIndex < mNumHeaderFrame) {
			//始まり部分
			++mIndex;
		} else if(mIndex >= mImages.length - mNumFotterFrame) {
			//終わり部分
			++mIndex;
		} else {
			//リピート部分
			
			++mIndex;
			if(mIndex >= mImages.length - mNumFotterFrame) {
				++mRepeatCount;
				
				if(mNumRepetition < 0 || mRepeatCount < mNumRepetition) {
					//リピートを続ける
					mIndex = mNumHeaderFrame;
				}
			}
		}
		
		if(mImages.length <= mIndex) {
			//アニメーション終了
			mIndex = mImages.length - 1;
			//一連の動作が終わったので状態変更
			mMascot.stateChange();
			
			return false;
		} else {
			//再描画
			Bitmap img = mImages[mIndex];
			
			mMascot.redraw(mCurX, mCurY, mCurX + img.getWidth(), mCurY + img.getHeight());
			
			return true;
		}
		
	}

}
