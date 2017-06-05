package cn.palmap.jilinscience.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.base.BaseFragment;
import cn.palmap.jilinscience.config.ServereConfig;
import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.utils.NetUtils;
import cn.palmap.jilinscience.view.ContactActivity;
import cn.palmap.jilinscience.view.HelpActivity;
import cn.palmap.jilinscience.view.LoginActivity;
import cn.palmap.jilinscience.view.MainActivity;
import cn.palmap.jilinscience.view.SettingActivity;
import cn.palmap.jilinscience.view.UserInfoActivity;
import cn.palmap.jilinscience.view.ZQRoundOvalImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class MineFragment extends BaseFragment {

    @BindView(R.id.imageHead) ZQRoundOvalImageView imageHead;
    @BindView(R.id.imageSex) ImageView imageSex;
    @BindView(R.id.tvUserName) TextView tvUserName;
    @BindView(R.id.tvHelp) TextView tvHelp;
    @BindView(R.id.tvContactus) TextView tvContactus;
    @BindView(R.id.tvSetting) TextView tvSetting;

    Unbinder unbinder;

    User user;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        user = App.getInstance().getUser();
        if (user != null) {
            initUserView();
        }
    }

    @Override
    public void initView() {
        super.initView();
        unbinder = ButterKnife.bind(this, rootView);
        if (NetUtils.isNetworkAvailable(getActivity())){
            user = unPersistUserInfo();
            App.getInstance().setUser(user);
        }
        initUerInfo();
    }

    private void initUerInfo() {
        if (user == null) {
            imageSex.setVisibility(View.GONE);
            return;
        } else {
            initUserView();
        }
    }

    private User unPersistUserInfo() {
        ObjectInputStream in= null;
        User mUser = null;
        try {
            in = new ObjectInputStream(new FileInputStream(getActivity().getExternalCacheDir().getPath()+"/user.txt"));
            mUser=(User)in.readObject();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return mUser;
        }

    }

    private void initUserView() {
        Glide.with(getActivity()).load(ServereConfig.HEAD_ROOT_HOST+user.getHeadPath()).into(imageHead);
        tvUserName.setText(user.getUserName());
        imageSex.setVisibility(View.VISIBLE);
        if (user.getSex() == 1) {
            imageSex.setImageResource(R.mipmap.ic_my_boy);
        } else {
            if (user.getSex() == 2) {
                imageSex.setImageResource(R.mipmap.ic_my_girl);
            } else {
                imageSex.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.tvUserName)
    public void onUserNameClick(){
        if (user == null) {
            startActivityForResult(new Intent(getActivity(),LoginActivity.class),
                    MainActivity.CODE_LOGIN);
            return;
        }

    }

    @OnClick(R.id.imageHead)
    public void onHeadImageClick(){
        if (user != null) {
            startActivity(new Intent(getActivity(),UserInfoActivity.class));
            return;
        }
    }

    @OnClick(R.id.tvContactus)
    public void onContactItemClick(){
        startActivity(new Intent(getActivity(),ContactActivity.class));
        return;
    }

    @OnClick(R.id.tvHelp)
    public void onHelpItemClick(){
        startActivity(new Intent(getActivity(),HelpActivity.class));
        return;
    }

    @OnClick(R.id.tvSetting)
    public void onSettingItemClick(){
        startActivityForResult(new Intent(getActivity(),SettingActivity.class),
                MainActivity.CODE_SETTING);
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.CODE_LOGIN && resultCode == RESULT_OK) {
            user = App.getInstance().getUser();
            initUserView();
        }else {
            imageHead.setImageDrawable(getResources().getDrawable(R.mipmap.ic_my_head));
            tvUserName.setText(getResources().getString(R.string.txt_noLogin));
            imageSex.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
