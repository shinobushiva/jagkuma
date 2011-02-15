package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.gotochi.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * 
 * 獲得ポイントやアイテムを一覧表示するアクティビティ
 * 
 * @author aharisu
 *
 */
public class CollectionsActivity extends Activity{
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.collections);
	}

}
