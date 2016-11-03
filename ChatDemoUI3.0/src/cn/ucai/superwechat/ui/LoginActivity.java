/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.db.SuperWeChatDBManager;
import cn.hyphenate.easeui.utils.EaseCommonUtils;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.domain.User;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * Login screen
 * 
 */
public class LoginActivity extends BaseActivity {
	private static final String TAG = LoginActivity.class.getSimpleName();
	public static final int REQUEST_CODE_SETNICK = 1;
	private EditText usernameEditText;
	private EditText passwordEditText;
	ImageView mLoginBack;
	Button mLoginButton;
	Button mLoginButtonRegister;

	private boolean progressShow;
	private boolean autoLogin = false;

	LoginActivity mContext;
	String currentUsername;
	String currentPassword;
	ProgressDialog pd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		L.e(TAG,"LoginActivity.onCreate().start");
		super.onCreate(savedInstanceState);
		mContext = this;
		// enter the main activity if already logged in
		if (SuperWeChatHelper.getInstance().isLoggedIn()) {
			autoLogin = true;
			startActivity(new Intent(LoginActivity.this, MainActivity.class));
			return;
		}
		setContentView(cn.ucai.superwechat.R.layout.em_activity_login);
		initView();
		setListener();
		L.e(TAG,"LoginActivity.onCreate().end");
	}

	private void initView() {
		L.e(TAG,"LoginActivity.initView().tart");
		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		mLoginBack = (ImageView) findViewById(R.id.LoginBack);
		mLoginButton = (Button) findViewById(R.id.LoginButton);
		mLoginButtonRegister = (Button) findViewById(R.id.LoginButtonRegister);

		if (SuperWeChatHelper.getInstance().getCurrentUsernName() != null) {
			usernameEditText.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
		}
		L.e(TAG,"LoginActivity.initView().end");
	}

	private void setListener() {
		L.e(TAG,"LoginActivity.setListener().start");
		//  完成返回箭头，登录，注册的点击方法
		mLoginBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.finish(mContext);
			}
		});
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				login();
			}
		});

		mLoginButtonRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.gotoRegisterActivity(mContext);
			}
		});


		L.e(TAG,"LoginActivity.setListener().onClick.end");
		// if user changed, clear the password
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		L.e(TAG,"LoginActivity.setListener().end");
	}

	/**
	 * login
	 */
	public void login() {
		L.e(TAG,"LoginActivity.login().start");
		if (!EaseCommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, cn.ucai.superwechat.R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		currentUsername = usernameEditText.getText().toString().trim();
		currentPassword = passwordEditText.getText().toString().trim();

		if (TextUtils.isEmpty(currentUsername)) {
			Toast.makeText(this, cn.ucai.superwechat.R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(currentPassword)) {
			Toast.makeText(this, cn.ucai.superwechat.R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
			return;
		}

		progressShow = true;
		pd = new ProgressDialog(LoginActivity.this);
		pd.setCanceledOnTouchOutside(false);
		pd.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				Log.d(TAG, "EMClient.getInstance().onCancel");
				progressShow = false;
			}
		});
		pd.setMessage(getString(cn.ucai.superwechat.R.string.Is_landing));
		pd.show();

		loginEMService();
	}

	private void loginEMService() {

		L.e(TAG,"LoginActivity.loginEMService().start");
		// After logout，the DemoDB may still be accessed due to async callback, so the DemoDB will be re-opened again.
		// close it before login to make sure DemoDB not overlap
		SuperWeChatDBManager.getInstance().closeDB();

		// reset current user name before login
		SuperWeChatHelper.getInstance().setCurrentUserName(currentUsername);

		final long start = System.currentTimeMillis();
		// call login method
		Log.d(TAG, "EMClient.getInstance().login");
		EMClient.getInstance().login(currentUsername, MD5.getMessageDigest(currentPassword), new EMCallBack() {

			@Override
			public void onSuccess() {
				Log.d(TAG, "login: onSuccess");
				loginAppService();

			}

			@Override
			public void onProgress(int progress, String status) {
				Log.d(TAG, "login: onProgress");
			}

			@Override
			public void onError(final int code, final String message) {
				Log.d(TAG, "login: onError: " + code);
				if (!progressShow) {
					return;
				}
				runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						Toast.makeText(getApplicationContext(), getString(cn.ucai.superwechat.R.string.Login_failed) + message,
								Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}

	private void loginAppService() {
		L.e(TAG,"LoginActivity.loginAppService().start");
		NetDao.login(mContext, currentUsername, currentPassword, new OkHttpUtils.OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				if (s != null && s != "") {
					Result result = ResultUtils.getResultFromJson(s, User.class);
					if (result != null && result.isRetMsg()) {
						User user = (User) result.getRetData();
						if (user != null) {
							UserDao dao = new UserDao(mContext);
							dao.saveUser(user);
							SuperWeChatHelper.getInstance().getCurrentUsernName();
							loginSuccess();
						}
					} else {
						pd.dismiss();
						L.e(TAG,"login fail"+result);
					}
				}
			}

			@Override
			public void onError(String error) {
				pd.dismiss();
				L.e(TAG,"login fail");
			}
		});
		loginSuccess();
	}

	private void loginSuccess() {
		L.e(TAG,"LoginActivity.loginSuccess().start");
		// ** manually load all local groups and conversation
		EMClient.getInstance().groupManager().loadAllGroups();
		EMClient.getInstance().chatManager().loadAllConversations();

		// update current user's display name for APNs
		boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
				SuperWeChatApplication.currentUserNick.trim());
		if (!updatenick) {
			Log.e("LoginActivity", "update current user nick fail");
		}

		if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
			pd.dismiss();
		}
		// get user's info (this should be get from App's server or 3rd party service)
		SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

		Intent intent = new Intent(LoginActivity.this,MainActivity.class);
		startActivity(intent);

		finish();
	}



	@Override
	protected void onResume() {
		super.onResume();
		if (autoLogin) {
			return;
		}
		if (SuperWeChatHelper.getInstance().getCurrentUsernName() != null) {
			usernameEditText.setText(SuperWeChatHelper.getInstance().getCurrentUsernName());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pd.dismiss();
	}
}
