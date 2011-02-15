package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.StampPin;
import jag.kumamoto.apps.gotochi.R;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.OverlayItem;

/**
 * 
 * スタンプラリーのピンをマップ上に表示するオーバーレイ
 * 
 * @author aharisu
 *
 */
public class StampPinOverlay extends ItemizedOverlay<StampPinOverlay.StampRallyMarker>{
	
	public static class StampRallyMarker extends OverlayItem {
		public final StampPin stampPin;
		
		public StampRallyMarker(StampPin stamp, Context context) {
			super(new GeoPoint(stamp.latitude, stamp.longitude),
					stamp.name, null);
			
			stampPin = stamp;
			
			Drawable drawable;
			if(stamp.type ==  StampPin.STAMP_TYPE_NONE) {
				drawable = context.getResources().getDrawable(stamp.isArrive ? 
								R.drawable.marker_none_arrived :
								R.drawable.marker_none);
			} else {
				drawable = context.getResources().getDrawable(stamp.isArrive ? 
								R.drawable.marker_quiz_arrived :
								R.drawable.marker_quiz);
			}
			
			setMarker(boundCenterBottom(drawable));
		}
	}
	
	
	public static final int ShowTypeAllMarker = 0;
	public static final int ShowTypeArrivedMarker = 1;
	public static final int ShowTypeNoArriveMarker = 2;
	
	
	private final Context mContext;
	
	private PinInfoOverlay mInfoOverlay;
	private final ArrayList<StampPin> mStampPinList = new ArrayList<StampPin>();
	private final MapController mMapController;
	
	/*
	private int mCurShowType = -1;
	private final ArrayList<StampLocation> mShowStampLocation;
	int mSize;
	*/
	
	
	public StampPinOverlay(Context context, Drawable defaultMarker, 
			MapController mapController, int showType) {
		super(boundCenterBottom(defaultMarker));
		
		this.mContext = context;
		
		this.mMapController = mapController;
		
		populate();
		
		/*
		this.mStampLocations = stampLocations;
		this.mShowStampLocation = new ArrayList<StampLocation>(mStampLocations.length);
		for(int i = 0;i < mStampLocations.length;++i)
			mShowStampLocation.add(null);
		
		setShowType(showType);
		*/
	}
	
	public void addStampPins(StampPin... pins) {
		if(pins == null || pins.length == 0)
			return;
		
		mStampPinList.addAll(Arrays.asList(pins));
		
		populate();
	}
	
	public void removeStampPins(StampPin... pins) {
		if(pins == null || pins.length == 0)
			return ;
		
		for(StampPin pin : pins) {
			int size = mStampPinList.size();
			for(int i = 0;i < size;++i) {
				if(pin.id == mStampPinList.get(i).id) {
					mStampPinList.remove(i);
					break;
				}
			}
		}
		
		populate();
	}
	
	public void setInfoOverlay(PinInfoOverlay overlay) {
		mInfoOverlay = overlay;
	}
	
	/*
	public void setShowType(int showType) {
		if(mCurShowType != showType) {
			
			if(showType == ShowTypeAllMarker) {
				for(int i = 0;i < mStampLocations.length;++i) {
					mShowStampLocation.set(i, mStampLocations[i]);
				}
				mSize = mStampLocations.length;
				
			} else {
				boolean showArrived = showType == ShowTypeArrivedMarker;
				mSize = 0;
				
				for(int i = 0;i < mStampLocations.length;++i) {
					if(mStampLocations[i].getIsArrived() == showArrived) {
						mShowStampLocation.set(mSize, mStampLocations[i]);
						++mSize;
					}
				}
			}
			
			mCurShowType = showType;
			this.populate();
		}
	}
	*/
	
	
	@Override protected StampRallyMarker createItem(int i) {
		return new StampRallyMarker(mStampPinList.get(i), mContext);
		//return new StampRallyMarker(mShowStampLocation.get(i), mContext);
	}
	
	@Override public int size() {
		return mStampPinList.size();
		//return mSize;
	}
	
	@Override protected boolean onTap(int index) {
		StampRallyMarker marker = getItem(index);
		
		mMapController.animateTo(marker.getPoint());
		if(mInfoOverlay != null) {
			mInfoOverlay.setMarkerInfo(marker);
		}
		
		return true;
	}
	
}
