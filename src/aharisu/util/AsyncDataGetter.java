package aharisu.util;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;


/**
 * 
 * 非同期に各種データを取得するユーティリティ
 * 
 * @author aharisu
 *
 */
public final class AsyncDataGetter {
	
	public static interface Callback<T> {
		public void onGetData(T data);
	}	
	
	
	private static final class AsyncByteArrayGetter extends AsyncTask<String, Void, byte[]> {
		
		private final Callback<byte[]> _callback;
		
		private AsyncByteArrayGetter(Callback<byte[]> callback) {		
			_callback = callback;
		}
		
		@Override protected byte[] doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			byte[] raw = HttpClient.getByteArrayFromURL(params[0]);
			
			return raw;
		}
		
		@Override protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			
			if(_callback != null) {
				_callback.onGetData(result);
			}
		}
	}
	
	public static void getByteArray(String url, Callback<byte[]> callback) {
		AsyncByteArrayGetter getter = new AsyncByteArrayGetter(callback);
		
		getter.execute(url);
	}
	
	
	
	
	private static final class AsyncStringGetter extends AsyncTask<String, Void, String> {
		
		private final Callback<String> _callback;
		
		private AsyncStringGetter(Callback<String> callback) {		
			_callback = callback;
		}
		
		@Override protected String doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			byte[] raw = HttpClient.getByteArrayFromURL(params[0]);
			String data = new String(raw);
			
			return data;
		}
		
		@Override protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if(_callback != null) {
				_callback.onGetData(result);
			}
		}
	}
	
	public static void getString(String url, Callback<String> callback) {
		AsyncStringGetter getter = new AsyncStringGetter(callback);
		
		getter.execute(url);
	}
	

	
	
	private static final class AsyncJSONGetter extends AsyncTask<String, Void, JSONObject> {
		
		private final Callback<JSONObject> _callback;
		
		private AsyncJSONGetter(Callback<JSONObject> callback) {
			this._callback = callback;
		}
		
		@Override protected JSONObject doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			byte[] raw = HttpClient.getByteArrayFromURL(params[0]);
			String data = new String(raw);
			raw = null;
			
			JSONObject obj = null;
			try {
				obj = new JSONObject(data);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return obj;
		}
		
		@Override protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			
			if(_callback != null) {
				_callback.onGetData(result);
			}
		}
	}
	
	public static void getJSONObject(String url, Callback<JSONObject> callback) {
		AsyncJSONGetter getter = new AsyncJSONGetter(callback);
		
		getter.execute(url);
	}
	
	
	
}
