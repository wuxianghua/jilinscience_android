package cn.palmap.jilinscience.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.api.UserNameService;
import cn.palmap.jilinscience.factory.ServiceFactory;
import cn.palmap.jilinscience.model.ApiCode;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    private Intent data;
    private ImageView mImageBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().addActivity(this);
        setContentView(R.layout.activity_nickname);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mBtNickName.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
    }

    private void initView() {
        mBtNickName = (Button) findViewById(R.id.nick_name_bt);
        mEtNickName = (EditText) findViewById(R.id.nick_name_et);
        mImageBack = (ImageView) findViewById(R.id.imageBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nick_name_bt:
                mNickName = mEtNickName.getText().toString().trim();
                uploadNickName();
                break;
            case R.id.imageBack:
                finish();
                break;
        }
    }

    private void initData() {
        apiconde = SharedPreferenceUtils.getValue(NicknameActivity.this,"UserInfo","customId",null);
        phone = SharedPreferenceUtils.getValue(NicknameActivity.this,"UserInfo","phone",null);

        data = new Intent();
    }

    private void uploadNickName() {
        final UserNameService userNameService = ServiceFactory.create(UserNameService.class);
        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"),mNickName);
        retrofit2.Call<ApiCode> call = userNameService.uploadNickName(phone+";"+apiconde,requestBody);
        call.enqueue(new Callback<ApiCode>() {
            @Override
            public void onResponse(retrofit2.Call<ApiCode> call, Response<ApiCode> response) {
                setResult(RESULT_OK,data.putExtra("nickName",mNickName));
                finish();
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(retrofit2.Call<ApiCode> call, Throwable t) {
                Log.e("Upload", t.toString());
            }
        });
    }
}
