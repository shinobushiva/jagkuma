package aharisu.mascot;

import aharisu.mascot.Mascot.Level;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class StateTimeZoneRepetition extends TimeZoneState{
	private static final int HeaderInterval = 700;
	private static final int RepetitionInterval = 800;
	private static final int FotterInterval = 750;
	
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
			BitmapLoader loader) {
		super(mascot, timeZoneType);
		
		setBitmapLoader(loader);
		
		mEntryPriority = Level.Middle;
		mExistProbability = Level.Middle;
	}
	
	public StateTimeZoneRepetition(IMascot mascot, 
			TimeZoneState.Type timeZoneType,
			BitmapLoader loader,
			Level entryPriority, Level existProbability) {
		super(mascot, timeZoneType);
		
		setBitmapLoader(loader);
		
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
	
	public StateTimeZoneRepetition copy(TimeZoneState.Type timeZoneType) {
		return copy(timeZoneType, mEntryPriority, mExistProbability);
	}
	
	public StateTimeZoneRepetition copy(TimeZoneState.Type timeZoneType, 
			Level entryPriority, Level existProbability) {
		StateTimeZoneRepetition state = new StateTimeZoneRepetition(
				mMascot, timeZoneType, getBitmapLoader(), entryPriority, existProbability);
		
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
		Bitmap image = getImageAt(mIndex);
		
		outRect.set(mCurX, mCurY, 
				mCurX + image.getWidth(), mCurY + image.getHeight());
	}
	
	@Override public Bitmap getImage() {
		return getImageAt(mIndex);
	}
	
	@Override public int getUpdateInterval() {
		if(mIndex < mNumHeaderFrame) {
			//始まり部分
			return HeaderInterval;
		} else if(mIndex >= getImageCount() - mNumFotterFrame) {
			//終わり部分
			return FotterInterval;
		} else {
			//リピート部分
			return RepetitionInterval;
		}
	}
	
	@Override public void entry(Rect bounds) {
		super.entry(bounds);
		
		mIndex = -1;
		mRepeatCount = 0;
		
		
		Bitmap img=  getImageAt(0);
		centering(bounds, img.getWidth(), img.getHeight());
		
		mCurX = bounds.left;
		mCurY = bounds.top;
	}
	
	@Override public boolean update() {
		int length = getImageCount();
		
		if(mIndex < mNumHeaderFrame) {
			//始まり部分
			++mIndex;
		} else if(mIndex >= length - mNumFotterFrame) {
			//終わり部分
			++mIndex;
		} else {
			//リピート部分
			
			++mIndex;
			if(mIndex >= length - mNumFotterFrame) {
				++mRepeatCount;
				
				if(mNumRepetition < 0 || mRepeatCount < mNumRepetition) {
					//リピートを続ける
					mIndex = mNumHeaderFrame;
				}
			}
		}
		
		if(length <= mIndex) {
			//アニメーション終了
			mIndex = length - 1;
			//一連の動作が終わったので状態変更
			mMascot.stateChange();
			
			return false;
		} else {
			//再描画
			Bitmap img = getImageAt(mIndex);
			
			mMascot.redraw(mCurX, mCurY, mCurX + img.getWidth(), mCurY + img.getHeight());
			
			return true;
		}
		
	}

}
