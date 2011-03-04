package aharisu.mascot;

import aharisu.util.ImageUtill;
import android.content.Context;
import android.graphics.Bitmap;

/***
 * ビットマップの遅延読み込みを行うためのローダインタフェース
 * 
 * @author aharisu
 *
 */
public interface BitmapLoader {
	public Bitmap getBitmap();
	public int getNumSplitVertical();
	public int getNumSplitHorizontal();
	
	public final static class RawResourceBitmapLoader implements BitmapLoader {
		private final Context mContext;
		private final int mResId;
		private final int mNumSplitVertical;
		private final int mNumSplitHorizontal;
		
		public RawResourceBitmapLoader(Context context, int resId, int numSplitVertical, int numSplitHorizontal) {
			this.mContext = context;
			this.mResId = resId;
			this.mNumSplitVertical = numSplitVertical;
			this.mNumSplitHorizontal = numSplitHorizontal;
		}
		
		@Override public Bitmap getBitmap() {
			return ImageUtill.loadImage(mContext.getResources().
					openRawResource(mResId), 1024, 1024);
		}
		
		@Override public int getNumSplitVertical() {
			return mNumSplitVertical;
		}
		
		@Override public int getNumSplitHorizontal() {
			return mNumSplitHorizontal;
		}
	}
	
}
