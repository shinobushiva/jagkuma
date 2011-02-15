package jag.kumamoto.apps.StampRally;

import java.io.IOException;

import jag.kumamoto.apps.gotochi.R;
import aharisu.util.DataGetter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;


/**
 * 
 * スタンプラリーのチェックポイントの情報を表示するアクティビティ
 * 
 * @author aharisu
 *
 */
public class LocationInfoActivity extends Activity{
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.location_infomation);
		
		try {
			((WebView)findViewById(R.id_location_info.webview)) .loadData(
					DataGetter.getHTML(this, R.raw.test_location_info),
					"text/html",
					"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		findViewById(R.id_location_info.go_quiz).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				startActivity(new Intent(LocationInfoActivity.this, QuizActivity.class));
			}
		});
				
		
	}

}
