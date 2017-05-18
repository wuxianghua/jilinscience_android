package cn.palmap.jilinscience.view;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.api.ResetPswService;
import cn.palmap.jilinscience.api.UserNameService;
import cn.palmap.jilinscience.api.UserService;
import cn.palmap.jilinscience.delegate.ProgressDialogDelegate;
import cn.palmap.jilinscience.delegate.ToastDelegate;
import cn.palmap.jilinscience.factory.ServiceFactory;
import cn.palmap.jilinscience.model.ApiCode;
import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;
import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.R.attr.data;
import static cn.palmap.jilinscience.utils.StringUtils.checkMobile;

/**
 * Created by stone on 2017/5/16.
 */

public class ResetPswActivity extends AppCompatActivity implements View.OnClickListener{
    private User mUser;
    private String mPhoneNumber;
    private Disposable timeSubscribe;
    private TextView tvGetVerifyCode;
    private TextView tvSendVerifyCode;
    private TextView tvUserTelephone;
    private EditText etVerifyCode;
    private EditText etNewPassword;
    private TextView tvConfirm;
    private String apicode;
    private String phone;
    private String mVerifyCode;
    private String mNewPassword;
    private StringBuffer secretePhone;
    private Intent mExitIntent;
    @Inject
    protected Lazy<ToastDelegate> toastDelegateLazy;
    @Inject protected Lazy<ProgressDialogDelegate> progressDialogDelegateLazy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpsw);
        App.getInstance().addActivity(this);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        tvGetVerifyCode.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    private void initView() {
        tvGetVerifyCode = (TextView) findViewById(R.id.tvGetVerifyCode);
        tvSendVerifyCode = (TextView) findViewById(R.id.verifysend_hint);
        tvUserTelephone = (TextView) findViewById(R.id.user_telephone);
        etNewPassword = (EditText) findViewById(R.id.editUserPwd);
        etVerifyCode = (EditText) findViewById(R.id.editVerifyCode);
        tvConfirm = (TextView) findViewById(R.id.btn_confirm);
        tvGetVerifyCode.setSelected(true);
    }

    protected void initData() {
        apicode = SharedPreferenceUtils.getValue(ResetPswActivity.this,"UserInfo","customId",null);
        phone = SharedPreferenceUtils.getValue(ResetPswActivity.this,"UserInfo","phone",null);
        mUser = App.getInstance().getUser();
        if (mUser != null) {
            mPhoneNumber = mUser.getLoginName();
        }
        secretePhone = new StringBuffer("+86 ").append(mPhoneNumber).replace(8,21,"****");
    }

    public void sendCodeClick() {
        if (tvGetVerifyCode.getTag() == null) {
            //吊起发送
            if (!checkMobile(mPhoneNumber)){
                return;
            }
            tvSendVerifyCode.setVisibility(View.VISIBLE);
            tvUserTelephone.setText(secretePhone);
            tvUserTelephone.setVisibility(View.VISIBLE);
            callRequestCode(mPhoneNumber);
            tvGetVerifyCode.setTag(60);
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
                            int time = (int) tvGetVerifyCode.getTag();
                            tvGetVerifyCode.setSelected(false);
                            tvGetVerifyCode.setText((time - 1) + "S后重新发送");
                            tvGetVerifyCode.setTag(time - 1);
                            if (time - 1 <= 0 && timeSubscribe != null && !timeSubscribe.isDisposed()) {
                                timeSubscribe.dispose();
                                tvGetVerifyCode.setTag(null);
                                resetSendTextView();
                            }
                        }
                    });
        }
    }

    private void resetSendTextView() {
        if (tvGetVerifyCode.getTag() == null) {
            tvGetVerifyCode.setSelected(true);
            tvGetVerifyCode.setText("点击获取");
        }
    }

    public void showMsg(String msg) {
        if (checkObj(progressDialogDelegateLazy)) {
            toastDelegateLazy.get().showMsg(msg);
        }
    }

    private boolean checkObj(Lazy lazy) {
        return !(lazy == null || lazy.get() == null);
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
                            //showMsg("验证码发送成功");
                        } else {
                            //showMsg("验证码发送失败");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        //showMsg("验证码发送失败");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvGetVerifyCode:
                sendCodeClick();
                break;
            case R.id.btn_confirm:
                updatePassword();
                break;
        }
    }

    private void updatePassword() {
        mNewPassword = etNewPassword.getText().toString().trim();
        if ("".equals(mNewPassword)) {
            showMsg("请输入新的密码");
            return;
        }
        mVerifyCode = etVerifyCode.getText().toString().trim();
        if("".equals(mVerifyCode)) {
            showMsg("请输入验证码");
            return;
        }
        final ResetPswService resetPswService = ServiceFactory.create(ResetPswService.class);
        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"),mNewPassword);
        final RequestBody requestBody1 =
                RequestBody.create(MediaType.parse("multipart/form-data"),mVerifyCode);
        Call<ApiCode> call = resetPswService.updatePassword(phone+";"+apicode,requestBody,requestBody1);
        call.enqueue(new Callback<ApiCode>() {
            @Override
            public void onResponse(retrofit2.Call<ApiCode> call, Response<ApiCode> response) {
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
                comeToHomePage();
            }

            @Override
            public void onFailure(retrofit2.Call<ApiCode> call, Throwable t) {
                Log.e("Upload", t.toString());
            }
        });
    }

    private void comeToHomePage() {
        mExitIntent = new Intent(ResetPswActivity.this,MainActivity.class);
        mExitIntent.setAction("exit_app");
        startActivity(mExitIntent);
        finish();
        deletePersistFile();
        App.getInstance().setUser(null);
    }

    private void deletePersistFile() {
        File file = new File(ResetPswActivity.this.getExternalCacheDir().getPath()+"/user.txt");
        if (file != null) {
            file.delete();
        }
    }
}
