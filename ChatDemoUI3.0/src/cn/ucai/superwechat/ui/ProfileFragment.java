package cn.ucai.superwechat.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.OnClick;
import cn.easemob.redpacketui.utils.RedPacketUtil;
import cn.hyphenate.easeui.utils.EaseUserUtils;
import cn.ucai.superwechat.Constant;
import cn.ucai.superwechat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    ImageView UserAvatar;
    TextView  UserNick;
    TextView UserName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(layout);
        setListener(layout);
        return layout;
    }

    private void setListener(View layout) {
        layout.findViewById(R.id.tv_profile_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layout.findViewById(R.id.tv_profile_collect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layout.findViewById(R.id.tv_profile_money).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //red packet code : 进入零钱页面
//                case cn.ucai.superwechat.R.id.ll_change:
                RedPacketUtil.startChangeActivity(getActivity());
//                break;
            }
        });
        layout.findViewById(R.id.tv_profile_smail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        layout.findViewById(R.id.tv_profile_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        setUserInfo();
    }

    private void setUserInfo() {
        EaseUserUtils.setCurrentAppUserAvatar(getActivity(),UserAvatar);
        EaseUserUtils.setCurrentAppUserNick(UserNick);
        EaseUserUtils.setCurrentAppUserName(UserName);
    }

    private void initView( View layout) {
        UserAvatar = (ImageView) layout.findViewById(R.id.iv_profile_default_avatar);
        UserName = (TextView) layout.findViewById(R.id.tv_profile_username);
        UserNick = (TextView) layout.findViewById(R.id.tv_profile_usernick);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(((MainActivity)getActivity()).isConflict){
            outState.putBoolean("isConflict", true);
        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
        }
    }

}
