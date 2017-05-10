package cn.palmap.jilinscience.di.module;

import javax.inject.Singleton;

import cn.palmap.jilinscience.base.BaseApplication;
import dagger.Module;
import dagger.Provides;

/**
 * Created by 王天明 on 2017/5/8.
 */
@Module
public class ApplicationModule {

    private BaseApplication baseApplication;

    public ApplicationModule(BaseApplication baseApplication) {
        this.baseApplication = baseApplication;
    }

    @Singleton
    @Provides
    BaseApplication providesApplication() {
        return baseApplication;
    }

}