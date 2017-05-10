package cn.palmap.jilinscience.base;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;

/**
 * Created by 王天明 on 2016/8/18.
 */
public class FragmentTabController {

    private Activity activity;
    private int fragmentContainerId;
    private int currentTabPosition = 0;
    private ArrayList<Fragment> fragmentList;

    public interface onTabChangedListener{
        void onTabChanged(int oldPosition, int newPosition);
    }

    private onTabChangedListener onTabChangedListener = null;

    public FragmentTabController(FragmentActivity activity, int fragmentContainerId, ArrayList<Fragment> fragmentList) {
        this.activity = activity;
        this.fragmentContainerId = fragmentContainerId;
        this.fragmentList = fragmentList;
    }

    public void initTab() {
        if (currentTabPosition == 0 && fragmentList != null) {
            if (!fragmentList.get(0).isAdded()) {
                activity.getFragmentManager().beginTransaction()
                        .add(fragmentContainerId, fragmentList.get(0)).show(fragmentList.get(0)).commit();
            }
        }
    }

    public void setOnTabChangedListener(FragmentTabController.onTabChangedListener onTabChangedListener) {
        this.onTabChangedListener = onTabChangedListener;
    }

    public void changeTab(int position) {
        if (fragmentList == null || fragmentList.size() < 2) return;
        if (currentTabPosition != position) {
            FragmentTransaction trx = activity.getFragmentManager().beginTransaction();
            trx.hide(fragmentList.get(currentTabPosition));
            if (!fragmentList.get(position).isAdded()) {
                trx.add(fragmentContainerId, fragmentList.get(position));
            }
            trx.show(fragmentList.get(position)).commit();
        }
        if (onTabChangedListener != null) {
            onTabChangedListener.onTabChanged(currentTabPosition, position);
        }
        currentTabPosition = position;
    }

    public int getCurrentTabPosition() {
        return currentTabPosition;
    }

    public Fragment getCurrentFragment(){
        return fragmentList.get(currentTabPosition);
    }

}