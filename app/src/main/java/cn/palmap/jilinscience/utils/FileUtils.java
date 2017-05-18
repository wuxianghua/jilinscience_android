package cn.palmap.jilinscience.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import cn.palmap.jilinscience.model.User;
import cn.palmap.jilinscience.view.LoginActivity;

/**
 * Created by stone on 2017/5/17.
 */

public class FileUtils {
    public static void persistUserInfo(User mUser, Context context) {
        File file = new File(context.getExternalCacheDir().getPath()+"/user.txt");
        try
        {
            file.createNewFile();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mUser);
            oos.flush();
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
