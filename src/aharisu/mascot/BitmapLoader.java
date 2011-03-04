package aharisu.mascot;

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
}
