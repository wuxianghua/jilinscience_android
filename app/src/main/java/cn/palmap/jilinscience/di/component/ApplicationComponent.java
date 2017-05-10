package cn.palmap.jilinscience.di.component;

import javax.inject.Singleton;

import cn.palmap.jilinscience.base.BaseApplication;
import cn.palmap.jilinscience.di.module.ApplicationModule;
import dagger.Component;

/**
 * Created by 王天明 on 2017/5/8.
 */
@Component (modules = {ApplicationModule.class})
@Singleton
public interface ApplicationComponent {
    void inject(BaseApplication baseApplication);
}
