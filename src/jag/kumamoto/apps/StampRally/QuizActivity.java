package jag.kumamoto.apps.StampRally;

import java.io.IOException;

import jag.kumamoto.apps.gotochi.R;
import aharisu.util.DataGetter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class QuizActivity extends Activity{
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.quiz);
		
		try {
			((WebView)findViewById(R.id_quiz.webview)).loadData(
					DataGetter.readHTML(this, R.raw.test_quiz),
					"text/html",
					"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
