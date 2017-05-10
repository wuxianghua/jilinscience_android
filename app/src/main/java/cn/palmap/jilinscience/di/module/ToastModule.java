package cn.palmap.jilinscience.di.module;


import android.app.Activity;

import cn.palmap.jilinscience.delegate.ToastDelegate;
import cn.palmap.jilinscience.di.ActivityScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by 王天明 on 2015/12/18 0018.
 */
@Module
public class ToastModule {
    @ActivityScope
    @Provides
    ToastDelegate providesDelegate(Activity activity) {
        return new ToastDelegate(activity);
    }
}