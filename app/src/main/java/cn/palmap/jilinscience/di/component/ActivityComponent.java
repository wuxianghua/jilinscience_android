package cn.palmap.jilinscience.di.component;

import cn.palmap.jilinscience.di.ActivityScope;
import cn.palmap.jilinscience.di.module.ActivityModule;
import dagger.Component;

/**
 * Created by 王天明 on 2017/5/8.
 */
@ActivityScope
@Component (dependencies = {ApplicationComponent.class},modules = {ActivityModule.class})
public interface ActivityComponent {

}