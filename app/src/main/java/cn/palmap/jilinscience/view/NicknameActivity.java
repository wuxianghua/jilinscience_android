package cn.palmap.jilinscience.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.api.UserNameService;
import cn.palmap.jilinscience.factory.ServiceFactory;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by stone on 2017/5/12.
 */

public class NicknameActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEtNickName;
    private Button mBtNickName;
    private String mNickName;
    private String apiconde;
    private String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mBtNickName.setOnClickListener(this);
    }

    private void initView() {
        mBtNickName = (Button) findViewById(R.id.nick_name_bt);
        mEtNickName = (EditText) findViewById(R.id.nick_name_et);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nick_name_bt:
                mNickName = mEtNickName.getText().toString().trim();
                uploadNickName(mNickName);
                break;
        }
    }

    private void initData() {
        apiconde = SharedPreferenceUtils.getValue(NicknameActivity.this,"UserInfo","customId",null);
        phone = SharedPreferenceUtils.getValue(NicknameActivity.this,"UserInfo","phone",null);
    }

    private void uploadNickName(String mNickName) {
        final UserNameService userNameService = ServiceFactory.create(UserNameService.class);
        retrofit2.Call<String> call = userNameService.uploadNickName(phone+";"+apiconde,mNickName);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit2.Call<String> call, Response<String> response) {
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(retrofit2.Call<String> call, Throwable t) {
                Log.e("Upload", t.toString());
            }
        });
    }
}
