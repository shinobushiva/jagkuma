package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.User;
import android.content.Context;
import android.content.SharedPreferences;


/**
 * 
 * スタンプラリーアプリケーションで使用するプリファレンスを操作するためのクラス
 * 
 * @author aharisu
 *
 */
public final class StampRallyPreferences {

	private static Context mContext;
	
	public static void setContext(Context context) {
		mContext = context;
	}
	
	private static final String PreferenceName = "StampRallyPrefs";
	
	
	private static final String PrefLastCheckDateStampPin = "StampPin";
	
	public static long getLastCheckDateStampPin() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
				
				return pref.getLong(PrefLastCheckDateStampPin, 0);
	}
	
	public static void setLastCheckDateStampPin(long date) {
		SharedPreferences	pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(PrefLastCheckDateStampPin, date);
		editor.commit();
	}
	
	
	public static final String PreferenceVisiblePassword = "settings-visible-password";
	
	public static boolean getVisiblePassword() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		return pref.getBoolean(PreferenceVisiblePassword, false);
	}
	
	public static void setVisiblePassword(boolean visible) {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(PreferenceVisiblePassword, visible);
		editor.commit();
	}
	
	private static final String PreferenceToken = "user-token";
	private static final String PreferenceGender = "user-gender";
	private static final String PreferenceNickname = "user-nickname";
	
	public static User getUser() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		String token = pref.getString(PreferenceToken, null);
		int gender = pref.getInt(PreferenceGender, -1);
		String nickname = pref.getString(PreferenceNickname, null);
		if(token == null || 
				(gender < 0 || gender > 2) ||
				nickname == null) {
			return null;
		}
		
		return new User(token, gender, nickname);
	}
	
	public static void setUser(User user) {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(PreferenceToken, user.token);
		editor.putInt(PreferenceGender, user.gender);
		editor.putString(PreferenceNickname, user.nickname);
		editor.commit();
	}
	
	
}
