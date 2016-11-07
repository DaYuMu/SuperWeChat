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
import cn.ucai.superwechat.utils.MFGT;

/**
 * Created by Administrator on 2016/11/7 0007.
 */

public class FriendProfileActivity extends BaseActivity {
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
        super.onCreate(savedInstanceState);
        setContentView(cn.ucai.superwechat.R.layout.em_activity_profile_friend);
        user = (User) getIntent().getSerializableExtra(I.User.USER_NAME);
        if (user == null) {
            MFGT.finish(this);
        }
        initView();
        setListener();
    }

    private void initView() {
        mback = (ImageView) findViewById(R.id.FriendProfile_Back);
        mavatar = (ImageView) findViewById(R.id.iv_profile_default_avatar);
        musername = (TextView) findViewById(R.id.tv_friend_profile_username);
        musernick = (TextView) findViewById(R.id.tv_friend_profile_usernick);
        mAddFriend = (Button) findViewById(R.id.btn_Add_Friend_Profile);
        mSendChat = (Button) findViewById(R.id.btn_Send_Chat);
        mSendVedio = (Button) findViewById(R.id.btn_Send_Video);
        setUserInfo();
        isFriend();
    }

    private void isFriend() {
        if (SuperWeChatHelper.getInstance().getAppContactList().containsKey(user.getMUserName())) {
            mSendVedio.setVisibility(View.VISIBLE);
            mSendChat.setVisibility(View.VISIBLE);
        } else {
            mAddFriend.setVisibility(View.VISIBLE);
        }
    }

    private void setUserInfo() {
        EaseUserUtils.setAppUserAvatar(this,user.getAvatar(),mavatar);
        EaseUserUtils.setAppUserNick(user.getMUserName(),musernick);
        EaseUserUtils.setAppUserName(user.getMUserName(),musername);
    }

    private void setListener() {
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

            }
        });
        mSendVedio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
