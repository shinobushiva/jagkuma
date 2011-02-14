package jag.kumamoto.apps.StampRally.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * クイズの選択肢群を表すクラス
 * 
 * @author aharisu
 *
 */
public final class QuizChoices {
	private static final String JsonNameKey = "key";
	private static final String JsonNameID = "id";
	private static final String JsonNameType = "type";
	private static final String JsonNameAltTypeText = "text";
	private static final String JsonNameCorrectAnswer = "correctness";
	
	
	public static final class Choice {
		
		public final long id;
		public final int type;
		public final String altTypeText;
		public final boolean isCorrectAnswer;
		
		private Choice(long id, int type, String text, boolean isCorrectAnswer) {
			this.id = id;
			this.type = type;
			this.altTypeText = text;
			this.isCorrectAnswer = isCorrectAnswer;
		}
	}
	
	private final Choice[] mChoices;
	
	private QuizChoices(Choice[] choices) {
		this.mChoices = choices;
	}
	
	public static QuizChoices decodeJSONArray(JSONArray ary) throws JSONException {
		int count = ary.length();
		final Choice[] choices = new Choice[count];
		
		for(int i = 0;i < count;++i) {
			JSONObject jsonChoice = ary.getJSONObject(i);
			
			long id = jsonChoice.getJSONObject(JsonNameKey).getLong(JsonNameID);
			int type = jsonChoice.getInt(JsonNameType);
			String text = jsonChoice.getString(JsonNameAltTypeText);
			boolean isCorrectAnswer = jsonChoice.getInt(JsonNameCorrectAnswer) == 1;
			
			choices[i] = new Choice(id, type, text, isCorrectAnswer);
		}
		
		return new QuizChoices(choices);
	}
	
	public int getCount() {
		return mChoices.length;
	}
	
	public Choice getChoice(int index) {
		return mChoices[index];
	}
	
	public boolean isChoiseSingle() {
		boolean hasCorrectAnswer = false;
		
		for(int i = 0;i < mChoices.length;++i) {
			if(mChoices[i].isCorrectAnswer && hasCorrectAnswer) {
				
				//正解が複数ある
				return false;
			}
			
			hasCorrectAnswer = mChoices[i].isCorrectAnswer;
		}
		
		//正解が一つしかない
		return true;
	}
	
}
