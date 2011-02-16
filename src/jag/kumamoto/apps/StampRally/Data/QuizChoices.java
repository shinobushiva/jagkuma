package jag.kumamoto.apps.StampRally.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * クイズの選択肢群を表すクラス
 * 
 * @author aharisu
 *
 */
public final class QuizChoices implements Parcelable{
	private static final String JsonNameKey = "key";
	private static final String JsonNameID = "id";
	private static final String JsonNameType = "type";
	private static final String JsonNameAltTypeText = "text";
	private static final String JsonNameCorrectAnswer = "correctness";
	
	
	public static final class Choice implements Parcelable{
		
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
		
		
		
		/*
		 * 以降 Parcelableクラスの実装
		 */
		
		@Override public int describeContents() {
			return 0;
		}
		
		@Override public void writeToParcel(Parcel dest, int flags) {
			dest.writeLong(id);
			dest.writeInt(type);
			dest.writeString(altTypeText);
			dest.writeInt(isCorrectAnswer ? 1 : 0);
		}
		
		public static final Parcelable.Creator<Choice> CREATOR = new Parcelable.Creator<Choice>() {
			
			@Override public Choice[] newArray(int size) {
				return new Choice[size];
			}
			
			@Override public Choice createFromParcel(Parcel source) {
				return new Choice(
						source.readLong(),
						source.readInt(),
						source.readString(),
						source.readInt() == 1);
			}
		};
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
	
	
	
	/*
	 * 以降 Parcelableクラスの実装
	 */
	
	@Override public int describeContents() {
		return 0;
	}
	
	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelableArray(mChoices, 0);
	}
	
	public static final Parcelable.Creator<QuizChoices> CREATOR = new Parcelable.Creator<QuizChoices>() {
		
		@Override public QuizChoices[] newArray(int size) {
			return new QuizChoices[size];
		}
		
		@Override public QuizChoices createFromParcel(Parcel source) {
			Parcelable[] ary = source.readParcelableArray(Choice.class.getClassLoader());
			Choice[] choices = new Choice[ary.length];
			for(int i = 0;i < ary.length;++i) {
				choices[i] = (Choice)ary[i];
			}
			
			return new QuizChoices(choices);
		}
	};
	
	
}
