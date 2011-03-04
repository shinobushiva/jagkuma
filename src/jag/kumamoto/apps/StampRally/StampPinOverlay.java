package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.StampPin;
import jag.kumamoto.apps.gotochi.R;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

/**
 * 
 * スタンプラリーのピンをマップ上に表示するオーバーレイ
 * 
 * @author aharisu
 *
 */
public class StampPinOverlay extends ItemizedOverlay<StampPinOverlay.StampRallyMarker>{
	
	public static interface Filter {
		public boolean filter(StampPin pin);
	}
	
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
	private final MapView mMapView;
	
	private final ArrayList<Filter> mFilterList = new ArrayList<Filter>();
	
	private final ArrayList<StampPin> mShowStampPin = new ArrayList<StampPin>();
	
	
	public StampPinOverlay(Context context, Drawable defaultMarker,  MapView map) {
		super(boundCenterBottom(defaultMarker));
		
		this.mContext = context;
		this.mMapView = map;
		
		applyFilter();
	}
	
	public void addStampPins(StampPin... pins) {
		if(pins == null || pins.length == 0)
			return;
		
		mStampPinList.addAll(Arrays.asList(pins));
		
		applyFilter();
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
		
		applyFilter();
	}
	
	public void setStampPins(StampPin... pins) {
		mShowStampPin.clear();
		
		if(pins == null || pins.length == 0)
			return;
		
		mStampPinList.addAll(Arrays.asList(pins));
		
		applyFilter();
	}
	
	public void setInfoOverlay(PinInfoOverlay overlay) {
		mInfoOverlay = overlay;
	}
	
	public void addShowPinFilter(Filter filter, boolean update) {
		mFilterList.add(filter);
		
		if(update) {
			applyFilter();
		}
	}
	
	public void removeShowPinFilter(Filter filter, boolean update) {
		mFilterList.remove(filter);
		
		if(update) {
			applyFilter();
		}
	}
	
	private void applyFilter() {
		mShowStampPin.clear();
		
		if(mFilterList.size() == 0) {
			mShowStampPin.addAll(mStampPinList);
		} else {
			for(StampPin pin : mStampPinList) {
				boolean show = true;
				for(Filter filter : mFilterList) {
					if(!filter.filter(pin)) {
						show = false;
						break;
					}
				}
				
				if(show) {
					mShowStampPin.add(pin);
				}
			}
		}
		
		this.populate();
		mMapView.invalidate();
	}
	
	@Override protected StampRallyMarker createItem(int i) {
		return new StampRallyMarker(mShowStampPin.get(i), mContext);
	}
	
	@Override public int size() {
		return mShowStampPin.size();
	}
	
	@Override protected boolean onTap(int index) {
		StampRallyMarker marker = getItem(index);
		
		mMapView.getController().animateTo(marker.getPoint());
		if(mInfoOverlay != null) {
			mInfoOverlay.setMarkerInfo(marker);
		}
		
		return true;
	}
	
}
