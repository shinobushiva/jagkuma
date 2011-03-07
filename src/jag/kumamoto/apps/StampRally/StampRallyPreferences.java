package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.StampRally.Data.UserRecord;
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
	
	public static void clearUser() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(PreferenceToken);
		editor.remove(PreferenceGender);
		editor.remove(PreferenceNickname);
		editor.commit();
	}
	
	private static final String PreferencePoint = "user-record-point";
	private static final String PreferenceNumStamp = "user-record-num-stamp";
	private static final String PreferenceNumTotalAnswerdQuize = "user-record-num-answerd-quize";
	private static final String PreferenceNumCorrectnss = "user-record-num-correctness";
	private static final String PreferenceTotalAnswerdTime = "user-record-total-answer-time";
	
	public static UserRecord getUserRecord() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		int point = pref.getInt(PreferencePoint, 0);
		int numStamp = pref.getInt(PreferenceNumStamp, 0);
		int numTotalAnswerdQuize = pref.getInt(PreferenceNumTotalAnswerdQuize, 0);
		int numCorrectness = pref.getInt(PreferenceNumCorrectnss, 0);
		long totalAnswerTime = pref.getLong(PreferenceTotalAnswerdTime, 0);
		
		return new UserRecord(point, numStamp, numTotalAnswerdQuize, numCorrectness, totalAnswerTime);
	}
	
	public static void setUserRecord(UserRecord record) {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(PreferencePoint, record.point);
		editor.putInt(PreferenceNumStamp, record.numStamp);
		editor.putInt(PreferenceNumTotalAnswerdQuize, record.numTotalAnswerdQuize);
		editor.putInt(PreferenceNumCorrectnss, record.numCorrectness);
		editor.putLong(PreferenceTotalAnswerdTime, record.totalAnswerTime);
		editor.commit();
	}
	
	public static void clearUserRecord() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(PreferencePoint);
		editor.remove(PreferenceNumStamp);
		editor.remove(PreferenceNumTotalAnswerdQuize);
		editor.remove(PreferenceNumCorrectnss);
		editor.remove(PreferenceTotalAnswerdTime);
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
	
	
	private static final String PreferenceArrivePollingInterval = "arrive-polling-interval";
	public static int getArrivePollingIntervalType() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		int value = pref.getInt(PreferenceArrivePollingInterval, 1);
		if(value < 0) {
			value = 1;
		} else if(value > 2) {
			value = 1;
		}
		
		return value;
	}
	
	public static int getArrivePollingIntervalType(Context context) {
		SharedPreferences pref = context.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		int value = pref.getInt(PreferenceArrivePollingInterval, 1);
		if(value < 0) {
			value = 1;
		} else if(value > 2) {
			value = 1;
		}
		
		return value;
	}
	
	public static void setArrivePollingIntervalType(int type) {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor= pref.edit();
		editor.putInt(PreferenceArrivePollingInterval, type);
		editor.commit();
	}
	
	private static final String PreferenceShowUrgeDialog = "show-urge-dialog";
	public static boolean getShowUrgeDialog() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		return pref.getBoolean(PreferenceShowUrgeDialog, true);
	}
	
	public static void setShowUrgeDialog(boolean bool) {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor= pref.edit();
		editor.putBoolean(PreferenceShowUrgeDialog, bool);
		editor.commit();
	}
	
	private static final String PreferenceKumamonTweetMaxId = "kumamon-tweet-max-id";
	
	public static long getKumamonTweetMaxId() {
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		return pref.getLong(PreferenceKumamonTweetMaxId, 0);
	}
	
	public static void setKumamonTweetMaxId(long id){ 
		SharedPreferences pref = mContext.getSharedPreferences(
				PreferenceName, Context.MODE_PRIVATE);
		
		SharedPreferences.Editor editor= pref.edit();
		editor.putLong(PreferenceKumamonTweetMaxId, id);
		editor.commit();
	}
	
}
