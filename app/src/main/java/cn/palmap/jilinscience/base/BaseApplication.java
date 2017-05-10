package cn.palmap.jilinscience.base;

import android.app.Application;

import cn.palmap.jilinscience.di.component.ApplicationComponent;
import cn.palmap.jilinscience.di.component.DaggerApplicationComponent;
import cn.palmap.jilinscience.di.module.ApplicationModule;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class BaseApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        inject();
    }

    private void inject() {
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        applicationComponent.inject(this);
    }
    public ApplicationComponent getApplicationComponent(){
        return applicationComponent;
    }
}
