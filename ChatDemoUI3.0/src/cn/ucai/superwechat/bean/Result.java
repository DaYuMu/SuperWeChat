package cn.ucai.superwechat.bean;

/**
 * Created by Administrator on 2016/10/13 0013.
 */
public class Result {


    /**
     * retCode : 0
     * retMsg : true
     * retData : {"muserName":"a952702","muserNick":"彭鹏","mavatarId":74,"mavatarPath":"user_avatar","mavatarSuffix":".jpg","mavatarType":0,"mavatarLastUpdateTime":"1476284146171"}
     */

    private int retCode;
    private boolean retMsg;
//    private User retData;

    public Result() {
    }

    public Result(int retCode, boolean retMsg/*, User retData*/) {
        this.retCode = retCode;
        this.retMsg = retMsg;
//        this.retData = retData;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public boolean isRetMsg() {
        return retMsg;
    }

    public void setRetMsg(boolean retMsg) {
        this.retMsg = retMsg;
    }
/*
    public User getRetData() {
        return retData;
    }

    public void setRetData(User retData) {
        this.retData = retData;
    }*/

    @Override
    public String toString() {
        return "Result{" +
                "retCode=" + retCode +
                ", retMsg=" + retMsg +
//                ", retData=" + retData +
                '}';
    }
}
