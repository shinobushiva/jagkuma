package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

final class EveryKindSettingsHelper {
	public static interface OnValueChangeListener {
		public void onPollingIntervalChanged(int type);
		public void onShowUrgeChanged(boolean bool);
	}
	
	private final ViewGroup mLayout;
	private User mUser;
	private final OnValueChangeListener mListener;
	
	public EveryKindSettingsHelper(ViewGroup layout, User user, OnValueChangeListener listener) {
		mLayout = layout;
		mUser = user;
		this.mListener = listener;
		
		initializeView();
	}
	
	private void initializeView() {
		RadioGroup pollingGroup = (RadioGroup)mLayout.findViewById(R.id_settings.arrive_polling_group);
		pollingGroup.check(convertPollingTypeToId(StampRallyPreferences.getArrivePollingIntervalType()));
		pollingGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(mListener != null) {
					mListener.onPollingIntervalChanged(convertIdToPollingType(checkedId));
				}
			}
		});
		
		CheckBox chkShowUrge = (CheckBox)mLayout.findViewById(R.id_settings.show_urge);
		chkShowUrge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(mListener != null) {
					mListener.onShowUrgeChanged(isChecked);
				}
			}
		});
		if(mUser == null) {
			chkShowUrge.setEnabled(true);
		} else {
			chkShowUrge.setEnabled(false);
			chkShowUrge.setChecked(StampRallyPreferences.getShowUrgeDialog());
		}
	}
	
	private int convertPollingTypeToId(int type) {
		return type == 0 ? R.id_settings.polling_short :
			type == 2 ? R.id_settings.polling_long :
			R.id_settings.polling_normal;
	}
	
	private int convertIdToPollingType(int id) {
		return id == R.id_settings.polling_short ? 0 :
			id == R.id_settings.polling_long ? 2 :
			1;
	}
	
	public void setUser(User user) {
		mUser = user;
		
		CheckBox chkShowUrge = (CheckBox)mLayout.findViewById(R.id_settings.show_urge);
		if(mUser == null) {
			chkShowUrge.setEnabled(true);
		} else {
			chkShowUrge.setEnabled(false);
		}
	}
	
}
