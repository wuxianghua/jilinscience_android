package cn.palmap.jilinscience.view;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;
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

import static cn.palmap.jilinscience.utils.StringUtils.checkMobile;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.imageBack) ImageView imageBack;
    @BindView(R.id.tvTitle) TextView tvTitle;
    @BindView(R.id.tvRight) TextView tvRight;
    @BindView(R.id.layoutBar) RelativeLayout layoutBar;
    @BindView(R.id.editUserName) EditText editUserName;
    @BindView(R.id.editUserPwd) EditText editUserPwd;
    @BindView(R.id.editUserCode) EditText editUserCode;
    @BindView(R.id.tvSendCode) TextView tvSendCode;
    @BindView(R.id.layoutUserCode) RelativeLayout layoutUserCode;
    @BindView(R.id.layoutEdit) LinearLayout layoutEdit;

    //是否使用密码登录
    private boolean isUsePwdLogin = true;

    private Disposable timeSubscribe;

    @Override
    protected int layoutId() {
        return R.layout.activity_login;
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
    }

    @OnClick(R.id.imageBack)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick(R.id.btnLogin)
    public void loginClick() {
        //showMsg(isUsePwdLogin ? "使用密码登录" : "使用验证码登录");
        if (TextUtils.isEmpty(editUserName.getText().toString())) {
            showMsg("请输入手机号");
            return;
        }
        if (!checkMobile(editUserName.getText().toString())) {
            showMsg("请输入正确手机号");
            return;
        }
        if (isUsePwdLogin) {
            if (TextUtils.isEmpty(editUserPwd.getText().toString())) {
                showMsg("请输入密码");
                return;
            }
        }else{
            if (TextUtils.isEmpty(editUserCode.getText().toString())) {
                showMsg("请输入验证码");
                return;
            }
        }
        final UserService userService = ServiceFactory.create(UserService.class);
        final String phone = editUserName.getText().toString();
        Observable<ApiCode> apiCodeObservable =
                isUsePwdLogin ?
                userService.loginByPassword(phone,editUserPwd.getText().toString()):
                userService.loginByAuth(phone,editUserCode.getText().toString());

        apiCodeObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<ApiCode, ObservableSource<ApiCode>>() {
                    @Override
                    public ObservableSource<ApiCode> apply(@NonNull final ApiCode apiCode) throws Exception {
                        if (apiCode.getError() == 0) {
                            SharedPreferenceUtils.putValue(LoginActivity.this,"UserInfo","customId",apiCode.getMsg());
                            SharedPreferenceUtils.putValue(LoginActivity.this,"UserInfo","phone",phone);
                            return userService.getUser(phone + ";" + apiCode.getMsg())
                                    .subscribeOn(Schedulers.computation())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .flatMap(new Function<User, ObservableSource<ApiCode>>() {
                                @Override
                                public ObservableSource<ApiCode> apply(@NonNull User user) throws Exception {
                                    App.getInstance().setUser(user);
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
                            setResult(RESULT_OK);
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

    @OnClick(R.id.tvSendCode)
    public void sendCodeClick() {
        if (tvSendCode.getTag() == null) {
            //吊起发送
            if (!checkMobile(editUserName.getText().toString())) {
                showMsg("请输入正确的手机号");
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

    private void callRequestCode(String mobile) {
        ServiceFactory.create(UserService.class)
                .requestCode(mobile)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ApiCode>() {
                    @Override
                    public void accept(@NonNull ApiCode apiCode) throws Exception {
                        if (apiCode.getError() == 0) {
                            showMsg("验证码发送成功");
                        } else {
                            showMsg("验证码发送失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        showMsg("验证码发送失败");
                    }
                });
    }

    @OnClick(R.id.tvChangeLoginType)
    public void changeLoginTypeClick(View v) {
        TextView textView = (TextView) v;
        textView.setText(isUsePwdLogin ? "使用密码登录" : "忘记密码? 使用短信验证码登录");
        layoutUserCode.setVisibility(isUsePwdLogin ? View.VISIBLE : View.GONE);
        editUserPwd.setVisibility(isUsePwdLogin ? View.GONE : View.VISIBLE);
        resetSendTextView();
        isUsePwdLogin = !isUsePwdLogin;
    }

    @OnClick(R.id.tvRight)
    public void registerClick(){
        startActivity(new Intent(this,RegisterActivity.class));
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
}
