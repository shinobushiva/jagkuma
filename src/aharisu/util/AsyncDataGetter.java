package aharisu.util;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
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
		public void onFailure(Exception e);
	}	
	
	
	private static final class AsyncByteArrayGetter extends AsyncTask<String, Void, byte[]> {
		
		private IOException _exception;
		private final Callback<byte[]> _callback;
		
		private AsyncByteArrayGetter(Callback<byte[]> callback) {		
			_callback = callback;
		}
		
		@Override protected byte[] doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			byte[] raw;
			try {
				raw = HttpClient.getByteArrayFromURL(params[0]);
			} catch (IOException e) {
				_exception = e;
				raw = null;
			}
			
			return raw;
		}
		
		@Override protected void onPostExecute(byte[] result) {
			super.onPostExecute(result);
			
			if(_callback != null) {
				if(result != null) {
					_callback.onGetData(result);
				} else {
					_callback.onFailure(_exception);
				}
			}
		}
	}
	
	/**
	 * {@link Callback#onFailure(Exception)}には IOExceptionの可能性がある
	 * @param url
	 * @param callback
	 */
	public static void getByteArray(String url, Callback<byte[]> callback) {
		AsyncByteArrayGetter getter = new AsyncByteArrayGetter(callback);
		
		getter.execute(url);
	}
	
	
	
	
	private static final class AsyncStringGetter extends AsyncTask<String, Void, String> {
		
		private IOException _exception;
		private final Callback<String> _callback;
		
		private AsyncStringGetter(Callback<String> callback) {		
			_callback = callback;
		}
		
		@Override protected String doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			String data;
			try {
				byte[] raw = HttpClient.getByteArrayFromURL(params[0]);
				data = new String(raw);
			} catch (IOException e) {
				_exception = e;
				data = null;
			}
			
			return data;
		}
		
		@Override protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			if(_callback != null) {
				if(result != null) {
					_callback.onGetData(result);
				} else {
					_callback.onFailure(_exception);
				}
			}
		}
	}
	
	/**
	 * {@link Callback#onFailure(Exception)}には IOExceptionの可能性がある
	 * @param url
	 * @param callback
	 */
	public static void getString(String url, Callback<String> callback) {
		AsyncStringGetter getter = new AsyncStringGetter(callback);
		
		getter.execute(url);
	}
	

	
	
	private static final class AsyncJSONGetter extends AsyncTask<String, Void, JSONObject> {
		
		private Exception _exception;
		private final Callback<JSONObject> _callback;
		
		private AsyncJSONGetter(Callback<JSONObject> callback) {
			this._callback = callback;
		}
		
		@Override protected JSONObject doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			JSONObject obj = null;
			try {
				obj = DataGetter.getJSONObject(params[0]);
			} catch (IOException e) {
				_exception = e;
			} catch (JSONException e) {
				_exception = e;
			}
			
			return obj;
		}
		
		@Override protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			
			if(_callback != null) {
				if(result != null) {
					_callback.onGetData(result);
				} else {
					_callback.onFailure(_exception);
				}
			}
		}
	}
	
	/**
	 * {@link Callback#onFailure(Exception)}には IOExceptionとJSONExceptionの可能性がある
	 * @param url
	 * @param callback
	 */
	public static void getJSONObject(String url, Callback<JSONObject> callback) {
		AsyncJSONGetter getter = new AsyncJSONGetter(callback);
		
		getter.execute(url);
	}
	
	
	
	
	public static interface BitmapCallback {
		public void onGetData(Bitmap data);
		public void onFailure(Exception e);
		public Size getMaxImageSize();
	}
	
	private static final class AsyncBitmapGetter extends AsyncTask<String, Void, Bitmap> {
		
		private Exception _exception;
		private final BitmapCallback mCallback;
		
		public AsyncBitmapGetter(BitmapCallback callback) {
			mCallback = callback;
		}
		
		@Override protected Bitmap doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			Size size = mCallback.getMaxImageSize();
			int maxWidth, maxHeight;
			if(size != null) {
				maxWidth = size.width;
				maxHeight = size.height;
			} else {
				maxWidth = -1;
				maxHeight = -1;
			}
			
			Bitmap bitmap = null;
			try {
				bitmap = DataGetter.getBitmap(params[0], maxWidth, maxHeight);
			} catch(IOException e) {
				_exception = e;
			} catch(DataGetter.BitmapDecodeException e) {
				_exception = e;
			}
			
			return bitmap;
		}
		
		@Override protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if(result != null) {
				mCallback.onGetData(result);
			} else {
				mCallback.onFailure(_exception);
			}
		}
		
	}
	
	/**
	 * {@link Callback#onFailure(Exception)}には IOExceptionとBitmapDecodeExceptionの可能性がある
	 * @param url
	 * @param callback
	 */
	public static void getBitmap(String url, BitmapCallback callback) {
		AsyncBitmapGetter getter = new AsyncBitmapGetter(callback);
		
		getter.execute(url);
	}
	
	
	
	
	private static final class AsyncBitmapCacheGetter extends AsyncTask<String, Void, Bitmap> {
		
		private Exception _exception;
		private final BitmapCallback mCallback;
		
		public AsyncBitmapCacheGetter(BitmapCallback callback) {
			mCallback = callback;
		}
		
		@Override protected Bitmap doInBackground(String... params) {
			if(params.length != 1) {
				throw new RuntimeException();
			}
			
			Size size = mCallback.getMaxImageSize();
			int maxWidth, maxHeight;
			if(size != null) {
				maxWidth = size.width;
				maxHeight = size.height;
			} else {
				maxWidth = -1;
				maxHeight = -1;
			}
			
			Bitmap bitmap = null;
			try {
				bitmap = DataGetter.getBitmapCache(params[0], maxWidth, maxHeight);
			} catch(IOException e) {
				_exception = e;
			} catch(DataGetter.BitmapDecodeException e) {
				_exception = e;
			}
			
			return bitmap;
		}
		
		@Override protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			
			if(result != null) {
				mCallback.onGetData(result);
			} else {
				mCallback.onFailure(_exception);
			}
		}
		
	}
	
	/**
	 * {@link Callback#onFailure(Exception)}には IOExceptionとBitmapDecodeExceptionの可能性がある
	 * @param url
	 * @param callback
	 */
	public static void getBitmapCache(String url, BitmapCallback callback) {
		AsyncBitmapCacheGetter getter = new AsyncBitmapCacheGetter(callback);
		
		getter.execute(url);
	}
	
}
