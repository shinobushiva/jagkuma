package aharisu.util;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
			
			return DataGetter.getJSONObject(params[0]);
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
	
	
	
	
	public static interface BitmapCallback {
		public void onGetData(Bitmap data);
		public Size getMaxImageSize();
	}
	
	private static final class AsyncBitmapGetter extends AsyncTask<String, Void, Bitmap> {
		
		private final BitmapCallback mCallback;
		
		public AsyncBitmapGetter(BitmapCallback callback) {
			mCallback = callback;
		}
		
		@Override protected Bitmap doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			byte[] raw = HttpClient.getByteArrayFromURL(params[0]);
			Size size = mCallback.getMaxImageSize();
			Bitmap bitmap;
			if(size != null) {
				bitmap = ImageUtill.loadImage(raw, size.width, size.height);
			} else {
				bitmap = BitmapFactory.decodeByteArray(raw, 0, raw.length);
			}
			
			return bitmap;
		}
		
		@Override protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			mCallback.onGetData(result);
		}
		
	}
	
	public static void getBitmap(String url, BitmapCallback callback) {
		AsyncBitmapGetter getter = new AsyncBitmapGetter(callback);
		
		getter.execute(url);
	}
	
}
