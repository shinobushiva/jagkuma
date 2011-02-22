package aharisu.mascot;

/**
 * 
 * ステートクラスからマスコットクラスのメソッドを呼ぶためのインタフェース
 * 
 * @author aharisu
 *
 */
public interface IMascot {
	public void invalidate(int left, int top, int right, int bottom);
	public int getViewWidth();
	public int getViewHeight();
	public void stateChange();
}
