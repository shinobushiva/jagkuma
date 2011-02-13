package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.gotochi.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class HomeActivity extends Activity{
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.home);
		
		
		findViewById(R.id_home.help).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, HelpActivity.class));
			}
		});
		
		findViewById(R.id_home.map).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, MapActivity.class));
			}
		});
		
		
		findViewById(R.id_home.settings).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
			}
		});
		
		
		findViewById(R.id_home.collections).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				startActivity(new Intent(HomeActivity.this, CollectionsActivity.class));
			}
		});
		
	}

}
