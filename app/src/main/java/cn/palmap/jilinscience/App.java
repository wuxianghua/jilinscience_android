package cn.palmap.jilinscience;

import cn.palmap.jilinscience.base.BaseApplication;
import cn.palmap.jilinscience.model.User;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class App extends BaseApplication {

    private User user;

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
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
