package cn.palmap.jilinscience.di.module;

import android.app.Activity;

import cn.palmap.jilinscience.base.BaseActivity;
import cn.palmap.jilinscience.di.ActivityScope;
import dagger.Module;
import dagger.Provides;

/**
 * Created by 王天明 on 2017/5/8.
 */
@ActivityScope
@Module(includes = {ToastModule.class, ProgressDialogModule.class})
public class ActivityModule {

    private BaseActivity activity;

    public ActivityModule(BaseActivity baseActivity) {
        this.activity = baseActivity;
    }

    @Provides
    Activity providesActivity() {
        return activity;
    }

}
