package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import cn.hyphenate.easeui.domain.User;
import cn.hyphenate.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
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
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setAppUserAvatar(this,user.getAvatar(),mavatar);
        EaseUserUtils.setAppUserNick(user.getMUserName(),musernick);
        EaseUserUtils.setAppUserName(user.getMUserName(),musername);
    }

    private void setListener() {

    }
}
