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
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import cn.hyphenate.easeui.domain.User;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.hyphenate.easeui.widget.EaseAlertDialog;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

public class AddContactActivity extends BaseActivity{
	private static String TAG = AddContactActivity.class.getSimpleName();
	private EditText editText;
	private String toAddUsername;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(cn.ucai.superwechat.R.layout.em_activity_add_contact);
		editText = (EditText) findViewById(R.id.edit_note);
		setListener();
	}

	private void setListener() {
		findViewById(R.id.AddFriend_Back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.finish(AddContactActivity.this);
			}
		});

		findViewById(R.id.AddFriend_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchContact();
			}
		});
	}


	public void searchContact() {
		final String name = editText.getText().toString();

			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				new EaseAlertDialog(this, cn.ucai.superwechat.R.string.Please_enter_a_username).show();
				return;
			}
			progressDialog = new ProgressDialog(this);
			String stri = getResources().getString(R.string.addcontact_search);
			progressDialog.setMessage(stri);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
		searchAppUser();
	}

	private void searchAppUser() {
		NetDao.searchFriend(this, toAddUsername, new OkHttpUtils.OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				progressDialog.dismiss();
				if (s != null) {
					Result result = ResultUtils.getResultFromJson(s, User.class);
					if (result != null && result.isRetMsg()) {
						User user = (User) result.getRetData();
						if (user != null) {
							MFGT.gotoFriendProfile(AddContactActivity.this,user);
						}
					} else {
						CommonUtils.showLongToast(R.string.search_user_fail);
					}
				} else {
					CommonUtils.showLongToast(R.string.search_user_fail);
				}
			}

			@Override
			public void onError(String error) {
				L.e(TAG,"error="+error);
				progressDialog.dismiss();
			}
		});
	}

	/**
	 *  add contact
	 * @param view
	 */
	public void addContact(View view){
		if(EMClient.getInstance().getCurrentUser().equals(editText.getText().toString())){
			new EaseAlertDialog(this, cn.ucai.superwechat.R.string.not_add_myself).show();
			return;
		}
		
		if(SuperWeChatHelper.getInstance().getContactList().containsKey(editText.getText().toString())){
		    //let the user know the contact already in your contact list
		    if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(editText.getText().toString())){
		        new EaseAlertDialog(this, cn.ucai.superwechat.R.string.user_already_in_contactlist).show();
		        return;
		    }
			new EaseAlertDialog(this, cn.ucai.superwechat.R.string.This_user_is_already_your_friend).show();
			return;
		}
		

		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo use a hardcode reason here, you need let user to input if you like
					String s = getResources().getString(cn.ucai.superwechat.R.string.Add_a_friend);
					EMClient.getInstance().contactManager().addContact(toAddUsername, s);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s1 = getResources().getString(cn.ucai.superwechat.R.string.send_successful);
							Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							String s2 = getResources().getString(cn.ucai.superwechat.R.string.Request_add_buddy_failure);
							Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

}
