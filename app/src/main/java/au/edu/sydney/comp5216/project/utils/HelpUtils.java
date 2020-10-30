package au.edu.sydney.comp5216.project.utils;

import android.content.Context;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;


public class HelpUtils {

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getStatusBarHeight(Context context) {
        Resources res = context.getResources();
        int resId = res.getIdentifier("status_bar_height", "dimen", "android");
        return res.getDimensionPixelSize(resId);

        // Can also be reflected
        /**
         Class<?> c = null;
         Object obj = null;
         Field field = null;
         int x = 0, statusBarHeight = 0;
         try {
         c = Class.forName("com.android.internal.R$dimen");
         obj = c.newInstance();
         field = c.getField("status_bar_height");
         x = Integer.parseInt(field.get(obj).toString());
         statusBarHeight = context.getResources().getDimensionPixelSize(x);
         } catch (Exception e1) {
         e1.printStackTrace();
         }
         return statusBarHeight;
         */
    }

    /**
     * Time display  for Users
     *
     * @param nowTime Current time in milliseconds
     * @param preTime  Previous time in milliseconds
     * @return Time display in line with user habits
     */
    public static String calculateShowTime(long nowTime, long preTime) {
        if (nowTime <= 0 || preTime <= 0)
            return null;
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd-HH-mm-E");
        String now = format.format(new Date(nowTime));
        String pre = format.format(new Date(preTime));
        String[] nowTimeArr = now.split("-");
        String[] preTimeArr = pre.split("-");
        // Within the day, the year, month, and day are the same, and the time display is more than one minute
        if (nowTimeArr[0].equals(preTimeArr[0]) && nowTimeArr[1].equals(preTimeArr[1]) && nowTimeArr[2].equals(preTimeArr[2]) && nowTime - preTime > 60000) {
            return preTimeArr[3] + ":" + preTimeArr[4];
        }
        // Within a week
        else if (Integer.valueOf(nowTimeArr[2]) - Integer.valueOf(preTimeArr[2]) > 0 && nowTime - preTime < 7 * 24 * 60 * 60 * 1000) {

            if (Integer.valueOf(nowTimeArr[2]) - Integer.valueOf(preTimeArr[2]) == 1)
                return "yesterday " + preTimeArr[3] + ":" + preTimeArr[4];
            else
                return preTimeArr[5] + " " + preTimeArr[3] + ":" + preTimeArr[4];
        }
        //More than one week
        else if (nowTime - preTime > 7 * 24 * 60 * 60 * 1000) {
            return preTimeArr[0] + "year" + preTimeArr[1] + "month" + preTimeArr[2] + "day" + " " + preTimeArr[3] + ":" + preTimeArr[4];
        }
        return null;
    }


    public static long getCurrentMillisTime() {
        return System.currentTimeMillis();
    }


}
