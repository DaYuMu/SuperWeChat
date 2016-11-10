package cn.ucai.superwechat.ui;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.bumptech.glide.Glide;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;

import cn.hyphenate.easeui.domain.User;
import cn.hyphenate.easeui.utils.EaseImageUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.hyphenate.easeui.domain.EaseUser;
import cn.hyphenate.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.CommonUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfileActivity extends BaseActivity implements OnClickListener{
	private static String TAG = UserProfileActivity.class.getSimpleName();
	
	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;
	private ImageView headAvatar;
	private TextView tvNickName;
	private TextView tvweixin;
	private ProgressDialog dialog;


	UserProfileActivity mContext;
	User user = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(cn.ucai.superwechat.R.layout.em_activity_user_profile);
		mContext = this;
		initView();
		initListener();
		user = EaseUserUtils.getCurrentUserInfo();
	}
	
	private void initView() {
		headAvatar = (ImageView) findViewById(R.id.iv_user_profile_avatar);
		tvNickName = (TextView) findViewById(R.id.tv_user_profile_nick);
		tvweixin = (TextView) findViewById(R.id.tv_user_profile_weixin);
	}
	
	private void initListener() {
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		EaseUserUtils.setAppUserNick(username, tvNickName);
		EaseUserUtils.setAppUserAvatar(this, username, headAvatar);
		EaseUserUtils.setAppUserWeixin(tvweixin);
		findViewById(R.id.user_profile_title_back).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MFGT.finish(mContext);
			}
		});
		findViewById(R.id.user_profile_nick).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText editText = new EditText(mContext);
				editText.setText(user.getMUserNick());
				new Builder(mContext).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
						.setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								String nickString = editText.getText().toString().trim();
								if (TextUtils.isEmpty(nickString)) {
									Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
									return;
								}
								if (nickString.equals(user.getMUserNick())) {
									CommonUtils.showLongToast(getString(R.string.nick_not_chang));
									return;
								}
								updateRemoteNick(nickString);
							}
						}).setNegativeButton(R.string.dl_cancel, null).show();
			}
		});
		findViewById(R.id.user_profile_avatar).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadHeadPhoto();
			}
		});
		findViewById(R.id.user_profile_weixh).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CommonUtils.showLongToast(R.string.user_name_no_be_motify);
			}
		});
	}
	
	public void asyncFetchUserInfo(String username){
		SuperWeChatHelper.getInstance().getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EaseUser>() {
			
			@Override
			public void onSuccess(EaseUser user) {
				if (user != null) {
				    SuperWeChatHelper.getInstance().saveContact(user);
				    if(isFinishing()){
				        return;
				    }
					tvNickName.setText(user.getNick());
					if(!TextUtils.isEmpty(user.getAvatar())){
						 Glide.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(cn.ucai.superwechat.R.drawable.em_default_avatar).into(headAvatar);
					}else{
					    Glide.with(UserProfileActivity.this).load(cn.ucai.superwechat.R.drawable.em_default_avatar).into(headAvatar);
					}
				}
			}
			
			@Override
			public void onError(int error, String errorMsg) {
			}
		});
	}
	
	
	
	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(cn.ucai.superwechat.R.string.dl_title_upload_photo);
		builder.setItems(new String[] { getString(cn.ucai.superwechat.R.string.dl_msg_take_photo), getString(cn.ucai.superwechat.R.string.dl_msg_local_upload) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_no_support),
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
	
	

	private void updateRemoteNick(final String nickName) {
		dialog = ProgressDialog.show(this, getString(cn.ucai.superwechat.R.string.dl_update_nick), getString(cn.ucai.superwechat.R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean updatenick = SuperWeChatHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(nickName);
				if (UserProfileActivity.this.isFinishing()) {
					return;
				}
				if (!updatenick) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
									.show();
							dialog.dismiss();
						}
					});
				} else {
					updateAppNick(nickName);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
									.show();
							tvNickName.setText(nickName);
						}
					});
				}
			}
		}).start();
	}

	private void updateAppNick(String Nickname) {
		NetDao.updatanick(mContext, user.getMUserName(), Nickname, new OkHttpUtils.OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				if (s != null) {
					Result result = ResultUtils.getResultFromJson(s, User.class);
					if (result != null && result.isRetMsg()) {
						User u = (User) result.getRetData();
						updateLocatUser(u);
					} else {
						Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
								.show();
						dialog.dismiss();
					}
				} else {
					Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
							.show();
					dialog.dismiss();
				}
			}

			@Override
			public void onError(String error) {
				L.e(TAG,"error="+error);
				Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
						.show();
				dialog.dismiss();
			}
		});
	}

	private void updateLocatUser(User u) {
		user = u;
//		SuperWeChatHelper.getInstance().saveAppContact(u);
		EaseUserUtils.setCurrentAppUserNick(tvNickName);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_PICK:
			if (data == null || data.getData() == null) {
				return;
			}
			startPhotoZoom(data.getData());
			break;
		case REQUESTCODE_CUTTING:
			if (data != null) {
				updateAppUserAvatar(data);
				setPicToView(data);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateAppUserAvatar(final Intent picavatar) {
		dialog = ProgressDialog.show(this, getString(cn.ucai.superwechat.R.string.dl_update_photo), getString(cn.ucai.superwechat.R.string.dl_waiting));
		dialog.show();
		File file = saveBitmapFile(picavatar);
		NetDao.updateavatar(this, user.getMUserName(), file, new OkHttpUtils.OnCompleteListener<String>() {
			@Override
			public void onSuccess(String s) {
				if (s != null) {
					Result result = ResultUtils.getResultFromJson(s, User.class);
					if (result != null && result.isRetMsg()) {
						User user = (User) result.getRetData();
//						SuperWeChatHelper.getInstance().saveAppContact(user);
						setPicToView(picavatar);
					} else {
						dialog.dismiss();
						CommonUtils.showMsgShortToast(result!=null?result.getRetCode():R.string.toast_updatephoto_fail);
//						CommonUtils.showLongToast(R.string.toast_updatephoto_fail);
					}
				} else {
					dialog.dismiss();
					CommonUtils.showLongToast(R.string.toast_updatephoto_fail);
				}
			}

			@Override
			public void onError(String error) {
				L.e(TAG,"error="+error);
				dialog.dismiss();
				CommonUtils.showLongToast(R.string.toast_updatephoto_fail);
			}
		});

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
			headAvatar.setImageDrawable(drawable);

			dialog.dismiss();
			Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatephoto_success),
					Toast.LENGTH_SHORT).show();

			uploadUserAvatar(Bitmap2Bytes(photo));
		}

	}
	
	private void uploadUserAvatar(final byte[] data) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String avatarUrl = SuperWeChatHelper.getInstance().getUserProfileManager().uploadUserAvatar(data);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						if (avatarUrl != null) {
							dialog.dismiss();
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatephoto_success),
									Toast.LENGTH_SHORT).show();
						} else {
							dialog.dismiss();
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatephoto_fail),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

			}
		}).start();

	}
	
	
	public byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	@Override
	public void onClick(View v) {
		uploadHeadPhoto();
	}

	public File saveBitmapFile(Intent picavatar) {
		Bundle extras = picavatar.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			String imagepath = EaseImageUtils.getImagePath(user.getMUserName()+ I.AVATAR_SUFFIX_JPG);
			File file = new File(imagepath);
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
				bos.flush();
				bos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return file;
		}
		return null;
	}
}
