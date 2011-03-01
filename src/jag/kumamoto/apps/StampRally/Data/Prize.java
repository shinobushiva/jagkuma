package jag.kumamoto.apps.StampRally.Data;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONObject;

/**
 * 
 * 受賞データを表すクラス
 * 
 * @author aharisu
 *
 */
public final class Prize {
	public final long id;
	private final Date time;
	public final String title;
	public final String message;
	public final Item item;
	
	public Prize(long id, long time, String title, String message, Item item) {
		this.id = id;
		this.time = new Date(time);
		this.title = title;
		this.message = message;
		this.item = item;
	}
	
	public static Prize[] decodeJSONObject(JSONObject obj) throws JSONException {
		JSONArray jsonAry = obj.getJSONArray("prizes");
		int length = jsonAry.length();
		
		Prize[] prizes = new Prize[length];
		
		for(int i = 0;i < length;++i) {
			JSONObject o = jsonAry.getJSONObject(i);
			
			long id = o.getJSONObject("key").getLong("id");
			long time = o.getLong("issuedTime");
			String message = o.getString("message");
			String title = o.getString("title");
			Item item = Item.decodeJSONObject(o.getJSONObject("item"));
			
			prizes[i] = new Prize(id, time, title, message, item);
		}
		
		return prizes;
	}
	
	public long getTimeMilliseconds() {
		return time.getTime();
	}
	
	public static Prize[] extractNewPrizes(Prize[] curPrizes, Prize[] gettedPrizes) {
		ArrayList<Prize> newerAry = new ArrayList<Prize>();
		
		for(int i = 0;i < gettedPrizes.length;++i) {
			boolean found = false;
			for(int j = 0;j < curPrizes.length;++j) {
				if(gettedPrizes[i].id == curPrizes[j].id) {
					found = true;
					break;
				}
			}
			if(!found) {
				newerAry.add(gettedPrizes[i]);
			}
		}
		
		Prize[] newer = new Prize[newerAry.size()];
		newerAry.toArray(newer);
		return newer;
	}
	
}
