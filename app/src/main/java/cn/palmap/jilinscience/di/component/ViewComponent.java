package cn.palmap.jilinscience.di.component;

import cn.palmap.jilinscience.di.ActivityScope;
import cn.palmap.jilinscience.di.module.ActivityModule;
import cn.palmap.jilinscience.di.module.MainModule;
import cn.palmap.jilinscience.view.LoginActivity;
import cn.palmap.jilinscience.view.MainActivity;
import cn.palmap.jilinscience.view.RegisterActivity;
import cn.palmap.jilinscience.view.ResetPswActivity;
import dagger.Component;

/**
 * Created by 王天明 on 2017/5/8.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class, MainModule.class})
public interface ViewComponent extends ActivityComponent {

    void inject(MainActivity activity);

    void inject(LoginActivity activity);

    void inject(RegisterActivity registerActivity);

    void inject(ResetPswActivity registerActivity);
}
