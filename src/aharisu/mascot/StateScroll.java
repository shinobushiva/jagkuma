package aharisu.mascot;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * 
 * マスコットをタッチしてスクロールしたときの動作を行うステートクラス
 * 
 * @author aharisu
 *
 */
public class StateScroll extends MascotState{
	
	private float mCurX;
	private float mCurY;
	
	private final Bitmap[] mImages;
	private int mIndex;
	
	public StateScroll(IMascot mascot) {
		super(mascot);
		
		mImages = new Bitmap[2];
	}
	
	public void move(float dx, float dy) {
		mCurX -= dx;
		mCurY -= dy;
		
		int x = (int)mCurX;
		int y = (int)mCurY;
		
		mMascot.redraw(x, y, 
				x + mImages[mIndex].getWidth(), y + mImages[mIndex].getHeight());
	}
	
	public void setImage(Bitmap image) {
		splitImage(image, mImages, 2);
	}
	
	public boolean isEnable() {
		return mImages[0] != null && mImages[1] != null;
	}
	
	@Override public int getUpdateInterval() {
		return 175;
	}
	
	@Override public boolean isAllowExist() {
		return false;
	}
	
	@Override public void entry(Rect bounds) {
		mIndex = 0;
		
		centering(bounds, mImages[mIndex].getWidth(), mImages[mIndex].getHeight());
		
		mCurX = bounds.left;
		mCurY = bounds.top;
	}
	
	@Override public boolean update() {
		mIndex = (mIndex + 1) % mImages.length;
		
		int x = (int)mCurX;
		int y = (int)mCurY;
		
		mMascot.redraw(x, y, 
				x + mImages[mIndex].getWidth(), y + mImages[mIndex].getHeight());
		
		return true;
	}
	
	@Override public void getBounds(Rect outRect) {
		int x = (int)mCurX;
		int y = (int)mCurY;
		
		outRect.set(x, y, 
				x + mImages[mIndex].getWidth(), y + mImages[mIndex].getHeight());
	}
	
	@Override public Bitmap getImage() {
		return mImages[mIndex];
	}
	

}
