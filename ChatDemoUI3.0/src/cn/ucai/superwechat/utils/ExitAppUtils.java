package cn.ucai.superwechat.utils;

import android.app.Activity;

import com.baidu.platform.comapi.map.A;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/5 0005.
 */

public class ExitAppUtils {
    List<Activity> mactivitylist = new LinkedList<>();
    private static ExitAppUtils instance = new ExitAppUtils();

    private ExitAppUtils() {

    }

    public static ExitAppUtils getInstance() {
        return instance;
    }

    public void addactivity(Activity activity) {
        mactivitylist.add(activity);
    }

    public void delactivity(Activity activity) {
        mactivitylist.remove(activity);
    }

    public void exit() {
        for (Activity activity : mactivitylist) {
            activity.finish();
        }
    }
}
