package cn.palmap.jilinscience.utils;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * Created by 王天明 on 2016/5/6
 */
public class DeviceUtils {

    /*
    * dip转化为px
    * */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /*
    * px转化为dip
    * */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取宽度
     *
     * @param context
     * @return
     */
    public static int getWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取高度
     *
     * @param context
     * @return
     */
    public static int getHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取MAC地址
     */
    public static String getMac(Context context) {
        String macSerial = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
            InputStreamReader isr = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(isr);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(macSerial)) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            macSerial = wifiManager.getConnectionInfo().getMacAddress();
        }

        return macSerial;
    }

}
