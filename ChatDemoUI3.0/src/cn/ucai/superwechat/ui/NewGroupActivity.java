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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager.EMGroupOptions;
import com.hyphenate.chat.EMGroupManager.EMGroupStyle;

import cn.hyphenate.easeui.domain.Group;
import cn.hyphenate.easeui.utils.EaseImageUtils;
import cn.hyphenate.easeui.widget.EaseAlertDialog;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.ResultUtils;

import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class NewGroupActivity extends BaseActivity {
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox publibCheckBox;
	private CheckBox memberCheckbox;
	private TextView secondTextView;
	private LinearLayout layoutgroupavatar;
	private ImageView mivGroupavatar;
	private TextView mnewgroupsave;

	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;
	private static final int REQUESTCODE_PICK_MEMBER = 3;

	EMGroup emGroup = null;

	File avatarFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(cn.ucai.superwechat.R.layout.em_activity_new_group);

		initView();
		setListener();

	}

	private void setListener() {
		publibCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					secondTextView.setText(cn.ucai.superwechat.R.string.join_need_owner_approval);
				}else{
					secondTextView.setText(cn.ucai.superwechat.R.string.Open_group_members_invited);
				}
			}
		});
		layoutgroupavatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadHeadPhoto();
			}
		});
		mnewgroupsave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				save();
			}
		});
	}

	private void initView() {
		groupNameEditText = (EditText) findViewById(cn.ucai.superwechat.R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(cn.ucai.superwechat.R.id.edit_group_introduction);
		publibCheckBox = (CheckBox) findViewById(cn.ucai.superwechat.R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(cn.ucai.superwechat.R.id.cb_member_inviter);
		secondTextView = (TextView) findViewById(cn.ucai.superwechat.R.id.second_desc);
		layoutgroupavatar = (LinearLayout) findViewById(R.id.layout_group_avatar);
		mivGroupavatar = (ImageView) findViewById(R.id.ivGroup_avatar);
		mnewgroupsave = (TextView) findViewById(R.id.new_group_save);
	}

	public void save() {
		String name = groupNameEditText.getText().toString();
		if (TextUtils.isEmpty(name)) {
		    new EaseAlertDialog(this, cn.ucai.superwechat.R.string.Group_name_cannot_be_empty).show();
		} else {
			// select from contact list
			startActivityForResult(new Intent(this, GroupPickContactsActivity.class).putExtra("groupName", name), REQUESTCODE_PICK_MEMBER);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {

		switch (requestCode) {
			case REQUESTCODE_PICK:
				if (data == null || data.getData() == null) {
					return;
				}
				startPhotoZoom(data.getData());
				break;
			case REQUESTCODE_CUTTING:
				if (data != null) {
//					updateAppUserAvatar(data);
					setPicToView(data);
				}
				break;
			case REQUESTCODE_PICK_MEMBER:
				if (resultCode == RESULT_OK) {
					createEMGroup(data);
				}
				break;
			default:
				break;
		}
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			createEMGroup(data);
		}
	}

	private void createEMGroup( final Intent data) {
		String st1 = getResources().getString(cn.ucai.superwechat.R.string.Is_to_create_a_group_chat);
		final String st2 = getResources().getString(cn.ucai.superwechat.R.string.Failed_to_create_groups);
		//new group
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();

		new Thread(new Runnable() {
			@Override
			public void run() {
				final String groupName = groupNameEditText.getText().toString().trim();
				String desc = introductionEditText.getText().toString();
				String[] members = data.getStringArrayExtra("newmembers");
				try {
					EMGroupOptions option = new EMGroupOptions();
					option.maxUsers = 200;

					String reason = NewGroupActivity.this.getString(cn.ucai.superwechat.R.string.invite_join_group);
					reason  = EMClient.getInstance().getCurrentUser() + reason + groupName;

					if(publibCheckBox.isChecked()){
						option.style = memberCheckbox.isChecked() ? EMGroupStyle.EMGroupStylePublicJoinNeedApproval : EMGroupStyle.EMGroupStylePublicOpenJoin;
					}else{
						option.style = memberCheckbox.isChecked()?EMGroupStyle.EMGroupStylePrivateMemberCanInvite:EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
					}

					L.e("NewGroupActivity.emGroup.groupName",groupName);
					L.e("NewGroupActivity.emGroup.desc",desc);
					L.e("NewGroupActivity.emGroup.members",members.toString());
					L.e("NewGroupActivity.emGroup.reason",reason);
					L.e("NewGroupActivity.emGroup.option",option.toString());
					emGroup = EMClient.getInstance().groupManager().createGroup(groupName, desc, members, reason, option);
					emGroup.getGroupId();

					createAppGroup();
				} catch (final HyphenateException e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}

			}
		}).start();
	}

	private void createAppGroup() {
		OkHttpUtils.OnCompleteListener<String> listener = new OkHttpUtils.OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				if (s != null) {
					Result result = ResultUtils.getResultFromJson(s, Group.class);
					if (result != null && result.isRetMsg()) {
						if (emGroup != null && emGroup.getMembers() != null && emGroup.getMembers().size() > 1) {
							addGroupMember();
						} else {
							createGroupSuccess();
						}
					} else {
						progressDialog.dismiss();
						CommonUtils.showLongToast(R.string.Failed_to_create_groups);
					}
				} else {
					progressDialog.dismiss();
					CommonUtils.showLongToast(R.string.Failed_to_create_groups);
				}
			}

			@Override
			public void onError(String error) {
				progressDialog.dismiss();
				CommonUtils.showLongToast(R.string.Failed_to_create_groups);
			}
		};
		if (avatarFile == null) {
			NetDao.createGroup(this, emGroup,listener);
		} else {
			NetDao.createGroup(this, emGroup, avatarFile,listener);
		}
	}

	private void addGroupMember() {
		NetDao.addGroupMember(this, emGroup, new OkHttpUtils.OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				if (s != null) {
					Result result = ResultUtils.getResultFromJson(s, Group.class);
					if (result != null && result.isRetMsg()) {
						createGroupSuccess();
					} else {
						progressDialog.dismiss();
						CommonUtils.showLongToast(R.string.Failed_to_create_groups);
					}
				} else {
					progressDialog.dismiss();
					CommonUtils.showLongToast(R.string.Failed_to_create_groups);
				}
			}

			@Override
			public void onError(String error) {
				progressDialog.dismiss();
				CommonUtils.showLongToast(R.string.Failed_to_create_groups);
			}
		});
	}


	private void createGroupSuccess() {
		runOnUiThread(new Runnable() {
			public void run() {
				progressDialog.dismiss();
				setResult(RESULT_OK);
				finish();
			}
		});
		progressDialog.dismiss();

	}

	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(cn.ucai.superwechat.R.string.dl_title_upload_photo);
		builder.setItems(new String[] { getString(cn.ucai.superwechat.R.string.dl_msg_take_photo), getString(cn.ucai.superwechat.R.string.dl_msg_local_upload) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
							case 0:
								Toast.makeText(NewGroupActivity.this, getString(cn.ucai.superwechat.R.string.toast_no_support),
										Toast.LENGTH_SHORT).show();
								break;
							case 1:
								Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
								pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
								startActivityForResult(pickIntent, REQUESTCODE_PICK);
								break;
							default:
								break;
						}
					}
				});
		builder.create().show();
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}

	/**
	 * save the picture data
	 *
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			mivGroupavatar.setImageDrawable(drawable);
			saveBitmapFile(picdata);
		}

	}

	public void saveBitmapFile(Intent picavatar) {
		Bundle extras = picavatar.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			String imagepath = EaseImageUtils.getImagePath(System.currentTimeMillis()+ I.AVATAR_SUFFIX_JPG);
			File file = new File(imagepath);
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			avatarFile =  file;
		}
	}

}
