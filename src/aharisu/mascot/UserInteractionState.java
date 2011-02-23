package aharisu.mascot;

import android.graphics.Bitmap;

/**
 * 
 * ユーザインタラクションに対応する抽象ステートクラス
 * 
 * @author aharisu
 *
 */
public abstract class UserInteractionState extends MascotState{
	
	private final Mascot.ActionType mActionType;
	private final Bitmap[] mImages;
	
	
	public UserInteractionState(IMascot mascot, Mascot.ActionType actionType, 
			Bitmap image, int numSplit) {
		super(mascot);
		
		mActionType = actionType;
		
		mImages = new Bitmap[numSplit];
		
		splitImage(image, mImages, numSplit);
	}
	
	public Mascot.ActionType getActionType() {
		return mActionType;
	}
	
	protected int getImageCount() {
		return mImages.length;
	}
	
	protected Bitmap getImageAt(int index) {
		if(index < 0 || index >= mImages.length) {
			throw new IndexOutOfBoundsException();
		}
		
		return mImages[index];
	}
	
	@Override public boolean isAllowExist() {
		return false;
	}
	
	@Override public boolean isAllowInterrupt() {
		return false;
	}

}
