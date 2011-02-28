package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
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

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		boolean isFirstStart = false;
		boolean loginRequest = false;

		if(extras != null) {
			mUser = extras.getParcelable(ConstantValue.ExtrasUser);
			isFirstStart = extras.getBoolean(ConstantValue.ExtrasFirstSettings, false);
			loginRequest = extras.getBoolean(ConstantValue.ExtrasLoginRequest, false);
		}
		
		//初回表示タイミングでキーボードを表示させないようにする
		this.getWindow().setSoftInputMode(
				android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		
		initTabPages();

		if(isFirstStart) {
			showFirstSettingsDialog();
		}
		
		UserSettingsHelper.constractUserSettingsView((ViewGroup)findViewById(R.id_settings.tab_user), mUser, 
				loginRequest ? new UserSettingsHelper.OnLoginListener() {
					@Override public void onLogin(User user) {
						Intent intent = new Intent();
						intent.putExtra(ConstantValue.ExtrasUser, user);
						setResult(Activity.RESULT_OK, intent);
						finish();
					}
				} : null);
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
		spec.setContent(R.id_settings.tab_other);
		tabHost.addTab(spec);
	}

	private void showFirstSettingsDialog() {
		new AlertDialog.Builder(this)
			.setTitle("設定")
			.setMessage("ユーザ登録が必要です\n以下いろいろと説明があればいいな")
			.setPositiveButton("OK", null)
			.show();
	}

}
