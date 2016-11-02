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

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

//import butterknife.BindView;    可以通过手动导入ButterKnife的包来实现ButterKnife的使用
//import butterknife.ButterKnife;
//import butterknife.OnClick;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.MD5;
import cn.ucai.superwechat.utils.MFGT;

import com.hyphenate.exceptions.HyphenateException;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * register screen
 *
 */
public class RegisterActivity extends BaseActivity {
	private EditText userNameEditText;
	private EditText userNickEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	Button mRegisterButton;
	ImageView mRegisterBack;
	ProgressDialog pd = null;

	RegisterActivity mContext;

	String username;
	String usernick;
	String pwd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(cn.ucai.superwechat.R.layout.em_activity_register);
		super.onCreate(savedInstanceState);
		mContext = this;
		initView();
		setLitener();
	}

	private void setLitener() {
		mRegisterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
		mRegisterBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.finish(mContext);
			}
		});
	}

	private void initView() {
		userNameEditText = (EditText) findViewById(R.id.UserNameRegister);
		userNickEditText = (EditText) findViewById(R.id.UserNickRegister);
		passwordEditText = (EditText) findViewById(R.id.UserPasswordRegister);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
		mRegisterButton = (Button) findViewById(R.id.RegisterButton);
		mRegisterBack = (ImageView) findViewById(R.id.RegisterBack);
	}


	public void register() {
		username = userNameEditText.getText().toString().trim();
		usernick = userNickEditText.getText().toString().trim();
		pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();


		if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, getResources().getString(cn.ucai.superwechat.R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (username.matches(("[a-zA-Z]\\w{5,15}"))) {
			Toast.makeText(this, getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}else if (TextUtils.isEmpty(usernick)) {
			Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
			userNickEditText.requestFocus();
			return;
		}else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, getResources().getString(cn.ucai.superwechat.R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(cn.ucai.superwechat.R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, getResources().getString(cn.ucai.superwechat.R.string.Two_input_password), Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			pd = new ProgressDialog(this);
			pd.setMessage(getResources().getString(cn.ucai.superwechat.R.string.Is_the_registered));
			pd.show();

			registerAppService();

		}
	}

	private void registerAppService() {
		NetDao.register(mContext, username, usernick, pwd, new OkHttpUtils.OnCompleteListener<Result>() {
			@Override
			public void onSuccess(Result result) {
				if (result == null) {
					unregisterAppService();
				} else {
					if (result.isRetMsg()) {
						registerEMService();
					} else if (result.getRetCode()== I.MSG_REGISTER_USERNAME_EXISTS) {
							CommonUtils.showMsgShortToast(result.getRetCode());
							pd.dismiss();
						} else {
							unregisterAppService();
						}

				}
			}

			@Override
			public void onError(String error) {
				pd.dismiss();
			}
		});
	}

	private void unregisterAppService() {
		NetDao.unregister(mContext, username, new OkHttpUtils.OnCompleteListener<Result>() {
			@Override
			public void onSuccess(Result result) {
				pd.dismiss();
			}

			@Override
			public void onError(String error) {
				pd.dismiss();
			}
		});
	}

	private void registerEMService() {
		new Thread(new Runnable() {
			public void run() {
				try {
					// call method in SDK                                   对环信注册时的密码进行加密处理
					EMClient.getInstance().createAccount(username, MD5.getMessageDigest(pwd));
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
							// save current user
							SuperWeChatHelper.getInstance().setCurrentUserName(username);
							Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.Registered_successfully), Toast.LENGTH_SHORT).show();
							MFGT.finish(mContext);
						}
					});
				} catch (final HyphenateException e) {
					unregisterAppService();
					runOnUiThread(new Runnable() {
						public void run() {
							if (!RegisterActivity.this.isFinishing())
								pd.dismiss();
								int errorCode=e.getErrorCode();
							if(errorCode==EMError.NETWORK_ERROR){
								Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.network_anomalies), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ALREADY_EXIST){
								Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.User_already_exists), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_AUTHENTICATION_FAILED){
								Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
							}else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
								Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.illegal_user_name),Toast.LENGTH_SHORT).show();
							}else{
								Toast.makeText(getApplicationContext(), getResources().getString(cn.ucai.superwechat.R.string.Registration_failed), Toast.LENGTH_SHORT).show();
							}
						}
					});
				}
			}
		}).start();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		MFGT.finish(mContext);
	}
}
