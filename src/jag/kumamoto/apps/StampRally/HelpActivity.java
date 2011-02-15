package jag.kumamoto.apps.StampRally;

import java.io.IOException;

import jag.kumamoto.apps.gotochi.R;
import aharisu.util.DataGetter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

/**
 * 
 * スタンプラリーの操作方法などヘルプ画面のアクティビティ
 * 
 * @author aharisu
 *
 */
public class HelpActivity extends Activity {
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help);
		
		try {
			((WebView)findViewById(R.id_help.webview)).loadData(
					DataGetter.getHTML(this, R.raw.help),
					"text/html",
					"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
