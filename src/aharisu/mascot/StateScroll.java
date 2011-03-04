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
	
	private int mIndex;
	
	public StateScroll(IMascot mascot) {
		super(mascot);
	}
	
	public void move(float dx, float dy) {
		mCurX -= dx;
		mCurY -= dy;
		
		int x = (int)mCurX;
		int y = (int)mCurY;
		
		Bitmap image = getImageAt(mIndex);
		mMascot.redraw(x, y, x + image.getWidth(), y + image.getHeight());
	}
	
	public void setLoader(BitmapLoader loader) {
		if(loader.getNumSplitHorizontal() != 1) {
			throw new IllegalArgumentException("number of horizontal split require 1");
		}
		
		setBitmapLoader(loader);
	}
	
	public boolean isEnable() {
		return getBitmapLoader() != null;
	}
	
	@Override public int getUpdateInterval() {
		return 175;
	}
	
	@Override public boolean isAllowExist() {
		return false;
	}
	
	@Override public void entry(Rect bounds) {
		super.entry(bounds);
		
		mIndex = 0;
		
		Bitmap image = getImageAt(mIndex);
		centering(bounds, image.getWidth(), image.getHeight());
		
		mCurX = bounds.left;
		mCurY = bounds.top;
	}
	
	@Override public boolean update() {
		mIndex = (mIndex + 1) % getImageCount();
		
		int x = (int)mCurX;
		int y = (int)mCurY;
		
		Bitmap image = getImageAt(mIndex);
		mMascot.redraw(x, y, x + image.getWidth(), y + image.getHeight());
		
		return true;
	}
	
	@Override public void getBounds(Rect outRect) {
		int x = (int)mCurX;
		int y = (int)mCurY;
		
		Bitmap image = getImageAt(mIndex);
		outRect.set(x, y, x + image.getWidth(), y + image.getHeight());
	}
	
	@Override public Bitmap getImage() {
		return getImageAt(mIndex);
	}
	

}
