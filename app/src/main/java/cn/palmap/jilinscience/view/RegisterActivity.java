package cn.palmap.jilinscience.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.api.UserService;
import cn.palmap.jilinscience.base.BaseActivity;
import cn.palmap.jilinscience.di.component.DaggerViewComponent;
import cn.palmap.jilinscience.di.module.MainModule;
import cn.palmap.jilinscience.factory.ServiceFactory;
import cn.palmap.jilinscience.model.ApiCode;
import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.utils.DialogUtils;
import cn.palmap.jilinscience.utils.FileUtils;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;
import cn.palmap.jilinscience.utils.StringUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import qiu.niorgai.StatusBarCompat;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.imageBack) ImageView imageBack;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvRight) TextView tvRight;
    @BindView(R.id.layoutBar) RelativeLayout layoutBar;
    @BindView(R.id.editUserName) EditText editUserName;
    @BindView(R.id.editUserCode) EditText editUserCode;
    @BindView(R.id.tvSendCode) TextView tvSendCode;
    @BindView(R.id.layoutUserCode) RelativeLayout layoutUserCode;
    @BindView(R.id.editUserPwd) EditText editUserPwd;
    @BindView(R.id.btnRegister) TextView btnRegister;

    private Disposable timeSubscribe;
    private Intent data;
    private Bundle mBundle;
    private String mPhone;
    private String mPassword;
    private String mAuth;

    @Override
    protected int layoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void inject() {
        DaggerViewComponent.builder().activityModule(getActivityModule())
                .applicationComponent(getApplicationComponent())
                .mainModule(new MainModule(this, 0))
                .build().inject(this);
    }

    @Override
    protected void onInjected() {
        StatusBarCompat.setStatusBarColor(this, Color.WHITE);
        ButterKnife.bind(this);
        tvSendCode.setSelected(true);
        data = new Intent();
        mBundle = new Bundle();
    }

    @OnClick(R.id.tvSendCode)
    public void sendCodeClick() {
        if (tvSendCode.getTag() == null) {
            //吊起发送
            if (!checkMobile(editUserName.getText().toString())) {
                DialogUtils.showOtherErrorDialog("请输入正确的手机号",RegisterActivity.this);
                return;
            }
            callRequestCode(editUserName.getText().toString());
            tvSendCode.setTag(60);
            timeSubscribe = Observable.timer(1000L, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .repeatWhen(new Function<Observable<Object>, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(@NonNull Observable<Object> objectObservable) throws Exception {
                            return objectObservable;
                        }
                    })
                    .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(@NonNull Observable<Throwable> throwableObservable) throws Exception {
                            return throwableObservable;
                        }
                    })
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            int time = (int) tvSendCode.getTag();
                            tvSendCode.setSelected(false);
                            tvSendCode.setText((time - 1) + "S后重新发送");
                            tvSendCode.setTag(time - 1);
                            if (time - 1 <= 0 && timeSubscribe != null && !timeSubscribe.isDisposed()) {
                                timeSubscribe.dispose();
                                tvSendCode.setTag(null);
                                resetSendTextView();
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.btnRegister)
    public void registerClick() {
        mPhone = editUserName.getText().toString();
        mPassword = editUserPwd.getText().toString();
        mAuth =  editUserCode.getText().toString();
        if (TextUtils.isEmpty(editUserName.getText().toString())) {
            DialogUtils.showOtherErrorDialog("请输入正确的手机号",RegisterActivity.this);
            return;
        }
        if (!checkMobile(editUserName.getText().toString())) {
            DialogUtils.showOtherErrorDialog("请输入正确的手机号",RegisterActivity.this);
            return;
        }
        if (TextUtils.isEmpty(editUserCode.getText().toString())) {
            DialogUtils.showOtherErrorDialog("验证码为空",RegisterActivity.this);
            return;
        }
        if (editUserPwd.getText().toString().length() < 6) {
            DialogUtils.showOtherErrorDialog("密码长度不小于六位！",RegisterActivity.this);
            return;
        }
        ServiceFactory.create(UserService.class)
                .registerUser(mPhone,
                        mPassword,
                        mAuth
                ).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiCode>() {
                    @Override
                    public void accept(@NonNull ApiCode apiCode) throws Exception {
                        if (apiCode.getError() == 0) {
                            showMsg("注册成功");
                            loginClick(mPhone,mPassword);
                        } else {
                            showMsg("注册失败 :" + apiCode.getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        showMsg("注册失败 :" + throwable.getMessage());
                    }
                });
    }

    public void loginClick(final String phone, String password) {
        final UserService userService = ServiceFactory.create(UserService.class);
        Observable<ApiCode> apiCodeObservable = userService.loginByPassword(phone,password);
        apiCodeObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ApiCode, ObservableSource<ApiCode>>() {
                    @Override
                    public ObservableSource<ApiCode> apply(@NonNull final ApiCode apiCode) throws Exception {
                        if (apiCode.getError() == 0) {
                            SharedPreferenceUtils.putValue(RegisterActivity.this,"UserInfo","customId",apiCode.getMsg());
                            SharedPreferenceUtils.putValue(RegisterActivity.this,"UserInfo","phone",phone);
                            return userService.getUser(phone + ";" + apiCode.getMsg())
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .flatMap(new Function<User, ObservableSource<ApiCode>>() {
                                        @Override
                                        public ObservableSource<ApiCode> apply(@NonNull User user) throws Exception {
                                            App.getInstance().setUser(user);
                                            FileUtils.persistUserInfo(user,RegisterActivity.this);
                                            final ApiCode apiCode1 = new ApiCode();
                                            apiCode1.setError(0);
                                            return Observable.create(new ObservableOnSubscribe<ApiCode>() {
                                                @Override
                                                public void subscribe(@NonNull ObservableEmitter<ApiCode> e) throws Exception {
                                                    e.onNext(apiCode1);
                                                    e.onComplete();
                                                }
                                            });
                                        }
                                    });
                        } else {
                            showMsg("登录失败 :" + apiCode.getMsg());
                            return Observable.create(new ObservableOnSubscribe<ApiCode>() {
                                @Override
                                public void subscribe(@NonNull ObservableEmitter<ApiCode> e) throws Exception {
                                    e.onNext(apiCode);
                                    e.onComplete();
                                }
                            });
                        }
                    }
                })
                .subscribe(new Consumer<ApiCode>() {
                    @Override
                    public void accept(@NonNull ApiCode apiCode) throws Exception {
                        if (apiCode.getError() == 0) {
                            showMsg("登录成功");
                            Intent mExitIntent = new Intent(RegisterActivity.this,MainActivity.class);
                            mExitIntent.setAction("exit_app");
                            startActivity(mExitIntent);
                            finish();
                        } else {
                            showMsg("登录失败 :" + apiCode.getMsg());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        showMsg("登录失败 :" + throwable.getMessage());
                    }
                });
    }

    private boolean checkMobile(String s) {
        return StringUtils.checkMobile(s);
    }

    private void callRequestCode(String mobile) {
        ServiceFactory.create(UserService.class)
                .requestCodeRegister(mobile,1)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiCode>() {
                    @Override
                    public void accept(@NonNull ApiCode apiCode) throws Exception {
                        if (apiCode.getError() == 0) {
                            showMsg("验证码发送成功");
                        } else {
                            DialogUtils.showVerifyErrorDialog(RegisterActivity.this);
                            timeSubscribe.dispose();
                            tvSendCode.setTag(null);
                            resetSendTextView();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        DialogUtils.showVerifyErrorDialog(RegisterActivity.this);
                        timeSubscribe.dispose();
                        tvSendCode.setTag(null);
                        resetSendTextView();
                    }
                });
    }

    private void resetSendTextView() {
        if (tvSendCode.getTag() == null) {
            tvSendCode.setSelected(true);
            tvSendCode.setText("点击获取");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeSubscribe != null && !timeSubscribe.isDisposed()) {
            timeSubscribe.dispose();
        }
    }

    @OnClick({R.id.imageBack,R.id.tvRight})
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
