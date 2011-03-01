package aharisu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * 
 * 同期的に各種データを取得するユーティリティクラス
 * 
 * @author aharisu
 *
 */
public final class DataGetter {
	
	
	public static String getHTML(Context context, int resRawId) throws IOException{
		InputStream in = context.getResources().openRawResource(resRawId);
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String s;
			while((s = reader.readLine()) != null) {
				builder.append(s);
				builder.append("\n");
			}
			
			return builder.toString();
	}
	
	public static JSONObject getJSONObject(String url) throws IOException, JSONException {
		byte[] raw = HttpClient.getByteArrayFromURL(url);
		String data = new String(raw);
		raw = null;
		
		return new JSONObject(data);
	}
	
	public static class BitmapDecodeException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1358149185993621801L;
		
	}
	
	/**
	 * 
	 * @param url
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 * @throws IOException
	 * @throws BitmapDecodeException
	 */
	public static Bitmap getBitmap(String url, int maxWidth, int maxHeight) 
		throws IOException, BitmapDecodeException {
			Bitmap bitmap = null;
			byte[] raw = HttpClient.getByteArrayFromURL(url);
			
			if(maxWidth < 0 || maxHeight < 0) {
				bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length);
			} else {
				bitmap = ImageUtill.loadImage(raw, maxWidth, maxHeight);
			}
			
			if(bitmap == null) {
				throw new BitmapDecodeException();
			}
			
			return bitmap;
	}
	
	private static final class CacheKey {
		private final String url;
		private final int width;
		private final int height;
		
		public CacheKey(String url, int width, int height) {
			this.url = url;
			this.width = width;
			this.height = height;
		}
		
		@Override public boolean equals(Object o) {
			if(o == this) {
				return true;
			} else if(o instanceof CacheKey) {
				CacheKey key = (CacheKey)o;
				
				return key.url.equals(this.url) && key.width == this.width && key.height == this.height;
			} else {
				return false;
			}
		}
		
		@Override public int hashCode() {
			int result = 17;
			result = 37 * result + url.hashCode();
			result = 37 * result + width;
			result = 37 * result + height;
			
			return result;
		}
	}
	
	private static final LruCache<CacheKey, Bitmap> mCache = new LruCache<CacheKey, Bitmap>(5);
	public static Bitmap getBitmapCache(String url, int maxWidth, int maxHeight) 
		throws IOException, BitmapDecodeException{
	
		CacheKey key = new CacheKey(url, maxWidth, maxHeight);
		
		Bitmap result = mCache.get(key);
		if(result == null) {
			result = getBitmap(url, maxWidth, maxHeight);
			
			mCache.put(key, result);
		}
		
		return result;
	}

}
