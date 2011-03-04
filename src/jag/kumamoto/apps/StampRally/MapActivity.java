package jag.kumamoto.apps.StampRally;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import jag.kumamoto.apps.StampRally.Data.StampPin;
import jag.kumamoto.apps.StampRally.Data.StampRallyURL;
import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;
import aharisu.mascot.MascotView;
import aharisu.util.DataGetter;
import aharisu.util.Pair;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Window;


/**
 * 
 * スタンプラリーのチェックポイントを含むマップを表示するアクティビティ
 * 
 * @author aharisu
 *
 */
public class MapActivity extends com.google.android.maps.MapActivity{
	
	private StampPinOverlay mPinOverlay;
	private StampPin[] mStampPins;
	private User mUser;
	
	private IArriveWatcherService mArriveWatcher;
	private final ServiceConnection mConnection = new ServiceConnection() {
		
		@Override public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override public void onServiceConnected(ComponentName name, IBinder service) {
			mArriveWatcher = IArriveWatcherService.Stub.asInterface(service);
		}
	};
	
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			mUser = extras.getParcelable(ConstantValue.ExtrasUser);
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.map);
		
		
		
		final MapView map = (MapView)findViewById(R.id_map.mapview);
		map.setBuiltInZoomControls(true);
		List<Overlay> overlayList = map.getOverlays();
		
		//スタンプラリーの場所を示すピンのレイヤを追加
		mPinOverlay = new StampPinOverlay(this,
				getResources().getDrawable(R.drawable.marker_none),
				map.getController(), 0);
		overlayList.add(mPinOverlay);
		
		
		//ピンの情報を示すレイヤを追加
		PinInfoOverlay infoOverlay = new PinInfoOverlay(createPinInfoOnClickListener(),
				mPinOverlay, map, getResources().getDrawable(R.drawable.marker_none));
		overlayList.add(infoOverlay);
		mPinOverlay.setInfoOverlay(infoOverlay);
		
		
		//DBからスタンプがある場所のピンデータを取得する
		GetAsyncStampPinsFromDB();
		
		//スタンプラリーのピンの到着を監視するサービスを起動する
		startArriveWatcherservice();
	}
	
	private void GetAsyncStampPinsFromDB() {
		new AsyncTask<Void, Void, StampPin[]>() {
			
			@Override protected StampPin[] doInBackground(Void... params) {
				return StampRallyDB.getStampPins();
			}
			
			
			@Override protected void onPostExecute(StampPin[] result) {
				
				mStampPins = result == null ? new StampPin[0] : result;
				
				mPinOverlay.addStampPins(result);
				((MapView)findViewById(R.id_map.mapview)).invalidate();
				
				if(isCheckUpdate()) {
					GetAsyncStampPinsFromServer();
					StampRallyPreferences.setLastCheckDateStampPin(System.currentTimeMillis());
				}
			}
		}.execute((Void)null);
	}
	
	private void GetAsyncStampPinsFromServer() {
		new AsyncTask<Void, Void, Pair<StampPin[], Pair<StampPin[], StampPin[]>>>() {
			
			@Override protected Pair<StampPin[], Pair<StampPin[], StampPin[]>>doInBackground(Void... params) {
				JSONObject obj = DataGetter.getJSONObject(StampRallyURL.getGetAllPinQuery());
				
				StampPin[] pins;
				try {
					pins = StampPin.decodeJSONObject(obj);
				}catch(JSONException e) {
					e.printStackTrace();
					
					pins = new StampPin[0];
				}
				
				return new Pair<StampPin[], Pair<StampPin[], StampPin[]>>(pins,
						StampPin.extractNewAndDeletePins(mStampPins, pins));
			}
			
			
			@Override protected void onPostExecute(Pair<StampPin[], Pair<StampPin[], StampPin[]>> result) {
				if(result == null)
					return;
				
				mStampPins = result.v1;
				
				StampRallyDB.deleteStampPins(result.v2.v2);
				StampRallyDB.insertStampPins(result.v2.v1);
				
				mPinOverlay.removeStampPins(result.v2.v2);
				mPinOverlay.addStampPins(result.v2.v1);
				
				if((result.v2.v1 != null &&  result.v2.v1.length != 0) ||
						(result.v2.v2 != null &&  result.v2.v2.length != 0)) {
					((MapView)findViewById(R.id_map.mapview)).invalidate();
				}
			}
			
		}.execute((Void)null);
	}
	
	
	/**
	 * 前回アップデート確認をしたときから日付が変わっているかを確認する
	 * @return アップデートが必要であればtrue.不必要ならfalse.
	 */
	private boolean isCheckUpdate() {
		Date last = new Date(StampRallyPreferences.getLastCheckDateStampPin());
		Date now = new Date(System.currentTimeMillis());

		return last.getYear() != now.getYear() ||
				last.getMonth() != now.getMonth() ||
				last.getDate() != now.getDate();
	}
	
	
	private PinInfoOverlay.OnClickListener createPinInfoOnClickListener() {
		return new PinInfoOverlay.OnClickListener() {
			@Override public void onClick(StampPin pin) {
				
				Intent intent = new Intent(MapActivity.this, LocationInfoActivity.class);
				intent.putExtra(ConstantValue.ExtrasStampPin, pin);
				
				boolean isArrived = isShowGoQuiz(pin);
				intent.putExtra(ConstantValue.ExtrasIsArrive, isArrived);
				
				if(pin.type == StampPin.STAMP_TYPE_QUIZ && isArrived) {
					intent.putExtra(ConstantValue.ExtrasShowGoQuiz, true);
				}
				
				if(mUser != null) {
					intent.putExtra(ConstantValue.ExtrasUser, mUser);
				}
				
				startActivity(intent);
			}
		};
	}
	
	
	private boolean isShowGoQuiz(StampPin pin) {
		if(mArriveWatcher != null) {
			try {
				long[] ids = mArriveWatcher.getArrivedStampPins();
				
				return Arrays.binarySearch(ids, pin.id) >= 0;
			} catch(RemoteException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	
	@Override protected boolean isRouteDisplayed() {
		return false;
	}

	
	@Override protected void onDestroy() {
		
		//スタンプラリーのピンの到着を監視するサービスを終了する
		stopArriveWatcherService();
		
		super.onDestroy();
	}
	
	private void startArriveWatcherservice() {
		Intent intent = new Intent(this, ArriveWatcherService.class);
		startService(intent);
		
		bindService(intent, mConnection, 0);
	}
	
	private void stopArriveWatcherService() {
		Intent intent = new Intent(this, ArriveWatcherService.class);
		
		unbindService(mConnection);
		
		stopService(intent);
	}
	
	@Override protected void onResume() {
		super.onResume();
		
		((MascotView)findViewById(R.id_map.mascot)).start();
	}
	
	@Override protected void onPause() {
		super.onPause();
		
		((MascotView)findViewById(R.id_map.mascot)).stop();
	}
}
