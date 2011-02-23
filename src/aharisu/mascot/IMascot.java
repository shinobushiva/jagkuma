package aharisu.mascot;

import android.graphics.Rect;

/**
 * 
 * ステートクラスからマスコットクラスのメソッドを呼ぶためのインタフェース
 * 
 * @author aharisu
 *
 */
public interface IMascot {
	public void redraw(int left, int top, int right, int bottom);
	public int getViewWidth();
	public int getViewHeight();
	public void stateChange();
	public void showText(String text, Rect mascotBounds);
	public void hideText();
}
