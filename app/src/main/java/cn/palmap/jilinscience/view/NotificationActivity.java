package cn.palmap.jilinscience.view;

import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;

/**
 * Created by stone on 2017/5/16.
 */

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mNotificationAccept;
    private ImageView mNotificationSound;
    private ImageView mNotificationShock;
    private ImageView mImageBack;
    private boolean mIsAcceptNotification = false;
    private boolean mIsShockNotification = false;
    private boolean mIsSoundNotification = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().addActivity(this);
        setContentView(R.layout.activity_notification);
        initView();
        initEvent();
        initData();
    }

    private void initData() {
        mIsAcceptNotification = SharedPreferenceUtils.getValue(this,"notification","mIsAcceptNotification",false);
        mIsShockNotification = SharedPreferenceUtils.getValue(this,"notification","mIsShockNotification",false);
        mIsSoundNotification = SharedPreferenceUtils.getValue(this,"notification","mIsSoundNotification",false);
        updateItemSwitch(mNotificationAccept,mIsAcceptNotification);
        updateItemSwitch(mNotificationSound,mIsSoundNotification);
        updateItemSwitch(mNotificationShock,mIsShockNotification);
    }

    private void initEvent() {
        mNotificationSound.setOnClickListener(this);
        mNotificationShock.setOnClickListener(this);
        mNotificationAccept.setOnClickListener(this);
        mImageBack.setOnClickListener(this);
    }

    private void initView() {
        mNotificationAccept = (ImageView) findViewById(R.id.iv_notification_accept);
        mNotificationShock = (ImageView) findViewById(R.id.iv_notification_shock);
        mNotificationSound = (ImageView) findViewById(R.id.iv_notification_sound);
        mImageBack = (ImageView) findViewById(R.id.imageBack);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_notification_accept:
                mIsAcceptNotification = !mIsAcceptNotification;
                updateItemSwitch(mNotificationAccept,mIsAcceptNotification);
                SharedPreferenceUtils.putValue(NotificationActivity.this,"notification","mIsAcceptNotification",mIsAcceptNotification);
                registerJPushMessage();
                break;
            case R.id.iv_notification_shock:
                mIsShockNotification = !mIsShockNotification;
                updateItemSwitch(mNotificationShock,mIsShockNotification);
                SharedPreferenceUtils.putValue(NotificationActivity.this,"notification","mIsShockNotification",mIsShockNotification);
                registerJPushMessage();
                break;
            case R.id.iv_notification_sound:
                mIsSoundNotification = !mIsSoundNotification;
                updateItemSwitch(mNotificationSound,mIsSoundNotification);
                SharedPreferenceUtils.putValue(NotificationActivity.this,"notification","mIsSoundNotification",mIsSoundNotification);
                registerJPushMessage();
                break;
            case R.id.imageBack:
                finish();
                break;
        }
    }

    private void registerJPushMessage() {
        if (mIsAcceptNotification) {
            JPushInterface.resumePush(getApplicationContext());
        } else {
            JPushInterface.stopPush(getApplicationContext());
            return;
        }
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
         if(mIsShockNotification && mIsSoundNotification) {
            builder.notificationDefaults = Notification.DEFAULT_VIBRATE|Notification.DEFAULT_SOUND;
            JPushInterface.setPushNotificationBuilder(1, builder);
        } else if(mIsSoundNotification){
            builder.notificationDefaults = Notification.DEFAULT_SOUND;
            JPushInterface.setPushNotificationBuilder(1, builder);
        } else if (mIsShockNotification) {
            builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
            JPushInterface.setPushNotificationBuilder(1, builder);
        } else {
             builder.notificationDefaults = Notification.DEFAULT_VIBRATE&Notification.DEFAULT_SOUND;
             JPushInterface.setPushNotificationBuilder(1, builder);
         }
    }

    private void updateItemSwitch(ImageView imageView,boolean isSwitchOn) {
        if (isSwitchOn) {
            imageView.setImageResource(R.mipmap.btn_slide_on);
        } else {
            imageView.setImageResource(R.mipmap.btn_slide_off);
        }
    }

}
