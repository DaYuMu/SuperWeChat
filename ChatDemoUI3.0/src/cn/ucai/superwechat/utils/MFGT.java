package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.RegisterActivity;


/**
 * 本方法为活动中的跳转
 * 从某一个活动跳转到MainActivity。
 */
public class MFGT {
    public static void finish(Activity activity){
        activity.finish();
        activity.overridePendingTransition(R.anim.push_right_in,R.anim.push_right_out);
    }
    public static void gotoMainActivity(Activity context){
        startActivity(context, MainActivity.class);
    }
    public static void startActivity(Activity context, Class<?> cls){
        Intent intent = new Intent();
        intent.setClass(context,cls);
        startActivity(context,intent);
    }
    public static void startActivity(Context context, Intent intent){
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }

    public static void startActivityForResult(Activity context, Intent intent, int requestcode){
        context.startActivityForResult(intent,requestcode);
        context.overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
    }

    // 跳转到登录页面
    public static void gotoLoginActivity(Activity context) {
        startActivity(context, LoginActivity.class);
    }

    // 跳转到注册页面
    public static void gotoRegisterActivity(Activity context) {
        startActivity(context, RegisterActivity.class);
    }
}

