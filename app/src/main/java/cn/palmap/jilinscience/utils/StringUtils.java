package cn.palmap.jilinscience.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 王天明 on 2017/5/10.
 */

public class StringUtils {

    public static boolean checkMobile(String str) {
        if (null == str || "".equals(str) || str.length() == 0) {
            return false;
        }
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
