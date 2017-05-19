package cn.palmap.jilinscience;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.palmap.jilinscience.base.BaseApplication;
import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.utils.SharedPreferenceUtils;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class App extends BaseApplication {

    private User user;

    private static App instance;

    private List<Activity> activities = new ArrayList<Activity>();

    private boolean mIsAcceptNotification;
    private boolean mIsSoundNotification;
    private boolean mIsShockNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initData();
        registerJPushMessage();
    }

    private void initData() {
        mIsAcceptNotification = SharedPreferenceUtils.getValue(this,"notification","mIsAcceptNotification",false);
        mIsShockNotification = SharedPreferenceUtils.getValue(this,"notification","mIsShockNotification",false);
        mIsSoundNotification = SharedPreferenceUtils.getValue(this,"notification","mIsSoundNotification",false);
    }

    private void registerJPushMessage() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
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
        }else if (mIsSoundNotification) {
            builder.notificationDefaults = Notification.DEFAULT_SOUND;
            JPushInterface.setPushNotificationBuilder(1, builder);
        } else if (mIsShockNotification) {
            builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
            JPushInterface.setPushNotificationBuilder(1, builder);
        } else {
            builder.notificationDefaults =  Notification.DEFAULT_VIBRATE&Notification.DEFAULT_SOUND;
            JPushInterface.setPushNotificationBuilder(1, builder);
        }
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();



        for (Activity activity : activities) {
            activity.finish();
        }
        System.exit(0);
    }

    public static App getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
