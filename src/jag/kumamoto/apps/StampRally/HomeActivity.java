package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;

/**
 * 
 * スタンプラリーのホームになるアクティビティ
 * 
 * @author aharisu
 * 
 */
public class HomeActivity extends Activity {
	private static final int RequestFirstStartSettings = 1;

	private final ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 最初にDBのシングルトンインスタンスを作成する
		StampRallyDB.createInstance(getApplicationContext());

		// プリファレンスクラスにコンテキストを設定する
		StampRallyPreferences.setContext(getApplicationContext());

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.home);

		// ヘルプ画面へ遷移
		findViewById(R.id_home.help).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(HomeActivity.this,
								HelpActivity.class);
						User user = StampRallyPreferences.getUser();
						if (user != null) {
							intent.putExtra(ConstantValue.ExtrasUser, user);
						}
						startActivity(intent);
					}
				});

		// Special Thanks画面へ遷移
		findViewById(R.id_home.thanks).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(HomeActivity.this,
								ThanksActivity.class);

						startActivity(intent);
					}
				});

		// マップ画面へ遷移
		findViewById(R.id_home.map).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (StampRallyPreferences.isFirstStampRallyStart()) {
							firstStartAction();
						} else {
							Intent intent = new Intent(HomeActivity.this,
									MapActivity.class);
							User user = StampRallyPreferences.getUser();
							if (user != null) {
								intent.putExtra(ConstantValue.ExtrasUser, user);
							}
							startActivity(intent);
						}
					}
				});

		// 設定画面へ遷移
		findViewById(R.id_home.settings).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(HomeActivity.this,
								SettingsActivity.class);
						User user = StampRallyPreferences.getUser();
						if (user != null) {
							intent.putExtra(ConstantValue.ExtrasUser, user);
						}
						startActivity(intent);
					}
				});

		// コレクション画面へ繊維
		findViewById(R.id_home.collections).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(HomeActivity.this,
								CollectionsActivity.class);
						User user = StampRallyPreferences.getUser();
						if (user != null) {
							intent.putExtra(ConstantValue.ExtrasUser, user);
						}
						startActivity(intent);
					}
				});

		// 到着確認サービスを開始
		startArriveWatcherservice();
	}

	@Override
	protected void onDestroy() {
		stopArriveWatcherService();

		super.onDestroy();
	}

	private void startArriveWatcherservice() {
		Intent intent = new Intent(this, ArriveWatcherService.class);

		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	private void stopArriveWatcherService() {
		unbindService(mConnection);
	}

	private void firstStartAction() {
		StampRallyPreferences.setFlagFirstStampRallyStart();

		Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
		intent.putExtra(ConstantValue.ExtrasFirstSettings, true);
		startActivityForResult(intent, RequestFirstStartSettings);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RequestFirstStartSettings:
			User user = null;
			if (resultCode == Activity.RESULT_OK) {
				user = data.getExtras().getParcelable(ConstantValue.ExtrasUser);
			}

			// Mapアクティビティを起動する
			Intent intent = new Intent(HomeActivity.this, MapActivity.class);
			if (user != null) {
				intent.putExtra(ConstantValue.ExtrasUser, user);
			}
			startActivity(intent);
			break;
		}
	}

}
