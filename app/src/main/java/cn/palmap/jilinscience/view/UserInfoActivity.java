package cn.palmap.jilinscience.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.api.HeadImageService;
import cn.palmap.jilinscience.api.UserSexService;
import cn.palmap.jilinscience.factory.ServiceFactory;
import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;
import cn.palmap.jilinscience.utils.UtilImags;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by stone on 2017/5/11.
 */

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener{
    ZQRoundOvalImageView zqRoundOvalImageView;
    PopupWindow pop;
    LinearLayout ll_popup;
    private String apiconde;
    private String phone;
    String filename = null;
    private LinearLayout mNickName;
    private LinearLayout mUserSex;
    private TextView mTvNickName;
    private User mUser;

    private final int SEXID_MAN = 1;
    private final int SEXID_FEMAL = 2;
    private final int SEXID_SECRET = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        initView();
    }

    private void initView() {
        mTvNickName = (TextView) findViewById(R.id.tv_nickname);
        mUserSex = (LinearLayout) findViewById(R.id.user_sex_ll);
        zqRoundOvalImageView = (ZQRoundOvalImageView) findViewById(R.id.iv_head_image);
        mNickName = (LinearLayout) findViewById(R.id.nick_name_ll);
        mNickName.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
        zqRoundOvalImageView.setOnClickListener(this);
        findViewById(R.id.userinfo_imageBack).setOnClickListener(this);
        initData();
    }

    private void initData() {
        mUser = App.getInstance().getUser();
        if (mUser != null) {
            mTvNickName.setText(mUser.getUserName());
        }
        apiconde = SharedPreferenceUtils.getValue(UserInfoActivity.this,"UserInfo","customId",null);
        phone = SharedPreferenceUtils.getValue(UserInfoActivity.this,"UserInfo","phone",null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_head_image:
                showPopupWindow();
                ll_popup.startAnimation(AnimationUtils.loadAnimation(
                        UserInfoActivity.this, R.anim.activity_translate_in));
                pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.userinfo_imageBack:
                break;

            case R.id.nick_name_ll:
                startActivity(new Intent(UserInfoActivity.this,NicknameActivity.class));
                break;
            case R.id.user_sex_ll:
                showSexPopupWindow();
                ll_popup.startAnimation(AnimationUtils.loadAnimation(
                        UserInfoActivity.this, R.anim.activity_translate_in));
                pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    /****
     * 头像提示框
     */
    public void showPopupWindow() {
        pop = new PopupWindow(UserInfoActivity.this);
        View view = getLayoutInflater().inflate(R.layout.item_popupwindows,
                null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        TextView bt1 = (TextView) view.findViewById(R.id.item_popupwindows_camera);
        TextView bt2 = (TextView) view.findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera, 1);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent picture = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(picture, 2);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    /****
     * 性别提示框
     */
    public void showSexPopupWindow() {
        pop = new PopupWindow(UserInfoActivity.this);
        View view = getLayoutInflater().inflate(R.layout.item_sex_popwindows,
                null);
        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);
        pop.setBackgroundDrawable(new BitmapDrawable());
        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        TextView bt1 = (TextView) view.findViewById(R.id.item_popupwindows_man);
        TextView bt2 = (TextView) view.findViewById(R.id.item_popupwindows_femal);
        TextView bt3 = (TextView) view.findViewById(R.id.item_popupwindows_secret);
        Button bt4 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadUserSex(SEXID_MAN);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadUserSex(SEXID_FEMAL);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadUserSex(SEXID_SECRET);
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });

        bt4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pop.dismiss();
                ll_popup.clearAnimation();
            }
        });
    }

    private void uploadUserSex(int sexId) {
        final UserSexService userSexService = ServiceFactory.create(UserSexService.class);
        retrofit2.Call<String> call = userSexService.uploadSex(phone+";"+apiconde,sexId);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && null != data) {
            String sdState = Environment.getExternalStorageState();
            if (!sdState.equals(Environment.MEDIA_MOUNTED)) {
                return;
            }
            String name = DateFormat.format("yyyyMMdd_hhmmss",
                    Calendar.getInstance(Locale.CHINA)) + ".jpg";
            Bundle bundle = data.getExtras();
            // 获取相机返回的数据，并转换为图片格式
            Bitmap bmp = (Bitmap) bundle.get("data");
            FileOutputStream fout = null;
            try {
                filename = UtilImags.SHOWFILEURL(UserInfoActivity.this) + "/" + name;
            } catch (IOException e) {
            }
            try {
                fout = new FileOutputStream(filename);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fout);
            } catch (FileNotFoundException e) {
                showToastShort("上传失败");
            } finally {
                try {
                    fout.flush();
                    fout.close();
                } catch (IOException e) {
                    showToastShort("上传失败");
                }
            }
            zqRoundOvalImageView.setImageBitmap(bmp);
            staffFileupload(new File(filename));
        }
        if (requestCode == 2 && resultCode == Activity.RESULT_OK
                && null != data) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = this.getContentResolver().query(selectedImage,
                        filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String picturePath = c.getString(columnIndex);
                c.close();

                Bitmap bmp = BitmapFactory.decodeFile(picturePath);
                // 获取图片并显示
                zqRoundOvalImageView.setImageBitmap(bmp);
                saveBitmapFile(UtilImags.compressScale(bmp), UtilImags.SHOWFILEURL(UserInfoActivity.this) + "/stscname.jpg");
                staffFileupload(new File(UtilImags.SHOWFILEURL(UserInfoActivity.this) + "/stscname.jpg"));
            } catch (Exception e) {
                showToastShort("上传失败");
            }
        }
    }

    public void saveBitmapFile(Bitmap bitmap, String path) {
        File file = new File(path);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void staffFileupload(File file) {
        final HeadImageService headImageService = ServiceFactory.create(HeadImageService.class);
        String imageName = "stscname.jpg";
        RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        retrofit2.Call<Boolean> call = headImageService.uploadImage(phone+";"+apiconde, requestBody);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(retrofit2.Call<Boolean> call, Response<Boolean> response) {
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(retrofit2.Call<Boolean> call, Throwable t) {
                Log.e("Upload", t.toString());
            }
        });
    }

    private void showToastShort(String string) {
        Toast.makeText(UserInfoActivity.this, string, Toast.LENGTH_LONG).show();
    }
}
