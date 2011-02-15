package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.gotochi.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;



/**
 * 
 * 設定画面のアクティビティ
 * 
 * @author aharisu
 *
 */
public class SettingsActivity extends Activity{
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
	}

}
