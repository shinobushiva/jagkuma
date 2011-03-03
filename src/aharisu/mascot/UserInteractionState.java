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
	public static enum Type{
		LongPress,
		SingleTap,
		DoubleTap,
		Fling,
	}
	
	private final Type mActionType;
	private final Bitmap[] mImages;
	
	
	public UserInteractionState(IMascot mascot, Type actionType, 
			Bitmap image, int numSplit) {
		super(mascot);
		
		mActionType = actionType;
		
		mImages = new Bitmap[numSplit];
		
		splitImage(image, mImages, numSplit);
	}
	
	public Type getActionType() {
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
