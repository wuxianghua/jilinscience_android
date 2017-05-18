package cn.palmap.jilinscience;

import android.app.Activity;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.palmap.jilinscience.base.BaseApplication;
import cn.palmap.jilinscience.model.User;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class App extends BaseApplication {

    private User user;

    private static App instance;

    private List<Activity> activities = new ArrayList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());
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
