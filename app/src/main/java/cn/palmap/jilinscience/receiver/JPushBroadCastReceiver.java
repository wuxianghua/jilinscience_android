package cn.palmap.jilinscience.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONObject;

import java.util.logging.Logger;

import cn.jpush.android.api.JPushInterface;
import cn.palmap.jilinscience.view.MainActivity;

/**
 * Created by stone on 2017/5/17.
 */

public class JPushBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            openNotification(context,bundle);
        }
    }


    private void openNotification(Context context, Bundle bundle){
        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.putExtras(bundle);
        mIntent.setAction("open_message");
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mIntent);
    }
}
