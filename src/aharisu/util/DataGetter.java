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

}
