package cn.ucai.superwechat.data;

import android.app.Activity;
import android.content.Context;

import java.io.File;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.utils.MD5;

/**
 * Created by Administrator on 2016/10/17 0017.
 * 商品新品的网络请求
 */
public class NetDao {

    /**
     * 下载注册需要的数据。
     * @param context
     * @param username
     * @param usernick
     * @param userpassword
     * @param listener
     */
    public static void register(Activity context, String username, String usernick, String userpassword, OkHttpUtils.OnCompleteListener<Result> listener) {
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,username)
                .addParam(I.User.NICK,usernick)
                .addParam(I.User.PASSWORD, MD5.getMessageDigest(userpassword))
                .post()
                .targetClass(Result.class)
                .execute(listener);
    }

    /**
     * 取消注册
     * @param context
     * @param username
     * @param listener
     */
    public static void unregister(Activity context, String username, OkHttpUtils.OnCompleteListener<Result> listener) {
        OkHttpUtils<Result> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_REGISTER)
                .addParam(I.User.USER_NAME,username)
                .targetClass(Result.class)
                .execute(listener);
    }

    public static void login(Context context, String name, String password,
                             OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_LOGIN)
                .addParam(I.User.USER_NAME,name)
                .addParam(I.User.PASSWORD,MD5.getMessageDigest(password))
                .targetClass(String.class)
                .execute(listener);
    }

    /**
     * i下载更新昵称后的数据
     * @param context
     * @param name
     * @param nick
     * @param listener
     */
    public static void updatanick(Context context, String name, String nick,
                             OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_USER_NICK)
                .addParam(I.User.USER_NAME,name)
                .addParam(I.User.NICK,nick)
                .targetClass(String.class)
                .execute(listener);
    }
    /**
     * i下载更新头像后的数据
     * @param context
     * @param name
     * @param file
     * @param listener
     */
    public static void updateavatar(Context context, String name, File file,
                                  OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_UPDATE_AVATAR)
                .addParam(I.NAME_OR_HXID,name)
                .addParam(I.AVATAR_TYPE,I.AVATAR_TYPE_USER_PATH)
                .addFile2(file)
                .targetClass(String.class)
                .post()
                .execute(listener);
    }

    /**
     * 下载收藏商品的数量
     * @param context
     * @param userName
     * @param listener
     */
    public static void syncUserInfo(Context context, String userName,
                               OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(listener);
    }


    /**
     * 查找好友
     * @param context
     * @param name
     * @param listener
     */
    public static void searchFriend(Context context, String name,
                             OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_FIND_USER)
                .addParam(I.User.USER_NAME,name)
                .targetClass(String.class)
                .execute(listener);
    }

    public static void addContact(Context context, String name, String cususername,
                             OkHttpUtils.OnCompleteListener<String> listener) {
        OkHttpUtils<String> utils = new OkHttpUtils<>(context);
        utils.setRequestUrl(I.REQUEST_ADD_CONTACT)
                .addParam(I.Contact.USER_NAME,name)
                .addParam(I.Contact.CU_NAME,cususername)
                .targetClass(String.class)
                .execute(listener);
    }
}
