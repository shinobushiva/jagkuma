package jag.kumamoto.apps.StampRally;

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
	
}
