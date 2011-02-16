package jag.kumamoto.apps.StampRally;

import java.io.IOException;

import jag.kumamoto.apps.StampRally.Data.QuizData;
import jag.kumamoto.apps.gotochi.R;
import aharisu.util.DataGetter;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Window;
import android.webkit.WebView;


/**
 * 
 * クイズを表示するアクティビティ
 * 
 * @author aharisu
 *
 */
public class QuizActivity extends Activity{
	
	private QuizData[] mQuizes;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if(extras == null) {
			finishActivity(Activity.RESULT_CANCELED);
			return;
		}
		
		Parcelable[] ary = extras.getParcelableArray(ConstantValue.ExtrasQuizData);
		if(ary == null) {
			finishActivity(Activity.RESULT_CANCELED);
			return;
		}
		mQuizes = new QuizData[ary.length];
		for(int i = 0;i < ary.length;++i) {
			mQuizes[i] = (QuizData)ary[i];
		}
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.quiz);
		
		try {
			((WebView)findViewById(R.id_quiz.webview)).loadData(
					DataGetter.getHTML(this, R.raw.test_quiz),
					"text/html",
					"utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
