package cn.palmap.jilinscience.di.module;


import android.app.Activity;

import cn.palmap.jilinscience.delegate.ProgressDialogDelegate;
import cn.palmap.jilinscience.di.ActivityScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by 王天明 on 2015/12/21 0021.
 */
@Module
public class ProgressDialogModule {
    @ActivityScope
    @Provides
    ProgressDialogDelegate providesDelegate(Activity activity) {
        return new ProgressDialogDelegate(activity,"提示","加载中...");
    }
}
