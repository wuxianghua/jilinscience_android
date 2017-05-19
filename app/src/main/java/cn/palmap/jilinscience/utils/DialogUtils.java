package cn.palmap.jilinscience.utils;

import android.content.Context;
import android.content.Intent;

import cn.palmap.jilinscience.view.LoginActivity;
import cn.palmap.jilinscience.view.RegisterActivity;

/**
 * Created by stone on 2017/5/18.
 */

public class DialogUtils {
    public static void showOtherErrorDialog(String title,Context context) {
        SelfDialog.Builder builder = new SelfDialog.Builder();
        final SelfDialog dialog = builder.title(title).forgetPswVisible(false).build(context);
        dialog.show();
        dialog.setInputAgainOnclickListener(new SelfDialog.OnInputAgainOnclickListener() {
            @Override
            public void onInputAgainClick() {
                dialog.dismiss();
            }
        });
    }

    public static void showPswErrorDialog(final Context context) {
        SelfDialog.Builder builder = new SelfDialog.Builder();
        final SelfDialog dialog = builder.title("密码输入错误！").forgetPswVisible(true).build(context);
        dialog.show();
        dialog.setForgetPswOnclickListener(new SelfDialog.OnForgetPswOnclickListener() {
            @Override
            public void onForgetPswClick() {
                context.startActivity(new Intent(context,RegisterActivity.class));
            }
        });
        dialog.setInputAgainOnclickListener(new SelfDialog.OnInputAgainOnclickListener() {
            @Override
            public void onInputAgainClick() {
                dialog.dismiss();
            }
        });
    }
}
