package jag.kumamoto.apps.StampRally.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * クイズデータを表すクラス
 * 
 * @author aharisu
 *
 */
public final class QuizData {
	private static final String QueryURL = "http://kumamotogotochi.appspot.com/quizes?pinId=";
	
	private static final String JsonNameQuizes = "quizes";
	private static final String JsonNameKey = "key";
	private static final String JsonNameID = "id";
	private static final String JsonNamePinKey = "pinKey";
	private static final String JsonNameTitle = "title";
	private static final String JsonNameHTML = "html"; 
	private static final String JsonNameChoices = "options";
	private static final String JsonNamePoint = "point";
	private static final String JsonNameOrder = "order";
	
	
	public final long id;
	public final long pinId;
	public final String title;
	public final String descriptionHTML;
	public final int point;
	public final int order;
	public final QuizChoices choices;
	
	
	private QuizData(long id, long pinId, 
			String title, String html, int point, int order,
			QuizChoices choices) {
		this.id = id;
		this.pinId = pinId;
		this.title = title;
		this.descriptionHTML = html;
		this.point = point;
		this.order = order;
		this.choices = choices;
	}
	
	
	public static QuizData[] decodeJSONObject(JSONObject obj) throws JSONException {
		JSONArray jsonQuizes = obj.getJSONArray(JsonNameQuizes);
		
		int count = jsonQuizes.length();
		QuizData[] quizes = new QuizData[count];
		for(int i = 0;i < count;++i) {
			JSONObject jsonQuiz = jsonQuizes.getJSONObject(i);
			
			final long id = jsonQuiz.getJSONObject(JsonNameKey).getLong(JsonNameID);
			final long pinId = jsonQuiz.getJSONObject(JsonNamePinKey).getLong(JsonNameID);
			final String title = jsonQuiz.getString(JsonNameTitle);
			final String html = jsonQuiz.getString(JsonNameHTML);
			final int point = jsonQuiz.getInt(JsonNamePoint);
			final int order = jsonQuiz.getInt(JsonNameOrder);
			final QuizChoices choices = QuizChoices.decodeJSONArray(jsonQuiz.getJSONArray(JsonNameChoices));
			
			quizes[i] = new QuizData(id, pinId, title, html, point, order, choices);
		}
		
		return quizes;
	}
	
	public static String getQueryURL(long pinId) {
		return new StringBuilder(QueryURL).append(pinId).toString();
	}

}
