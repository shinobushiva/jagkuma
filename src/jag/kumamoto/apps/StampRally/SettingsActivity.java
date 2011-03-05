package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;



/**
 *
 * 設定画面のアクティビティ
 *
 * @author aharisu
 *
 */
public class SettingsActivity extends TabActivity{
	private User mUser;
	
	private int mChangedType = -1;

	private IArriveWatcherService mArriveWatcher;
	private final ServiceConnection mConnection = new ServiceConnection() {
		
		@Override public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override public void onServiceConnected(ComponentName name, IBinder service) {
			mArriveWatcher = IArriveWatcherService.Stub.asInterface(service);
			if(mChangedType >= 0) {
				try {
					mArriveWatcher.changeArriveCheckInterval(mChangedType);
				} catch(RemoteException e) {
					e.printStackTrace();
				}
				mChangedType = 0;
			}
		}
	};
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		boolean isFirstStart = false;
		
		if(extras != null) {
			mUser = extras.getParcelable(ConstantValue.ExtrasUser);
			isFirstStart = extras.getBoolean(ConstantValue.ExtrasFirstSettings, false);
		}
		final boolean loginRequest = extras != null ?
				extras.getBoolean(ConstantValue.ExtrasLoginRequest, false) :
				false;
				
		//初回表示タイミングでキーボードを表示させないようにする
		this.getWindow().setSoftInputMode(
				android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		
		initTabPages();

		if(isFirstStart) {
			showFirstSettingsDialog();
		}
		
		final EveryKindSettingsHelper everyKindSettings = new EveryKindSettingsHelper(
				(ViewGroup)findViewById(R.id_settings.tab_every_kind),
				mUser,
				new EveryKindSettingsHelper.OnValueChangeListener() {
					@Override public void onShowUrgeChanged(boolean bool) {
						StampRallyPreferences.setShowUrgeDialog(bool);
					}
					
					@Override public void onPollingIntervalChanged(int type) {
						StampRallyPreferences.setArrivePollingIntervalType(type);
						if(mArriveWatcher != null) {
							try {
								mArriveWatcher.changeArriveCheckInterval(mChangedType);
							} catch(RemoteException e) {
								e.printStackTrace();
							}
						} else {
							mChangedType = type;
						}
					}
				});
		
		UserSettingsHelper.constractUserSettingsView((ViewGroup)findViewById(R.id_settings.tab_user), mUser, 
				new UserSettingsHelper.OnLoginLogoutListener() {
					@Override public void onLogin(User user) {
						if(loginRequest) {
							Intent intent = new Intent();
							intent.putExtra(ConstantValue.ExtrasUser, user);
							setResult(Activity.RESULT_OK, intent);
							finish();
						} else {
							everyKindSettings.setUser(user);
						}
					}
					
					@Override public void onLogout() {
						everyKindSettings.setUser(null);
					}
					
				});
		
		//スタンプラリーのピンの到着を監視するサービスとバインドする
		bindArriveWatcherservice();
	}
	
	private void initTabPages() {
		TabHost tabHost = getTabHost();
		
		TabHost.TabSpec spec;
		
		spec = tabHost.newTabSpec("user_settings");
		spec.setIndicator("ユーザ設定");
		spec.setContent(R.id_settings.tab_user);
		tabHost.addTab(spec);
		
		spec = tabHost.newTabSpec("other");
		spec.setIndicator("その他");
		spec.setContent(R.id_settings.tab_every_kind);
		tabHost.addTab(spec);
	}

	private void showFirstSettingsDialog() {
		new AlertDialog.Builder(this)
			.setTitle("設定")
			.setMessage("ユーザ登録が必要です\n以下いろいろと説明があればいいな")
			.setPositiveButton("OK", null)
			.show();
	}
	
	@Override protected void onDestroy() {
		//スタンプラリーのピンの到着を監視するサービスをアンバインドする
		unbindArriveWatcherService();
		
		super.onDestroy();
	}

	private void bindArriveWatcherservice() {
		Intent intent = new Intent(this, ArriveWatcherService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	private void unbindArriveWatcherService() {
		unbindService(mConnection);
	}
}
