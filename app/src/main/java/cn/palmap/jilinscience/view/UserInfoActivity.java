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
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.api.HeadImageService;
import cn.palmap.jilinscience.api.UploadHeadService;
import cn.palmap.jilinscience.api.UserBirthdayService;
import cn.palmap.jilinscience.api.UserSexService;
import cn.palmap.jilinscience.config.ServereConfig;
import cn.palmap.jilinscience.factory.ServiceFactory;
import cn.palmap.jilinscience.model.ApiCode;
import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.utils.DialogUtils;
import cn.palmap.jilinscience.utils.FileUtils;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;
import cn.palmap.jilinscience.utils.UtilImags;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by stone on 2017/5/11.
 */

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    ZQRoundOvalImageView zqRoundOvalImageView;
    PopupWindow pop;
    LinearLayout ll_popup;
    private String apiconde;
    private String phone;
    String filename = null;
    private LinearLayout mNickName;
    private LinearLayout mUserSex;
    private LinearLayout mUserBirthday;
    private TextView mTvNickName;
    private TextView mTvUserSex;
    private User mUser;
    private String birthday;
    private SimpleDateFormat formatter;
    private ImageView mImageBack;

    private final int SEXID_MAN = 1;
    private final int SEXID_FEMAL = 2;
    private final int SEXID_SECRET = 0;
    private TextView tvTime;
    private int currentMonth;
    private int currentYear;
    private int currentDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        App.getInstance().addActivity(this);
        initView();
        initData();
    }

    private void initView() {
        mTvNickName = (TextView) findViewById(R.id.tv_nickname);
        mUserSex = (LinearLayout) findViewById(R.id.user_sex_ll);
        mTvUserSex = (TextView) findViewById(R.id.tv_user_sex);
        zqRoundOvalImageView = (ZQRoundOvalImageView) findViewById(R.id.iv_head_image);
        mNickName = (LinearLayout) findViewById(R.id.nick_name_ll);
        mUserBirthday = (LinearLayout) findViewById(R.id.ll_tv_user_birthday);
        mImageBack = (ImageView) findViewById(R.id.userinfo_imageBack);
        mUserBirthday.setOnClickListener(this);
        mNickName.setOnClickListener(this);
        mUserSex.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
        tvTime = (TextView) findViewById(R.id.tv_user_birthday);
        zqRoundOvalImageView.setOnClickListener(this);
        findViewById(R.id.userinfo_imageBack).setOnClickListener(this);
    }



    private void initData() {
        mUser = App.getInstance().getUser();
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        birthday = formatter.format(mUser.getBirthday());
        if (mUser != null) {
            getUserSex(mUser.getSex());
            mTvNickName.setText(mUser.getUserName());
            mTvUserSex.setText(getUserSex(mUser.getSex()));
            tvTime.setText(birthday);
            Glide.with(UserInfoActivity.this).load(ServereConfig.HEAD_ROOT_HOST+mUser.getHeadPath()).into(zqRoundOvalImageView);
        }
        apiconde = SharedPreferenceUtils.getValue(UserInfoActivity.this, "UserInfo", "customId", null);
        phone = SharedPreferenceUtils.getValue(UserInfoActivity.this, "UserInfo", "phone", null);
        Calendar c = Calendar.getInstance();
        currentYear = c.get(Calendar.YEAR);
        currentMonth = c.get(Calendar.MONTH)+1;
        currentDay = c.get(Calendar.DATE);
    }

    private String getUserSex(int sex) {
        switch (sex) {
            case 0:
                return "秘密";
            case 1:
                return "男";
            case 2:
                return "女";
        }
        return "秘密";
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
            case R.id.nick_name_ll:
                startActivityForResult(new Intent(UserInfoActivity.this, NicknameActivity.class), 3);
                break;
            case R.id.user_sex_ll:
                showSexPopupWindow();
                ll_popup.startAnimation(AnimationUtils.loadAnimation(
                        UserInfoActivity.this, R.anim.activity_translate_in));
                pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                break;
            case R.id.ll_tv_user_birthday:
                showTimerPicker();
                break;
            case R.id.userinfo_imageBack:
                finish();
                break;
        }
    }

    private void showTimerPicker() {
        new DoubleDatePickerDialog(UserInfoActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth) {
                currentMonth = startMonthOfYear + 1;
                currentDay = startDayOfMonth;
                currentYear = startYear;
                String textString = currentYear+"-"+currentMonth+"-"+currentDay;
                if (getTime(textString) > new Date().getTime()) {
                    DialogUtils.showTimePickerErrorDialog(UserInfoActivity.this);
                    return;
                }
                tvTime.setText(textString);
                updateUserBirthday(textString);
            }
        }, currentYear, currentMonth-1, currentDay, true).show();
    }

    private long getTime(String user_time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d;
        long l = 0;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    private void updateUserBirthday(final String birthday) {
        final UserBirthdayService userSexService = ServiceFactory.create(UserBirthdayService.class);
        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), birthday);
        retrofit2.Call<ApiCode> call = userSexService.updateBirthday(phone + ";" + apiconde, requestBody);
        call.enqueue(new Callback<ApiCode>() {
            @Override
            public void onResponse(retrofit2.Call<ApiCode> call, Response<ApiCode> response) {
                mUser.setBirthday(parseStringTime(birthday));
                FileUtils.persistUserInfo(mUser,UserInfoActivity.this);
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(retrofit2.Call<ApiCode> call, Throwable t) {
                Log.e("Upload", t.toString());
            }
        });
    }

    private long parseStringTime(String birthday) {
        long time = 0;
        try {
            Date date = formatter.parse(birthday);
            time = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            return time;
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
        TextView bt3 = (TextView) view.findViewById(R.id.item_popupwindows_cancel);
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
                mTvUserSex.setText(getString(R.string.txt_man));
                ll_popup.clearAnimation();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadUserSex(SEXID_FEMAL);
                pop.dismiss();
                mTvUserSex.setText(getString(R.string.txt_femal));
                ll_popup.clearAnimation();
            }
        });

        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                uploadUserSex(SEXID_SECRET);
                pop.dismiss();
                mTvUserSex.setText(getString(R.string.txt_secret));
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

    private void uploadUserSex(final int sexId) {
        final UserSexService userSexService = ServiceFactory.create(UserSexService.class);
        retrofit2.Call<ApiCode> call = userSexService.uploadSex(phone + ";" + apiconde, sexId);
        call.enqueue(new Callback<ApiCode>() {
            @Override
            public void onResponse(retrofit2.Call<ApiCode> call, Response<ApiCode> response) {
                mUser.setSex(sexId);
                FileUtils.persistUserInfo(mUser,UserInfoActivity.this);
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(retrofit2.Call<ApiCode> call, Throwable t) {
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
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK
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
                staffFileupload(new File(UtilImags.SHOWFILEURL(UserInfoActivity.this)+ "/stscname.jpg"));
            } catch (Exception e) {
                showToastShort("上传失败");
            }
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK
                && null != data) {
            mTvNickName.setText(data.getStringExtra("nickName"));
            mUser.setUserName(data.getStringExtra("nickName"));
            FileUtils.persistUserInfo(mUser,UserInfoActivity.this);
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
        final UploadHeadService uploadHeadService = ServiceFactory.create(UploadHeadService.class);
        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);
        retrofit2.Call<ApiCode> call = uploadHeadService.uploadImage(phone + ";" + apiconde, requestBody);
        call.enqueue(new Callback<ApiCode>() {
            @Override
            public void onResponse(retrofit2.Call<ApiCode> call, Response<ApiCode> response) {
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
                if (response.isSuccessful()) {
                    String str = response.body().getMsg();
                    staffFileupDate(str);
                    mUser.setHeadPath(str);
                    FileUtils.persistUserInfo(mUser,UserInfoActivity.this);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ApiCode> call, Throwable t) {
                Log.e("Upload", t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void staffFileupDate(String str) {
        final HeadImageService headImageService = ServiceFactory.create(HeadImageService.class);
        final RequestBody requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), str);
        retrofit2.Call<ApiCode> call = headImageService.updateImage(phone + ";" + apiconde, requestBody);
        call.enqueue(new Callback<ApiCode>() {
            @Override
            public void onResponse(retrofit2.Call<ApiCode> call, Response<ApiCode> response) {
                Log.v("Upload", response.message());
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(retrofit2.Call<ApiCode> call, Throwable t) {
                Log.e("Upload", t.toString());
            }
        });
    }

    private void showToastShort(String string) {
        Toast.makeText(UserInfoActivity.this, string, Toast.LENGTH_LONG).show();
    }
}
