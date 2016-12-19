package cn.ac.ict.myo;

import android.app.Application;
import android.content.Context;

/**
 * Author: saukymo
 * Date: 12/14/16
 */

public class MyoApp extends Application{
    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getAppContext() {
        return mContext;
    }
}
