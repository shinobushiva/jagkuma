package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * 
 * スタンプラリーのホームになるアクティビティ
 * 
 * @author aharisu
 *
 */
public class HomeActivity extends Activity{
	
	
	private User mUser;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//最初にDBのシングルトンインスタンスを作成する
		StampRallyDB.createInstance(getApplicationContext());
		
		//プリファレンスクラスにコンテキストを設定する
		StampRallyPreferences.setContext(getApplicationContext());
		
		mUser = StampRallyPreferences.getUser();
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.home);
		
		
		//ヘルプ画面へ遷移
		findViewById(R.id_home.help).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
				if(mUser != null) {
					intent.putExtra(ConstantValue.ExtrasUser, mUser);
				}
				startActivity(intent);
			}
		});
		
		
		//マップ画面へ遷移
		findViewById(R.id_home.map).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, MapActivity.class);
				if(mUser != null) {
					intent.putExtra(ConstantValue.ExtrasUser, mUser);
				}
				startActivity(intent);
			}
		});
		
		
		//設定画面へ遷移
		findViewById(R.id_home.settings).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
				if(mUser != null) {
					intent.putExtra(ConstantValue.ExtrasUser, mUser);
				}
				startActivity(intent);
			}
		});
		
		
		//コレクション画面へ繊維
		findViewById(R.id_home.collections).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, CollectionsActivity.class);
				if(mUser != null) {
					intent.putExtra(ConstantValue.ExtrasUser, mUser);
				}
				startActivity(intent);
			}
		});
	}

}
