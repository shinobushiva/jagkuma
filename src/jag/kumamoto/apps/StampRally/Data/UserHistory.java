package jag.kumamoto.apps.StampRally.Data;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

public final class UserHistory {
	
	public static int decodeJSONGotochiData(JSONObject obj) throws JSONException {
		JSONObject objData = obj.getJSONObject("gotochiData");
		return objData.getInt("point");
	}
	
	public static long[] decodeJSONGetArrivedIds(JSONObject obj) throws JSONException {
		JSONArray ary = obj.getJSONArray("history");
		int count = ary.length();
		long[] ids = new long[count];
		
		int index = 0;
		for(int i = 0;i < count;++i) {
			JSONObject o = ary.getJSONObject(i);
			if(o.isNull("quiz")) {
				ids[index] = o.getJSONObject("pin").getJSONObject("key").getLong("id");
				
				++index;
			}
		}
		
		if(count != index) {
			long[] tmp = new long[index];
			for(int i = 0;i < index;++i) {
				tmp[i] = ids[i];
			}
			
			ids = tmp;
		}
		
		return ids;
	}
	
}
