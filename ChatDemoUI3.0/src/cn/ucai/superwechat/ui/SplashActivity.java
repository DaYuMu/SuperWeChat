package cn.ucai.superwechat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatHelper;
import cn.ucai.superwechat.utils.L;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private static final String TAG = SplashActivity.class.getSimpleName();

	private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(cn.ucai.superwechat.R.layout.em_activity_splash);

		/*RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		TextView versionText = (TextView) findViewById(R.id.tv_version);
		L.e(TAG+"OnCreate()",getVersion());
		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
		L.e(TAG,"onCreate（）方法执行完毕");*/
	}

	@Override
	protected void onStart() {
		L.e(TAG,"onStart（）方法开始");
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				L.e(TAG,"new Thread.run（）方法开始");
				//  判断是否登录，决定闪屏页面好之后进入哪个页面
				if (SuperWeChatHelper.getInstance().isLoggedIn()) {
					// auto login mode, make sure all group and conversation is loaed before enter the main screen
					long start = System.currentTimeMillis();
					EMClient.getInstance().groupManager().loadAllGroups();
					EMClient.getInstance().chatManager().loadAllConversations();
					long costTime = System.currentTimeMillis() - start;
					//wait
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//enter main screen
					//                                            从闪屏页面跳转都欢迎页面
					L.e(TAG,"开始执行从闪屏页面跳转到欢迎页面");
					startActivity(new Intent(SplashActivity.this, GuideActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					L.e(TAG,"开始执行从闪屏页面跳转到登录页面");
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}
	
	/**
	 * get sdk version
	 */
	private String getVersion() {
		L.e(TAG,EMClient.getInstance().getChatConfig().getVersion());
		return EMClient.getInstance().getChatConfig().getVersion();
	}
}
