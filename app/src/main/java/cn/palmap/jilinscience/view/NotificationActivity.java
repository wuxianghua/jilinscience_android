package cn.palmap.jilinscience.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;

/**
 * Created by stone on 2017/5/16.
 */

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView mNotificationAccept;
    private ImageView mNotificationSound;
    private ImageView mNotificationShock;
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
    }

    private void initEvent() {
        mNotificationSound.setOnClickListener(this);
        mNotificationShock.setOnClickListener(this);
        mNotificationAccept.setOnClickListener(this);
    }

    private void initView() {
        mNotificationAccept = (ImageView) findViewById(R.id.iv_notification_accept);
        mNotificationShock = (ImageView) findViewById(R.id.iv_notification_shock);
        mNotificationSound = (ImageView) findViewById(R.id.iv_notification_sound);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_notification_accept:
                mIsAcceptNotification = !mIsAcceptNotification;
                updateItemSwitch(mNotificationAccept,mIsAcceptNotification);
                break;
            case R.id.iv_notification_shock:
                mIsShockNotification = !mIsShockNotification;
                updateItemSwitch(mNotificationShock,mIsShockNotification);
                break;
            case R.id.iv_notification_sound:
                mIsSoundNotification = !mIsSoundNotification;
                updateItemSwitch(mNotificationSound,mIsSoundNotification);
                break;
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
