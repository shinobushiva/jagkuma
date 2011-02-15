package jag.kumamoto.apps.StampRally;


import jag.kumamoto.apps.StampRally.Data.StampPin;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * 
 * スタンプラリーアプリケーションで使用するDBを操作するためのクラス
 * 
 * @author aharisu
 *
 */
public class StampRallyDB extends SQLiteOpenHelper{
	
	private static final String DataBaseName = "StampRally";
	private static final int Version = 1;
	
	
	/*
	 * StampLocationクラスの永続化のためのフィールド
	 */
	private static final String StampLocationTable = "stamp_location";
	private static final String StampLocationID = "id";
	private static final String StampLocationLatitude = "latitude";
	private static final String StampLocationLongitude = "longitude";
	private static final String StampLocationName = "name";
	private static final String StampLocationIsArrived = "is_arrived";
	private static final String StampLocationPoint = "point";
	private static final String StampLocationPrefecturesCode = "prefectures";
	private static final String StampLocationAreaCode = "area_code";
	private static final String StampLocationType = "type";
	private static final String StampLocationURL = "url";

	
	/*
	 * Singletonインスタンス
	 */
	private static StampRallyDB _instance = null;
	
	public static void createInstance(Context context) {
		_instance = new StampRallyDB(context);
	}
	
	private static StampRallyDB getInstance() {
		if(_instance == null) {
			throw new RuntimeException("do not create instance yet");
		}
		
		return _instance;
	}
	
	private StampRallyDB(Context context) {
		super(context, DataBaseName, null, Version);
	}
	
	
	@Override public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			
			//StampLocationテーブル作成
			db.execSQL(new StringBuilder()
				.append("create table ").append(StampLocationTable).append("(")
				.append(StampLocationID).append(" integer primary key")
				.append(",").append(StampLocationLatitude).append(" integer not null")
				.append(",").append(StampLocationLongitude).append(" integer not null")
				.append(",").append(StampLocationName).append(" text not null")
				.append(",").append(StampLocationIsArrived).append(" integer not null")
				.append(",").append(StampLocationPoint).append(" integer not null")
				.append(",").append(StampLocationPrefecturesCode).append(" integer not null")
				.append(",").append(StampLocationAreaCode).append(" integer not null")
				.append(",").append(StampLocationType).append(" integer not null")
				.append(",").append(StampLocationURL).append(" text not null")
				.append(")")
				.toString());
			
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	@Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	
	public static StampPin[] getStampPins() {
		SQLiteOpenHelper helper = getInstance();
		SQLiteDatabase db = helper.getReadableDatabase();
		
		Cursor cursor = null;
		try {
			cursor = db.query(StampLocationTable, 
					new String[] {
						StampLocationID,
						StampLocationLatitude,
						StampLocationLongitude,
						StampLocationName,
						StampLocationIsArrived,
						StampLocationPoint,
						StampLocationPrefecturesCode,
						StampLocationAreaCode,
						StampLocationType,
						StampLocationURL,
				}, null, null, null, null, null);
			
			return createStampPinsFromCursor(cursor);
		} finally {
			if(cursor != null)
				cursor.close();
			
			db.close();
		}
	}
	
	private static StampPin[] createStampPinsFromCursor(Cursor cursor) {
		int count = cursor.getCount();
		StampPin[] stamps = new StampPin[count];
		if(count != 0) {
			int idIndex = cursor.getColumnIndex(StampLocationID);
			int latitudeIndex = cursor.getColumnIndex(StampLocationLatitude);
			int longitudeIndex = cursor.getColumnIndex(StampLocationLongitude);
			int nameIndex = cursor.getColumnIndex(StampLocationName);
			int isArrivedIndex = cursor.getColumnIndex(StampLocationIsArrived);
			int pointIndex = cursor.getColumnIndex(StampLocationPoint);
			int prefecturesIndex = cursor.getColumnIndex(StampLocationPrefecturesCode);
			int areaIndex = cursor.getColumnIndex(StampLocationAreaCode);
			int typeIndex = cursor.getColumnIndex(StampLocationType);
			int urlIndex = cursor.getColumnIndex(StampLocationURL);
			
			cursor.moveToFirst();
			for(int i = 0;i < count;++i) {
				stamps[i] = new StampPin(
						cursor.getLong(idIndex),
						cursor.getInt(latitudeIndex),
						cursor.getInt(longitudeIndex),
						cursor.getString(nameIndex),
						cursor.getInt(pointIndex),
						cursor.getInt(prefecturesIndex),
						cursor.getInt(areaIndex),
						cursor.getInt(typeIndex),
						cursor.getString(urlIndex),
						cursor.getInt(isArrivedIndex) != 0);
				
				cursor.moveToNext();
			}
		}
				
		return stamps;
	}
	
	public static void insertStampPins(StampPin... pins) {
		if(pins == null || pins.length == 0)
			return;
		
		SQLiteDatabase db = getInstance().getWritableDatabase();
		db.beginTransaction();
		
		try {
			ContentValues values = new ContentValues();
			for(StampPin pin : pins) {
				values.clear();
				
				values.put(StampLocationID, pin.id);
				values.put(StampLocationLatitude, pin.latitude);
				values.put(StampLocationLongitude, pin.longitude);
				values.put(StampLocationName, pin.name);
				values.put(StampLocationIsArrived, pin.isArrive);
				values.put(StampLocationPoint, pin.point);
				values.put(StampLocationPrefecturesCode, pin.prefCode);
				values.put(StampLocationAreaCode, pin.areaCode);
				values.put(StampLocationType, pin.type);
				values.put(StampLocationURL, pin.url);
				
				db.insert(StampLocationTable, null, values);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
	public static void deleteStampPins(StampPin... pins) {
		if(pins == null || pins.length == 0)
			return;
		
		SQLiteDatabase db = getInstance().getWritableDatabase();
		
		db.beginTransaction();
		try {
			StringBuilder builder = new StringBuilder()
				.append("delete from ")
				.append(StampLocationTable)
				.append(" where ")
				.append(StampLocationID)
				.append(" in (")
				.append(pins[0].id);
			for(int i = 1;i< pins.length;++i) {
				builder.append(", ").append(pins[i].id);
			}
			builder.append(")");
			
			db.execSQL(builder.toString());
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}
	}
	
}
