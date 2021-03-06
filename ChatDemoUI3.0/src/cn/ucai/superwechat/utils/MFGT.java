package cn.ucai.superwechat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import cn.hyphenate.easeui.domain.User;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.ui.AddContactActivity;
import cn.ucai.superwechat.ui.AddFriendActivity;
import cn.ucai.superwechat.ui.ChatActivity;
import cn.ucai.superwechat.ui.FriendProfileActivity;
import cn.ucai.superwechat.ui.GroupsActivity;
import cn.ucai.superwechat.ui.LoginActivity;
import cn.ucai.superwechat.ui.MainActivity;
import cn.ucai.superwechat.ui.NewFriendsMsgActivity;
import cn.ucai.superwechat.ui.NewGroupActivity;
import cn.ucai.superwechat.ui.PublicGroupsActivity;
import cn.ucai.superwechat.ui.RegisterActivity;
import cn.ucai.superwechat.ui.SettingsActivity;


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

    public static void gotoSettingsActivity(Activity context) {
        startActivity(context, SettingsActivity.class);
    }

    public static void gotoAddFriend(Activity context) {
        startActivity(context, AddContactActivity.class);
    }
    public static void gotoFriendProfile(Activity context,String username) {
        Intent intent = new Intent();
        intent.setClass(context, FriendProfileActivity.class);
        intent.putExtra(I.User.USER_NAME,username);
        startActivity(context,intent);
    }
    public static void gotoAddFriendMsg(Activity context,String username) {
        Intent intent = new Intent();
        intent.setClass(context, AddFriendActivity.class);
        intent.putExtra(I.User.USER_NAME,username);
        startActivity(context,intent);
    }
    public static void gotoNewFriend(Activity context) {
        startActivity(context, NewFriendsMsgActivity.class);
    }

    public static void gotoChatActivity(Activity context,String username) {
        Intent intent = new Intent();
        intent.setClass(context, ChatActivity.class);
        intent.putExtra("userId",username);
        startActivity(context,intent);
    }

    public static void gotoGroupChat(Activity context) {
        startActivity(context, GroupsActivity.class);
    }

    public static void gotoNewGroupChat(Activity context) {
        startActivity(context, NewGroupActivity.class);
    }

    public static void gotoPubicGroupChat(Activity context) {
        startActivity(context, PublicGroupsActivity.class);
    }
}

