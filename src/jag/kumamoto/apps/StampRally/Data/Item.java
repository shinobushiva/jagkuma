package jag.kumamoto.apps.StampRally.Data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * 受賞で与えられるアイテムデータを表すクラス
 * 
 * @author aharisu
 *
 */
public final class Item {
	public final long id;
	public final String name;
	public final String imageUrl;
	public final String description;
	
	public Item(long id, String name, String imageUrl, String description) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.description = description;
	}
	
	public static Item decodeJSONObject(JSONObject obj) throws JSONException {
		long id = obj.getJSONObject("key").getLong("id");
		String description = obj.getString("description");
		String url = obj.getString("imageUrl");
		String name  =obj.getString("name");
		
		return new Item(id, name, url, description);
	}

}
