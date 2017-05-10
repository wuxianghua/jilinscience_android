package cn.palmap.jilinscience.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by wtm on 2015/5/7.
 */
public class ActivityUtils {

    public static void noTitle(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public static void fullScreen(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static void noTitleFullScreen(Activity activity) {
        noTitle(activity);
        fullScreen(activity);
    }

    /**
     * 当activity拥有非透明背景时 应该调用该方法 为window设置空背景 清除默认背景 加强渲染效率
     *
     * @param activity
     */
    public static void noBackground(Activity activity) {
        activity.getWindow().setBackgroundDrawable(null);
    }

    public static void hideInputWindow(Activity aty) {
        try {
            InputMethodManager imm = (InputMethodManager) aty.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(aty.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 改变背景颜色
     */
    public static void alphaBackground(Activity activity, Float bgcolor){
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgcolor;
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        activity.getWindow().setAttributes(lp);
    }

}
