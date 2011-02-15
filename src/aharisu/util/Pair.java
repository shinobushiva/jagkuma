package aharisu.util;


/**
 * 
 * 二組の値を表すジェネリクスクラス
 * 
 * @author aharisu
 *
 * @param <V1>
 * @param <V2>
 */
public class Pair<V1, V2> {
	public final V1 v1;
	public final V2 v2;
	
	public Pair(V1 value1, V2 value2) {
		this.v1 = value1;
		this.v2 = value2;
	}

}
