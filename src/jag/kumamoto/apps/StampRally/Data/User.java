package jag.kumamoto.apps.StampRally.Data;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public final class User implements Parcelable {
	private static final String RegistrationQueryURL = "http://kumamotogotochi.appspot.com/registration?";
	private static final String ArriveQueryURL = "http://kumamotogotochi.appspot.com/arrive?";
	
	public static final int Female = 0;
	public static final int Male = 1;
	public static final int Unknown = 2;
	
	public final String token;
	public final int gender;
	public final String nickname;
	
	public User(String token, int gender, String nickname) {
		this.token = token;
		this.gender = gender;
		this.nickname = nickname;
	}
	
	public String getRegistrationQueryURL() {
		return new StringBuilder(RegistrationQueryURL)
			.append("token=").append(token)
			.append("&gender=").append(gender)
			.append("&nickname=").append(nickname).toString();
	}
	
	public static boolean isSuccess(JSONObject obj) throws JSONException{
		return obj.getString("success").equals("true");
	}
	
	public String getArriveQueryURL(StampPin pin) {
		return new StringBuilder(ArriveQueryURL)
			.append("token=").append(this.token)
			.append("&pinId=").append(pin.id)
			.toString();
	}
	
	/*
	 * 以降 Parcelableクラスの実装
	 */
	
	@Override public int describeContents() {
		return 0;
	}
	
	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(token);
		dest.writeInt(gender);
		dest.writeString(nickname);
	}
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		
		@Override public User[] newArray(int size) {
			return new User[size];
		}
		
		@Override public User createFromParcel(Parcel source) {
			return new User(
					source.readString(),
					source.readInt(),
					source.readString());
		}
	};
	

}
