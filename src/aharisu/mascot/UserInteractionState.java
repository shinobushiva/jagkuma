package aharisu.mascot;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

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
		
		splitSingleLineImage(image, numSplit);
	}
	
	private void splitSingleLineImage(Bitmap image, int numSplit) {
		int width = image.getWidth() / numSplit;
		int height = image.getHeight();
		
		Rect srcRect = new Rect(0, 0, 0, height);
		Rect destRect = new Rect(0, 0, width, height);
		for(int i = 0;i < numSplit;++i) {
			Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
			Canvas canvas = new Canvas(img);
			
			srcRect.left = width * i;
			srcRect.right = srcRect.left + width;
			
			canvas.drawBitmap(image, srcRect, destRect, null);
			
			this.mImages[i] = img;
		}
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
