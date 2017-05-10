package cn.palmap.jilinscience.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import cn.palmap.jilinscience.delegate.ProgressDialogDelegate;
import cn.palmap.jilinscience.delegate.ToastDelegate;
import cn.palmap.jilinscience.di.component.ApplicationComponent;
import cn.palmap.jilinscience.di.module.ActivityModule;
import dagger.Lazy;

/**
 * Created by 王天明 on 2017/5/8.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Inject protected Lazy<ToastDelegate> toastDelegateLazy;
    @Inject protected Lazy<ProgressDialogDelegate> progressDialogDelegateLazy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        afterOnCreate();
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        ButterKnife.bind(this);
        inject();
        onInjected();
    }

    protected void afterOnCreate(){

    };


    protected abstract int layoutId();

    protected abstract void inject();

    protected abstract void onInjected();

    protected ApplicationComponent getApplicationComponent() {
        return ((BaseApplication) getApplication()).getApplicationComponent();
    }

    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }

    public void showMsg(String msg) {
        if (checkObj(progressDialogDelegateLazy)) {
            toastDelegateLazy.get().showMsg(msg);
        }
    }

    public void showMsgLong(String msg) {
        if (checkObj(progressDialogDelegateLazy)) {
            toastDelegateLazy.get().showMsgLong(msg);
        }
    }

    public void showLoading() {
        if (checkObj(progressDialogDelegateLazy)) {
            progressDialogDelegateLazy.get().show();
        }
    }

    public void showLoading(String title, String msg) {
        if (checkObj(progressDialogDelegateLazy)) {
            progressDialogDelegateLazy.get().show(title,msg);
        }
    }

    public void showLoading(String title, String msg, boolean cancelable) {
        if (checkObj(progressDialogDelegateLazy)) {
            progressDialogDelegateLazy.get().show(title, msg, cancelable);
        }
    }

    public void hideLoading() {
        if (checkObj(progressDialogDelegateLazy)) {
            progressDialogDelegateLazy.get().hide();
        }
    }

    private boolean checkObj(Lazy lazy) {
        return !(lazy == null || lazy.get() == null);
    }

}
