package jag.kumamoto.apps.StampRally.Data;

/**
 * 
 * ユーザのこれまでの記録を表すクラス
 * 
 * @author aharisu
 *
 */
public final class UserRecord {
	public int point;
	public int numStamp;
	public int numTotalAnswerdQuize;
	public int numCorrectness;
	public long totalAnswerTime;
	
	public UserRecord(int point, int numStamp, 
			int numTotalAnswerdQuize, int numCorrectness, long totalAnswerTime) {
		this.point = point;
		this.numStamp = numStamp;
		this.numTotalAnswerdQuize = numTotalAnswerdQuize;
		this.numCorrectness = numCorrectness;
		this.totalAnswerTime = totalAnswerTime;
	}

}
