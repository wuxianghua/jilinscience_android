package cn.palmap.jilinscience.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.base.BaseFragment;
import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.view.LoginActivity;
import cn.palmap.jilinscience.view.MainActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class MineFragment extends BaseFragment {

    @BindView(R.id.imageHead) ImageView imageHead;
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
    public void initView() {
        super.initView();
        unbinder = ButterKnife.bind(this, rootView);
        user = App.getInstance().getUser();
        if (user == null) {
            return;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.CODE_LOGIN && resultCode == RESULT_OK) {
            //加载

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
