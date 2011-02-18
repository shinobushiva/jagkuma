package jag.kumamoto.apps.StampRally;

import org.json.JSONException;
import org.json.JSONObject;

import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;
import aharisu.util.DataGetter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;



/**
 * 
 * 設定画面のアクティビティ
 * 
 * @author aharisu
 *
 */
public class SettingsActivity extends Activity{
	private User mUser;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			mUser = extras.getParcelable(ConstantValue.ExtrasUser);
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.settings);
		
		boolean visiblePassword = StampRallyPreferences.getVisiblePassword();
		
		TextWatcher watcher = createEditTextWatcher();
		
		
		//パスワード入力を設定
		final EditText password = (EditText)findViewById(R.id_settings.password);
		password.addTextChangedListener(watcher);
		password.setInputType(InputType.TYPE_CLASS_TEXT | 
				(visiblePassword ?
						InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
						InputType.TYPE_TEXT_VARIATION_PASSWORD));
		
		
		//ニックネーム入力を設定
		EditText nickname = (EditText)findViewById(R.id_settings.nickname);
		nickname.addTextChangedListener(watcher);
		
		
		//すでに入力済みのユーザデータがあれば初期値として設定
		if(mUser != null) {
			password.setText(mUser.token);
			
			nickname.setText(mUser.nickname);
			((RadioGroup)findViewById(R.id_settings.gender_frame)).check(
					mUser.gender == User.Female ? R.id_settings.gender_female :
					mUser.gender == User.Male ? R.id_settings.gender_male :
					R.id_settings.gender_unknown);
		}
		
		
		//パスワードの文字列を表示するか否かのチェックボックス設定
		CheckBox cbxVisiblePassword = (CheckBox)findViewById(R.id_settings.visible_password);
		cbxVisiblePassword.setChecked(visiblePassword);
		cbxVisiblePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				//パスワードの可視・非可視を切り替える
				int start = password.getSelectionStart();
				int end = password.getSelectionEnd();
				password.setInputType(InputType.TYPE_CLASS_TEXT | 
						(isChecked ?
								InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
								InputType.TYPE_TEXT_VARIATION_PASSWORD));
				password.setSelection(start, end);
				
				StampRallyPreferences.setVisiblePassword(isChecked);
			}
		});
		
		
		//OKボタンの設定
		View.OnClickListener okOnClickListener = createOKOnClickListener();
		View aboveOk = findViewById(R.id_settings.above_ok);
		aboveOk.setOnClickListener(okOnClickListener);
		aboveOk.setEnabled(mUser != null);
		
		View belowOk = findViewById(R.id_settings.below_ok);
		belowOk.setOnClickListener(okOnClickListener);
		belowOk.setEnabled(mUser != null);
	}
	
	}
	
	private TextWatcher createEditTextWatcher() {
		return new TextWatcher() {
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				EditText password = (EditText)findViewById(R.id_settings.password);
				EditText nickname = (EditText)findViewById(R.id_settings.nickname);
				
				//入力欄両方とも空白でなければOKボタンを有効にする
				boolean enabled = password.getText().length() != 0 && nickname.getText().length() != 0;
				findViewById(R.id_settings.above_ok).setEnabled(enabled);
				findViewById(R.id_settings.below_ok).setEnabled(enabled);
			}
			
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override public void afterTextChanged(Editable s) {
			}
		};
	}
	
	private View.OnClickListener createOKOnClickListener() {
		return new View.OnClickListener() {
			@Override public void onClick(View v) {
				String token = ((EditText)findViewById(R.id_settings.password)).getText().toString();
				int checkedId = ((RadioGroup)findViewById(R.id_settings.gender_frame)).getCheckedRadioButtonId();
				int gender = checkedId == R.id_settings.gender_female ? User.Female :
					checkedId == R.id_settings.gender_male ? User.Male :
					User.Unknown;
				String nickname = ((EditText)findViewById(R.id_settings.nickname)).getText().toString();
				
				AsyncCerticication(new User(token, gender, nickname));
			}
		};
	}
	
	private void AsyncCerticication(final User user) {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage("認証中です");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();
		
		new AsyncTask<Void, Void, Boolean>() {
			
			@Override protected Boolean doInBackground(Void... params) {
				JSONObject obj = DataGetter.getJSONObject(user.getRegistrationQueryURL());
				
				if(obj != null) {
					try {
						return User.isSuccess(obj);
					} catch(JSONException e) {
						e.printStackTrace();
					}
				}
				return false;
			}
			
			@Override protected void onPostExecute(Boolean result) {
				dialog.dismiss();
				
				if(result) {
					StampRallyPreferences.setUser(user);
					
					Intent intent = new Intent();
					intent.putExtra(ConstantValue.ExtrasUser, user);
					setResult(Activity.RESULT_OK, intent);
					finish();
					
					Toast.makeText(getApplicationContext(), "認証しました", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "認証に失敗しました", Toast.LENGTH_SHORT).show();
				}
			}
			
		}.execute((Void)null);
	}

}
