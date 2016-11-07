package cn.ucai.superwechat.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;

import cn.hyphenate.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.MFGT;


/**
 * Created by Administrator on 2016/11/7 0007.
 */

public class AddFriendActivity extends BaseActivity {
    private ProgressDialog progressDialog;
    ImageView mback;
    EditText mMyname;
    Button mbutton;
    String username;
    String msg = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cn.ucai.superwechat.R.layout.em_activity_add_friend);
        username = getIntent().getStringExtra(I.User.USER_NAME);
        if (username == null) {
            MFGT.finish(this);
        }
        initView();
        setListener();
    }

    private void setListener() {
        mback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMsg();
            }
        });
    }

    private void sendMsg() {
        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.addcontact_adding);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        new Thread(new Runnable() {
            public void run() {

                try {
                    //demo use a hardcode reason here, you need let user to input if you like
                    String s = getResources().getString(cn.ucai.superwechat.R.string.Add_a_friend);
                    EMClient.getInstance().contactManager().addContact(username,msg);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(cn.ucai.superwechat.R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_LONG).show();
                            MFGT.finish(AddFriendActivity.this);
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(cn.ucai.superwechat.R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                            MFGT.finish(AddFriendActivity.this);
                        }
                    });
                }
            }
        }).start();
    }

    private void initView() {
        mback = (ImageView) findViewById(R.id.AddFriend_Back);
        mMyname = (EditText) findViewById(R.id.edit_note);
        mbutton = (Button) findViewById(R.id.Send_Add_Friend_btn);
        msg = getString(R.string.addcontact_send_msg_prefix)+
                EaseUserUtils.getCurrentUserInfo().getMUserNick();
        mMyname.setText(msg);
    }
}
