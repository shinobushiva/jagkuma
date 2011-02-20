package jag.kumamoto.apps.StampRally.Data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import aharisu.util.Pair;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * スタンプラリーのチェックポイントを表すクラス
 * 
 * @author aharisu
 *
 */
public final class StampPin implements Parcelable{
	private static final String GetAllPinQueryURL = "http://kumamotogotochi.appspot.com/pins";
	private static final String ArriveQueryURL = "http://kumamotogotochi.appspot.com/arrive?";
	
	private static final String JsonNamePins = "pins";
	
	private static final String JsonNameKey = "key";
	private static final String JsonNameID = "id";
	private static final String JsonNameLatitude = "latitude";
	private static final String JsonNameLongitude = "longitude";
	private static final String JsonNameName = "name";
	private static final String JsonNamePoint = "point";
	private static final String JsonNamePrefCode = "prefCode";
	private static final String JsonNameAreaCode = "areaCode";
	private static final String JsonNameType = "type";
	private static final String JsonNameURL = "url";
	
	
	public static final int STAMP_TYPE_NONE = 0;
	public static final int STAMP_TYPE_QUIZ = 1;
	
	
	public final long id;
	public final int latitude;
	public final int longitude;
	public final String name;
	public final int point;
	public final int prefCode;
	public final int areaCode;
	public final int type;
	public final String url;
	public final boolean isArrive;
	
	public StampPin(long id,
			int latitude, int longitude,
			String name, int point,
			int prefCode, int areaCode,
			int type, String url,
			boolean isArrive) {
		
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.point = point;
		this.prefCode = prefCode;
		this.areaCode = areaCode;
		this.type = id == 1 ? STAMP_TYPE_NONE : type;
		this.url = url;
		this.isArrive = id == 1 ? true :  isArrive;
	}
	
	
	public static StampPin[] decodeJSONObject(JSONObject obj) throws JSONException {
		JSONArray jsonPins = obj.getJSONArray(JsonNamePins);
		
		int count = jsonPins.length();
		StampPin[] pins = new StampPin[count];
		for(int i = 0;i < count;++i) {
			JSONObject jsonPin = jsonPins.getJSONObject(i);
			
			final long id = jsonPin.getJSONObject(JsonNameKey).getLong(JsonNameID);
			final int latitude = jsonPin.getInt(JsonNameLatitude);
			final int longitude = jsonPin.getInt(JsonNameLongitude);
			final String name = jsonPin.getString(JsonNameName);
			final int point = jsonPin.getInt(JsonNamePoint);
			final int prefCode = jsonPin.getInt(JsonNamePrefCode);
			final int areaCode = jsonPin.getInt(JsonNameAreaCode);
			final int type = jsonPin.getInt(JsonNameType);
			final String url = jsonPin.getString(JsonNameURL);
			
			pins[i] = new StampPin(id, latitude, longitude, name, point, prefCode, areaCode, type, url, false);
		}
		
		return pins;
	}
	
	public static final String getGetAllPinQueryURL() {
		return GetAllPinQueryURL;
	}
	
	public String getArriveQueryURL(User user) {
		return new StringBuilder(ArriveQueryURL)
			.append("token=").append(user.token)
			.append("&pinId=").append(this.id)
			.toString();
	}
	
		/**
	 * 
	 * @param curPins
	 * @param gettedPins
	 * @return V1:新しく追加されたスタンプピンの配列.V2:消去されたスタンプピンの配列
	 */
	public static Pair<StampPin[], StampPin[]> extractNewAndDeletePins(StampPin[] curPins, StampPin[] gettedPins) {
		boolean[] curPinsFlag = new boolean[curPins.length];
		boolean[] gettedPinsFlag = new boolean[gettedPins.length];
		
		for(int i = 0;i < curPins.length;++i) {
			for(int j = 0;j < gettedPins.length;++j) {
				if(curPins[i].id == gettedPins[j].id) {
					curPinsFlag[i] = true;
					gettedPinsFlag[j] = true;
					break;
				}
			}
		}
		
		
		ArrayList<StampPin> newPinList = new ArrayList<StampPin>();
		for(int i = 0;i < gettedPins.length;++i) {
			if(!gettedPinsFlag[i]) {
				newPinList.add(gettedPins[i]);
			}
		}
		
		ArrayList<StampPin> deletePinList = new ArrayList<StampPin>();
		for(int i = 0;i < curPinsFlag.length;++i) {
			if(!curPinsFlag[i]) {
				deletePinList.add(curPins[i]);
			}
		}
		
		StampPin[] tmp = new StampPin[0];
		return new Pair<StampPin[], StampPin[]>(newPinList.toArray(tmp), deletePinList.toArray(tmp));
	}
	
	
	
	/*
	 * 以降 Parcelableクラスの実装
	 */
	
	@Override public int describeContents() {
		return 0;
	}
	
	@Override public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeInt(latitude);
		dest.writeInt(longitude);
		dest.writeString(name);
		dest.writeInt(point);
		dest.writeInt(prefCode);
		dest.writeInt(areaCode);
		dest.writeInt(type);
		dest.writeString(url);
		dest.writeInt(isArrive ? 1 : 0);
	}
	
	public static final Parcelable.Creator<StampPin> CREATOR = new Parcelable.Creator<StampPin>() {
		
		@Override public StampPin[] newArray(int size) {
			return new StampPin[size];
		}
		
		@Override public StampPin createFromParcel(Parcel source) {
			return new StampPin(
					source.readLong(),
					source.readInt(),
					source.readInt(),
					source.readString(),
					source.readInt(),
					source.readInt(),
					source.readInt(),
					source.readInt(),
					source.readString(),
					source.readInt() == 1);
					
		}
	};
	
}
