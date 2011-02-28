package jag.kumamoto.apps.StampRally;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import aharisu.util.DataGetter;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import jag.kumamoto.apps.StampRally.Data.StampRallyURL;
import jag.kumamoto.apps.StampRally.Data.User;
import jag.kumamoto.apps.gotochi.R;

final class UserSettingsHelper {
	public static interface OnLoginListener {
		public void onLogin(User user);
	}
	
	private final ViewGroup mLayout;
	private final OnLoginListener mListener;
	private User mUser;
	
	private UserSettingsHelper(ViewGroup layout, User user, OnLoginListener listner) {
		mLayout = layout;
		mUser = user;
		mListener = listner;
		
		if(mUser == null) {
			constractLoginView();
		} else {
			constractUserInfoModifyView();
		}
	}
	
	public static void constractUserSettingsView(ViewGroup layout, User user, OnLoginListener listener) {
		//この中で自動的に構築される
		new UserSettingsHelper(layout, user, listener);
	}
	
	
	/*
	 * 
	 * ユーザ情報修正ビュー
	 * 
	 */
	
	private void constractUserInfoModifyView() {
		//タイトル設定
		((TextView)mLayout.findViewById(R.id_settings.user_setting_title)).setText("ユーザー情報編集");
		
		//トークンリストを非表示
		mLayout.findViewById(R.id_settings.select_token_frame).setVisibility(View.GONE);
		
		//ニックネームと性別設定欄を表示
		mLayout.findViewById(R.id_settings.registration_frame).setVisibility(View.VISIBLE);
		
		//ニックネーム設定
		EditText edtNickname = (EditText)mLayout.findViewById(R.id_settings.nickname);
		edtNickname.setText(mUser.nickname);
		edtNickname.addTextChangedListener(new TextWatcher() {
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s.length() == 0) {
					//長さ０は無条件で無効
					((Button)mLayout.findViewById(R.id_settings.ok)).setEnabled(false);
				} else {
					checkModifyEnable();
				}
			}
			
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override public void afterTextChanged(Editable s) {
			}
		});
		
		//性別設定
		RadioGroup genderGroup =  (RadioGroup)mLayout.findViewById(R.id_settings.gender_frame);
		genderGroup.check(genderToId(mUser.gender));
		genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override public void onCheckedChanged(RadioGroup group, int checkedId) {
				checkModifyEnable();
			}
		});
		
		//okボタンを設定
		Button btnModify =  (Button)mLayout.findViewById(R.id_settings.ok);
		btnModify.setText("変更");
		btnModify.setEnabled(false);
		btnModify.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				int gender = idToGender(((RadioGroup)mLayout.findViewById(R.id_settings.gender_frame)).getCheckedRadioButtonId());
				String nickname = ((EditText)mLayout.findViewById(R.id_settings.nickname)).getText().toString();
				
				if(mUser.gender != gender || !mUser.nickname.equals(nickname)) {
					modifyUser(new User(mUser.token, gender, nickname));
				}
			}
		});
		
		//ログアウトするテキストを設定
		TextView tvwLogout = (TextView)mLayout.findViewById(R.id_settings.change_view);
		setUnderlineText(tvwLogout, "ログアウトする");
		//テキストをタッチしたときに背景色を変える
		tvwLogout.setOnTouchListener(createChangeViewOnTouchListener());
		//テキストをタッチしたときに画面を変更する
		tvwLogout.setOnClickListener(createChangeViewOnClickListener());
	}
	
	private int genderToId(int gender) {
		return gender == User.Female ? R.id_settings.gender_female :
				gender == User.Male ? R.id_settings.gender_male :
				R.id_settings.gender_unknown;
	}
	
	private int idToGender(int id) {
		return id == R.id_settings.gender_female ? User.Female :
			id == R.id_settings.gender_male ? User.Male :
			User.Unknown;
	}
	
	private void setUnderlineText(TextView tvw, String text) {
		//テキストに下線を引く
		Spannable span = Spannable.Factory.getInstance().newSpannable(text);
		UnderlineSpan us = new UnderlineSpan();
		span.setSpan(us, 0, text.length(), span.getSpanFlags(us));
		tvw.setText(span, TextView.BufferType.SPANNABLE);
	}
	
	private void checkModifyEnable() {
		//性別とニックネームどちらかがもとと違えば有効にする	
		((Button)mLayout.findViewById(R.id_settings.ok)).setEnabled(
			genderToId(mUser.gender) != ((RadioGroup)mLayout.findViewById(R.id_settings.gender_frame)).getCheckedRadioButtonId() ||
			 !mUser.nickname.equals(((EditText)mLayout.findViewById(R.id_settings.nickname)).getText().toString()));
	}
	
	private View.OnTouchListener createChangeViewOnTouchListener() {
		return new View.OnTouchListener() {
				private Drawable mBackground;
				
				@Override public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mBackground = v.getBackground();
						v.setBackgroundColor(0xffadfec4);
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						v.setBackgroundDrawable(mBackground);
						break;
					}
				return false;
			}
		};
	}
	
	private View.OnClickListener createChangeViewOnClickListener() {
		return new View.OnClickListener() {
			@Override public void onClick(View v) {
				if(mUser != null) {
					mUser = null;
					//ユーザ情報を消去する
					StampRallyPreferences.clearUser();
					constractLoginView();
				} else {
					if(mLayout.findViewById(R.id_settings.registration_frame).getVisibility() == View.GONE) {
						constractRegistrationView();
					} else {
						constractLoginView();
					}
				}
			}
		};
	}
	
	private void modifyUser(final User user) {
		final ProgressDialog dialog = new ProgressDialog(mLayout.getContext());
		dialog.setMessage("変更中です");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();

		new AsyncTask<Void, Void, Boolean>() {

			@Override protected Boolean doInBackground(Void... params) {
				JSONObject obj = DataGetter.getJSONObject(StampRallyURL.getRegistrationQuery(user));

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
					mUser = user;

					Toast.makeText(mLayout.getContext(), "変更しました", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mLayout.getContext(), "変更に失敗しました", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute((Void)null);
	}
	
	/*
	 * 
	 * ログインビュー
	 * 
	 */
	
	private void constractLoginView() {
		//タイトル設定
		((TextView)mLayout.findViewById(R.id_settings.user_setting_title)).setText("ログイン");
		
		//トークンリストを表示
		mLayout.findViewById(R.id_settings.select_token_frame).setVisibility(View.VISIBLE);
		
		//トークンリストを設定&初期化
		((TextView)mLayout.findViewById(R.id_settings.token_label)).setText("登録したアドレスを選択してください");
		initTokenFrame();
		
		//ニックネームと性別設定欄を非表示
		mLayout.findViewById(R.id_settings.registration_frame).setVisibility(View.GONE);
		
		//okボタンを設定
		Button btnLogin =  (Button)mLayout.findViewById(R.id_settings.ok);
		btnLogin.setText("ログイン");
		btnLogin.setEnabled(true);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				login(getSelectedToken());
			}
		});
		
		//新規登録するテキストを設定
		TextView tvwGotoRegistration = (TextView)mLayout.findViewById(R.id_settings.change_view);
		setUnderlineText(tvwGotoRegistration, "新規登録はこちら");
		//テキストをタッチしたときに背景色を変える
		tvwGotoRegistration.setOnTouchListener(createChangeViewOnTouchListener());
		//テキストをタッチしたときに画面を変更する
		tvwGotoRegistration.setOnClickListener(createChangeViewOnClickListener());
	}
	
	private void login(final String token) {
		final ProgressDialog dialog = new ProgressDialog(mLayout.getContext());
		dialog.setMessage("認証中です");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();

		new AsyncTask<Void, Void, User>() {

			@Override protected User doInBackground(Void... params) {
				JSONObject obj = DataGetter.getJSONObject(StampRallyURL.getUserInfoQuery(token));

				if(obj != null) {
					try {
						return User.decodeJSONObject(token, obj);
					} catch(JSONException e) {
						e.printStackTrace();
					}
				}
				
				return null;
			}

			@Override protected void onPostExecute(User result) {
				dialog.dismiss();

				if(result != null) {
					StampRallyPreferences.setUser(result);
					mUser = result;

					if(mListener != null) {
						mListener.onLogin(mUser);
					}
					constractUserInfoModifyView();

					Toast.makeText(mLayout.getContext(), "ログインしました", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mLayout.getContext(), "ログインに失敗しました", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute((Void)null);
	}

	
	/*
	 * 
	 * 新規登録ビュー
	 * 
	 */
	
	private void constractRegistrationView() {
		//タイトル設定
		((TextView)mLayout.findViewById(R.id_settings.user_setting_title)).setText("新規登録");
		
		//トークンリストを表示
		mLayout.findViewById(R.id_settings.select_token_frame).setVisibility(View.VISIBLE);
		
		//トークンリストを設定&初期化
		((TextView)mLayout.findViewById(R.id_settings.token_label)).setText("登録するアドレスを選択してください");
		initTokenFrame();
		
		//ニックネームと性別設定欄を表示
		mLayout.findViewById(R.id_settings.registration_frame).setVisibility(View.VISIBLE);
		
		//ニックネーム設定
		EditText edtNickname = (EditText)mLayout.findViewById(R.id_settings.nickname);
		edtNickname.addTextChangedListener(createEditTextWatcher());
		
		//okボタンを設定
		Button btnOK = (Button)mLayout.findViewById(R.id_settings.ok);
		btnOK.setText("新規登録");
		btnOK.setEnabled(false);
		btnOK.setOnClickListener(createRegistrationOnClickListener());
		
		//ログインするテキストを設定
		TextView tvwGotoLogin = (TextView)mLayout.findViewById(R.id_settings.change_view);
		setUnderlineText(tvwGotoLogin, "ログインはこちら");
		//テキストをタッチしたときに背景色を変える
		tvwGotoLogin.setOnTouchListener(createChangeViewOnTouchListener());
		//テキストをタッチしたときに画面を変更する
		tvwGotoLogin.setOnClickListener(createChangeViewOnClickListener());
	}

	private TextWatcher createEditTextWatcher() {
		return new TextWatcher() {
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) {
				EditText edtNickname = (EditText)mLayout.findViewById(R.id_settings.nickname);

				//入力欄が空白でなければOKボタンを有効にする
				boolean enabled = edtNickname.getText().length() != 0;
				mLayout.findViewById(R.id_settings.ok).setEnabled(enabled);
			}

			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override public void afterTextChanged(Editable s) {
			}
		};
	}
	
	private View.OnClickListener createRegistrationOnClickListener() {
		return new View.OnClickListener() {
			@Override public void onClick(View v) {
				String token = getSelectedToken();
				int gender = idToGender(((RadioGroup)mLayout.findViewById(R.id_settings.gender_frame)).getCheckedRadioButtonId());
				String nickname = ((EditText)mLayout.findViewById(R.id_settings.nickname)).getText().toString();
				
				registration(new User(token, gender, nickname));
			}
		};
	}
	
	private void registration(final User user) {
		final ProgressDialog dialog = new ProgressDialog(mLayout.getContext());
		dialog.setMessage("登録中です");
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();

		new AsyncTask<Void, Void, Boolean>() {

			@Override protected Boolean doInBackground(Void... params) {
				JSONObject obj = DataGetter.getJSONObject(StampRallyURL.getRegistrationQuery(user));

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
					mUser = user;

					
					if(mListener != null) {
						mListener.onLogin(user);
					}
					constractUserInfoModifyView();
					
					Toast.makeText(mLayout.getContext(), "認証しました", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mLayout.getContext(), "認証に失敗しました", Toast.LENGTH_SHORT).show();
				}
			}

		}.execute((Void)null);
	}
	
	private String getSelectedToken() {
		int id = ((RadioGroup)mLayout.findViewById(R.id_settings.token_frame)).getCheckedRadioButtonId();
		if(id < 0) {
			throw new RuntimeException("tokenが選択されていません.実装のエラー");
		}
		
		return getGoogleAccounts()[id];
	}
	
	private boolean initTokenFrame() {
		RadioGroup group = (RadioGroup)mLayout.findViewById(R.id_settings.token_frame);
		group.clearCheck();
		group.removeAllViews();
		
		String[] tokens = getGoogleAccounts();
		if(tokens.length == 0) {
			//TODO どうしようかな
			return false;
		}
		
		LayoutInflater inflater = (LayoutInflater)mLayout.getContext()
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for(int i = 0;i < tokens.length;++i) {
			RadioButton btn = (RadioButton)inflater.inflate(R.layout.settings_token_radiobutton, null);
			
			RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
					RadioGroup.LayoutParams.FILL_PARENT,
					RadioGroup.LayoutParams.WRAP_CONTENT);
			btn.setLayoutParams(params);
			btn.setText(tokens[i]);
			btn.setId(i);
			
			group.addView(btn);
		}
		group.check(0);
		
		return true;
	}
	
	private String[] getGoogleAccounts() {
		ArrayList<String> accountNames = new ArrayList<String>();
		Account[] accounts = AccountManager.get(mLayout.getContext()).getAccounts();
		for (Account account : accounts) {
			if(account.type.equals("com.google")) {
				accountNames.add(account.name);
			}
		}
		String[] result = new String[accountNames.size()];
		accountNames.toArray(result);
		return result;
	}
		
}
