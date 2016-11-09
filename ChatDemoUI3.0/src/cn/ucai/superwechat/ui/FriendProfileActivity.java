package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import cn.hyphenate.easeui.domain.User;
import cn.hyphenate.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.L;
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2016/11/7 0007.
 */

public class FriendProfileActivity extends BaseActivity {
    private static String TAG = FriendProfileActivity.class.getSimpleName();
    User user = null;
    ImageView mavatar;
    ImageView mback;
    TextView musername;
    TextView musernick;
    Button mAddFriend;
    Button mSendChat;
    Button mSendVedio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        L.e(TAG,"FriendProfileActivity.onCreate1111.start");
        super.onCreate(savedInstanceState);
        setContentView(cn.ucai.superwechat.R.layout.em_activity_profile_friend);
        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        L.e(TAG,"username="+user);
        if (user == null) {
            L.e(TAG,"不好··user==null了，自己关闭了。");
            MFGT.finish(this);
            return;
        }
        initView();
        setListener();
        L.e(TAG,"FriendProfileActivity.onCreate.end");
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
        isFriend();
    }

    private void isFriend() {
        L.e(TAG,"FriendProfileActivity.isFriend4444.start");
        if (SuperWeChatHelper.getInstance().getAppContactList().containsKey(user.getMUserName())) {
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
        EaseUserUtils.setAppUserNick(user.getMUserName(),musernick);
        L.e(TAG,"userNick加载完毕");
        EaseUserUtils.setAppUserName(user.getMUserName(),musername);
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

            }
        });
    }
}
