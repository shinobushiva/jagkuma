package aharisu.mascot;

/**
 * 
 * マスコットに対するイベントを表すデータクラス
 * 
 * @author aharisu
 *
 */
public class MascotEvent {
	public enum Type {
		Text,
		Tweet,
	};
	
	public final Type type;
	public final String text;
	
	public MascotEvent(Type type, String text) {
		this.type = type;
		this.text = text;
	}

}
