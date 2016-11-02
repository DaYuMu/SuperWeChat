package cn.ucai.superwechat.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.utils.MFGT;


public class GuideActivity extends BaseActivity {

    Button login;
    Button register;
    GuideActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cn.ucai.superwechat.R.layout.activity_guide);
        mContext = this;
        initView();
        setListener();
    }

    private void setListener() {
        findViewById(R.id.ButtonLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.gotoLoginActivity(mContext);
            }
        });
        findViewById(R.id.ButtonRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MFGT.gotoRegisterActivity(mContext);
            }
        });
    }

    private void initView() {
        login = (Button) findViewById(R.id.ButtonLogin);
        register = (Button) findViewById(R.id.ButtonRegister);
    }
}
