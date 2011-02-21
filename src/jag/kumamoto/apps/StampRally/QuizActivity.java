package jag.kumamoto.apps.StampRally;

import java.util.Arrays;
import java.util.Comparator;

import jag.kumamoto.apps.StampRally.Data.QuizChoices;
import jag.kumamoto.apps.StampRally.Data.QuizData;
import jag.kumamoto.apps.StampRally.Data.StampRallyURL;
import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;
import jag.kumamoto.apps.gotochi.R.layout;
import aharisu.util.DataGetter;
import aharisu.widget.ImageCheckBox;
import aharisu.widget.ImageRadioButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * 
 * クイズを表示するアクティビティ
 * 
 * @author aharisu
 *
 */
public class QuizActivity extends Activity{
	
	private User mUser;
	
	private QuizData[] mQuizes;
	private int mIndex = 0;
	
	
	private long mStartTime; //ミリ秒
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		if(extras == null) {
			setResult(Activity.RESULT_CANCELED);
			finish();
			return;
		}
		
		mQuizes = getQuizData(extras);
		if(mQuizes == null) {
			setResult(Activity.RESULT_CANCELED);
			finish();
			return;
		}
		
		mUser = extras.getParcelable(ConstantValue.ExtrasUser);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.quiz);
		
		//クイズデータをもとにビューを構成する
		constractQuizView();
	}
	
	private QuizData[] getQuizData(Bundle extras) {
		Parcelable[] ary = extras.getParcelableArray(ConstantValue.ExtrasQuizData);
		if(ary == null) {
			return null;
		}
		
		QuizData[] quizes = new QuizData[ary.length];
		for(int i = 0;i < ary.length;++i) {
			quizes[i] = (QuizData)ary[i];
		}
		
		Arrays.sort(quizes, new Comparator<QuizData>() {
			public int compare(QuizData object1, QuizData object2) {
				return object1.order - object2.order;
			};
		});
		
		return quizes;
	}
	
	private void constractQuizView() {
		//タイトル設定
		((TextView)findViewById(R.id_quiz.name)).setText(mQuizes[mIndex].title);
		
		//問題文設定
			((WebView)findViewById(R.id_quiz.webview)).loadData(
					mQuizes[mIndex].descriptionHTML,
					"text/html",
					"utf-8");
			
		//OKボタンの設定
		final Button btnOK = (Button)findViewById(R.id_quiz.ok);
		btnOK.setEnabled(false);
		btnOK.setOnClickListener(createOKOnClickListener());
		
		//解答欄設定
		constractChoices();
		
		
		//解答時間を算出するためにスタートの時刻を覚えておく
		mStartTime = System.currentTimeMillis();
	}
	
	private View.OnClickListener createOKOnClickListener() {
		return new View.OnClickListener() {
			@Override public void onClick(View v) {
				//OKボタンを押すまでにかかった時間(ミリ秒）
				long duration = System.currentTimeMillis() - mStartTime;
				
				RadioGroup choicesFrame = (RadioGroup)findViewById(R.id_quiz.frame_choices);
				boolean[] isCheckedAry = new boolean[choicesFrame.getChildCount()];
				for(int i = 0;i < isCheckedAry.length;++i) {
					isCheckedAry[i] = ((CompoundButton)choicesFrame.getChildAt(i)).isChecked();
				}
				
				//正解か否か
				boolean correctness = isCorrectness(isCheckedAry);
				
				//ユーザ設定が行われていればロギングデータを送信する
				if(mUser != null) {
					String loggingQuery = StampRallyURL.getLoggingQuery(
							mUser, mQuizes[mIndex],
							correctness, duration, isCheckedAry);
					Log.i("query", loggingQuery);
					
					//非同期で送信
					asyncSendAnswerLong(loggingQuery);
				}
				
				
				showAnswerDialog(correctness);
			}
		};
	}
	
	private boolean isCorrectness(boolean[] isCheckedAry) {
		boolean correctness = true;
		
		QuizChoices choices = mQuizes[mIndex].choices;
		
		for(int i = 0;i < isCheckedAry.length;++i) {
			if(isCheckedAry[i] != choices.getChoice(i).isCorrectAnswer) {
				correctness = false;
				break;
			}
		}
		
		return correctness;
	}
	
	private void asyncSendAnswerLong(final String query) {
		new AsyncTask<Void, Void, Boolean>() {
			
			@Override protected Boolean doInBackground(Void... params) {
				return DataGetter.getJSONObject(query) != null;
			}
			
			@Override protected void onPostExecute(Boolean result) {
				if(!result) {
					//TODO 送信失敗.どうしよう
				}
			}
			
		}.execute((Void)null);
	}
	
 	private void showAnswerDialog(boolean correctness) {
		AlertDialog.Builder builder =  new AlertDialog.Builder(this)
			.setMessage(correctness ? "正解!!" : "残念。不正解")
			.setPositiveButton("戻る", new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
			.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
		
		if(correctness && (mIndex + 1) < mQuizes.length) {
			builder.setNegativeButton("次の問題へ", new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) {
					++mIndex;
					constractQuizView();
				}
			});
		}
		
		builder.show();
	}
	
	private void constractChoices() {
		final Button btnOK = (Button)findViewById(R.id_quiz.ok);
		
		
		final RadioGroup choicesFrame = (RadioGroup)findViewById(R.id_quiz.frame_choices);
		choicesFrame.removeAllViews();
			
		
		//単一選択か複数選択か
		int layoutId = mQuizes[mIndex].choices.isChoiseSingle() ? 
				layout.quiz_single_choice :
				layout.quiz_muliple_choice;
		
		
		int count = mQuizes[mIndex].choices.getCount();
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for(int i = 0;i < count;++i) {
			QuizChoices.Choice choice = mQuizes[mIndex].choices.getChoice(i);
			
			CompoundButton btn = (CompoundButton)inflater.inflate(layoutId, null);
			
			btn.setId(i);
			btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked) {
						btnOK.setEnabled(true);
					} else {
						int count = choicesFrame.getChildCount();
						boolean isEnabled = false;
						for(int i = 0;i < count;++i) {
							if(((CompoundButton)choicesFrame.getChildAt(i)).isChecked()) {
								isEnabled = true;
								break;
							}
						}
						
						btnOK.setEnabled(isEnabled);
					}
				}
			});
			
			if(choice.type == QuizChoices.Choice.TextTypeRawText) {
				btn.setText(choice.altTypeText);
			} else {
				if(btn instanceof ImageRadioButton) {
					((ImageRadioButton)btn).setImageURL(choice.altTypeText);
				} else if(btn instanceof ImageCheckBox) {
					((ImageCheckBox)btn).setImageURL(choice.altTypeText);
				}
			}
			
			choicesFrame.addView(btn);
		}
	}

}
