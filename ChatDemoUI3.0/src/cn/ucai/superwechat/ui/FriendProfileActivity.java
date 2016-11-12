package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import java.io.Serializable;

import cn.hyphenate.easeui.domain.User;
import cn.hyphenate.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.data.NetDao;
import cn.ucai.superwechat.data.OkHttpUtils;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;
import cn.ucai.superwechat.utils.ResultUtils;

/**
 * Created by Administrator on 2016/11/7 0007.
 */

public class FriendProfileActivity extends BaseActivity {
    private static String TAG = FriendProfileActivity.class.getSimpleName();
    String username = null;
    User user = null;
    ImageView mavatar;
    ImageView mback;
    TextView musername;
    TextView musernick;
    Button mAddFriend;
    Button mSendChat;
    Button mSendVedio;
    boolean isFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.e(TAG,"FriendProfileActivity.onCreate1111.start");
        super.onCreate(savedInstanceState);
        setContentView(cn.ucai.superwechat.R.layout.em_activity_profile_friend);
        username = getIntent().getStringExtra(I.User.USER_NAME);
        L.e(TAG,"username="+username);
        if (username == null) {
            L.e(TAG,"不好··user==null了，自己关闭了。");
            MFGT.finish(this);
            return;
        }
        initView();
        setListener();
        user = SuperWeChatHelper.getInstance().getAppContactList().get(username);
        if (user == null) {
            isFriend=false;
        } else {
            setUserInfo();
            isFriend=true;
        }
        isFriend(isFriend);
        syncUserInfo();
        L.e(TAG,"FriendProfileActivity.onCreate.end");
    }

    private void syncFail() {
        if (!isFriend) {
            MFGT.finish(this);
            return;
        }
    }

    private void syncUserInfo() {
        NetDao.syncUserInfo(this, username, new OkHttpUtils.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String s) {
                if (s != null) {
                    Result result = ResultUtils.getResultFromJson(s, User.class);
                    if (result != null && result.isRetMsg()) {
                        User u = (User) result.getRetData();
                        if (user != null) {
                            setUserInfo();
                            if (isFriend) {
                                // 同步数据，刷新好友的数据。
                                SuperWeChatHelper.getInstance().saveAppContact(u);
                            }
                            user = u;
                            setUserInfo();
                        } else {
                            syncFail();
                        }
                    } else {
                        syncFail();
                    }
                } else {
                    syncFail();
                }
            }

            @Override
            public void onError(String error) {
                syncFail();
            }
        });
    }

    private void initView() {
        L.e(TAG,"FriendProfileActivity.initView2222.start");
        mback = (ImageView) findViewById(R.id.FriendProfile_Back);
        mavatar = (ImageView) findViewById(R.id.iv_profile_avater);
        musername = (TextView) findViewById(R.id.tv_friend_profile_username);
        musernick = (TextView) findViewById(R.id.tv_friend_profile_usernick);
        mAddFriend = (Button) findViewById(R.id.btn_Add_Friend_Profile);
        mSendChat = (Button) findViewById(R.id.btn_Send_Chat);
        mSendVedio = (Button) findViewById(R.id.btn_Send_Video);
        L.e(TAG,"FriendProfileActivity.initView.end");
        setUserInfo();
    }

    private void isFriend(boolean isFriend) {
        L.e(TAG,"FriendProfileActivity.isFriend4444.start");
        if (isFriend) {
            mSendVedio.setVisibility(View.VISIBLE);
            mSendChat.setVisibility(View.VISIBLE);
        } else {
            mAddFriend.setVisibility(View.VISIBLE);
        }
    }

    private void setUserInfo() {
        L.e(TAG,"FriendProfileActivity.setUserInfo3333.start");
        EaseUserUtils.setAppUserPathAvatar(this,user.getAvatar(),mavatar);
        L.e(TAG,"头像加载完毕");
        EaseUserUtils.setAppUserName(user.getMUserName(),musernick);
        L.e(TAG,"userNick加载完毕");
        EaseUserUtils.setAppUserNick(user.getMUserName(),musername);
        L.e(TAG,"username加载完毕");
    }

    private void setListener() {
        L.e(TAG,"FriendProfileActivity.setListener5555.start");
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.finish(FriendProfileActivity.this);
            }
        });
        mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.gotoAddFriendMsg(FriendProfileActivity.this,user.getMUserName());
            }
        });
        mSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.gotoChatActivity(FriendProfileActivity.this,user.getMUserName());
//                startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("userId", username));
            }
        });
        mSendVedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EMClient.getInstance().isConnected())
                    Toast.makeText(FriendProfileActivity.this, cn.ucai.superwechat.R.string.not_connect_to_server, Toast.LENGTH_SHORT).show();
                else {
                    startActivity(new Intent(FriendProfileActivity.this, VideoCallActivity.class).putExtra("username", user.getMUserName())
                            .putExtra("isComingCall", false));
                    // videoCallBtn.setEnabled(false);
//                    inputMenu.hideExtendMenuContainer();
                }
            }
        });
    }
}
