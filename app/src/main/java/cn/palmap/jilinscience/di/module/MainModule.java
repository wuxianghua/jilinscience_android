package cn.palmap.jilinscience.di.module;

import android.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

import javax.inject.Named;

import cn.palmap.jilinscience.base.FragmentTabController;
import cn.palmap.jilinscience.di.ActivityScope;
import cn.palmap.jilinscience.view.fragment.HomePageFragment;
import cn.palmap.jilinscience.view.fragment.MineFragment;
import dagger.Module;
import dagger.Provides;

/**
 * Created by 王天明 on 2017/5/8.
 */
@Module
@ActivityScope
public class MainModule {

    public static final String mainFragmentTabController = "mainFragmentTabController";

    private int containerId;
    private FragmentActivity fragmentActivity;

    public MainModule(FragmentActivity fragmentActivity,int fragmentContainerId) {
        this.containerId = fragmentContainerId;
        this.fragmentActivity = fragmentActivity;
    }

    @Provides
    @Named(mainFragmentTabController)
    FragmentTabController providesFragmentTabController(ArrayList<Fragment> fragments) {
        return new FragmentTabController(fragmentActivity, containerId, fragments);
    }

    @Provides
    ArrayList<Fragment> providesFragmentList() {
        ArrayList<Fragment> result = new ArrayList<>();
        result.add(new HomePageFragment());
        result.add(new MineFragment());
        return result;
    }

}
