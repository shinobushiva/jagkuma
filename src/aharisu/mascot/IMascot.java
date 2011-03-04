package aharisu.mascot;

import android.graphics.Rect;

/**
 * 
 * ステートクラスからマスコットクラスのメソッドを呼ぶためのインタフェース
 * 
 * @author aharisu
 *
 */
public abstract class IMascot {
	abstract void redraw(int left, int top, int right, int bottom);
	abstract int getViewWidth();
	abstract int getViewHeight();
	abstract void stateChange();
	abstract void showText(String text, Rect mascotBounds);
	abstract void hideText();
}
