package jag.kumamoto.apps.StampRally.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * スタンプラリーのチェックポイントを表すクラス
 * 
 * @author aharisu
 *
 */
public final class StampPin {
	private static final String QueryURL = "http://kumamotogotochi.appspot.com/pins";
	
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
	
	
	public final long id;
	public final int latitude;
	public final int longitude;
	public final String name;
	public final int point;
	public final int prefCode;
	public final int areaCode;
	public final int type;
	public final String url;
	
	private StampPin(long id,
			int latitude, int longitude,
			String name, int point,
			int prefCode, int areaCode,
			int type, String url) {
		
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.point = point;
		this.prefCode = prefCode;
		this.areaCode = areaCode;
		this.type = type;
		this.url = url;
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
			
			pins[i] = new StampPin(id, latitude, longitude, name, point, prefCode, areaCode, type, url);
		}
		
		return pins;
	}
	
	public static final String getQueryURL() {
		return QueryURL;
	}
	
}
