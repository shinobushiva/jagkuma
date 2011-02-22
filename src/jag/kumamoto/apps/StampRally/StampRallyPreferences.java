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
	
	//インスタンス化させない
	private StampRallyPreferences() {
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
	
	private static final String PreferenceFirstStartStampRally = "first-start";
	public static boolean isFirstStampRallyStart() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		return pref.getInt(PreferenceFirstStartStampRally, 0) != 1;
	}
	
	public static void setFlagFirstStampRallyStart() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(PreferenceFirstStartStampRally, 1);
		editor.commit();
	}
	
	
	private static final String PreferenceRouteSearchKind = "route-search-kind";
	public static int getRouteSearchKind() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		return pref.getInt(PreferenceRouteSearchKind, 0);
	}
	
	public static void setRouteSearchKind(int kind) {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(PreferenceRouteSearchKind, kind);
		editor.commit();
	}
	
	
}
