package cn.palmap.jilinscience.delegate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.Toast;

import cn.palmap.jilinscience.utils.IOUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by 王天明 on 2015/12/18 0018.
 */
public class ToastDelegate {

    private Toast toast;
    @SuppressLint("ShowToast")
    public ToastDelegate(Activity context) {
        toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
    }

    public void showMsg(final String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }

    public void showMsgLong(String msg) {
        show(msg, Toast.LENGTH_LONG);
    }

    private void show(final String msg,final int time){
        if (IOUtils.checkMainThread()) {
            toast.setText(msg);
            toast.setDuration(time);
            toast.show();
        } else {
            AndroidSchedulers.mainThread().createWorker().schedule(new Runnable() {
                @Override
                public void run() {
                    toast.setText(msg);
                    toast.setDuration(time);
                    toast.show();
                }
            });
        }
    }
}