package jag.kumamoto.apps.StampRally;

import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import jag.kumamoto.apps.StampRally.Data.StampPin;
import jag.kumamoto.apps.gotochi.R;
import aharisu.util.DataGetter;
import aharisu.util.Pair;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
	
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
				JSONObject obj = DataGetter.getJSONObject(StampPin.getQueryURL());
				
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
				
				if(pin.type == StampPin.STAMP_TYPE_QUIZ &&
						isShowGoQuiz(pin))  {
					intent.putExtra(ConstantValue.ExtrasShowGoQuiz, true);
				}
				
				startActivity(intent);
			}
		};
	}
	
	
	private boolean isShowGoQuiz(StampPin pin) {
		//TODO ここで位置情報を使ってクイズを表示する範囲かどうか確かめる
		return true;
	}
	
	
	@Override protected boolean isRouteDisplayed() {
		return false;
	}

}
