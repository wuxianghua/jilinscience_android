package cn.palmap.jilinscience.view;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.OnClick;
import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.base.BaseActivity;
import cn.palmap.jilinscience.base.FragmentTabController;
import cn.palmap.jilinscience.di.component.DaggerViewComponent;
import cn.palmap.jilinscience.di.module.MainModule;
import cn.palmap.jilinscience.utils.ActivityUtils;
import cn.palmap.jilinscience.view.fragment.HomePageFragment;

import static cn.palmap.jilinscience.di.module.MainModule.mainFragmentTabController;

public class MainActivity extends BaseActivity implements FragmentTabController.onTabChangedListener {
    private String mReLoginAction;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 要做的事情
            mFlash.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
            layoutTab.setVisibility(View.VISIBLE);
            super.handleMessage(msg);
        }
    };

    @Inject @Named(mainFragmentTabController) FragmentTabController fragmentTabController;

    @BindView(R.id.layoutTabHome) ViewGroup layoutTabHome;
    @BindView(R.id.layoutTabMine) ViewGroup layoutTabMine;
    @BindView(R.id.iv_tab_home) ImageView ivTabHome;
    @BindView(R.id.iv_tab_mine) ImageView ivTabMine;
    @BindView(R.id.tv_tab_home) TextView tvTabHome;
    @BindView(R.id.tv_tab_mine) TextView tvTabMine;
    @BindView(R.id.layoutTab) ViewGroup layoutTab;
    @BindView(R.id.fragmentContainer) ViewGroup fragmentContainer;
    @BindView(R.id.flash) ImageView mFlash;

    public static final int CODE_LOGIN = 1000;
    public static final int CODE_SETTING = 1001;

    @OnClick({
            R.id.layoutTabHome,
            R.id.layoutTabMine
    })
    public void onTabClick(View v){
        switch (v.getId()) {
            case R.id.layoutTabHome:
                fragmentTabController.changeTab(0);
                ivTabMine.setSelected(false);
                tvTabMine.setSelected(false);
                ivTabHome.setSelected(true);
                tvTabHome.setSelected(true);
                break;
            case R.id.layoutTabMine:
                fragmentTabController.changeTab(1);
                ivTabMine.setSelected(true);
                tvTabMine.setSelected(true);
                ivTabHome.setSelected(false);
                tvTabHome.setSelected(false);
                break;
        }
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void afterOnCreate() {
        ActivityUtils.noTitle(this);
        ActivityUtils.noBackground(this);
    }

    @Override
    protected void inject() {
        DaggerViewComponent.builder()
                .applicationComponent(getApplicationComponent())
                .activityModule(getActivityModule())
                .mainModule(new MainModule(this, R.id.fragmentContainer))
                .build().inject(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ((HomePageFragment) fragmentTabController.getCurrentFragment()).onNewIntent();
    }

    @Override
    protected void onInjected() {
        reLoginApp();
        if (App.getInstance().isFirstOpenApp) {
            mFlash.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
            layoutTab.setVisibility(View.GONE);
            Message message = new Message();
            message.what = 1;
            handler.sendMessageDelayed(message,3000);// 发送消息
            App.getInstance().isFirstOpenApp = false;
        }
        if ("exit_app".equals(mReLoginAction)) {
            fragmentTabController.changeTab(1);
            ivTabMine.setSelected(true);
            tvTabMine.setSelected(true);
            ivTabHome.setSelected(false);
            tvTabHome.setSelected(false);
        } else {
            fragmentTabController.initTab();
            layoutTabHome.setSelected(true);
        }
        fragmentTabController.setOnTabChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        App.getInstance().onTerminate();
    }

    private void reLoginApp() {
        Intent mReLoginIntent = getIntent();
        mReLoginAction = mReLoginIntent.getAction();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)
                && 0 == fragmentTabController.getCurrentTabPosition()
                && ((HomePageFragment) fragmentTabController.getCurrentFragment()).canGoBack()){
            ((HomePageFragment) fragmentTabController.getCurrentFragment()).goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onTabChanged(int oldPosition, int newPosition) {
        layoutTabHome.setSelected(newPosition == 0);
        layoutTabMine.setSelected(newPosition == 1);
    }

    public void hideTab(){
        layoutTab.setVisibility(View.GONE);
    }

    public void showTab(){
        layoutTab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_LOGIN) {
            fragmentTabController.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        }else {
            fragmentTabController.getCurrentFragment().onActivityResult(requestCode, resultCode, data);
        }
    }
}