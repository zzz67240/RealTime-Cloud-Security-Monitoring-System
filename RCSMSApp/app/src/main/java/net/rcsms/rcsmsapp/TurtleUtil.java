package net.rcsms.rcsmsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 一般公用程式
 * 
 * @author macdidi
 */
public class TurtleUtil {
	
	// 讀取與寫入Preference資料的物件
    private static SharedPreferences sp = null;

    // 儲存使用者帳號與密碼的Preference資料名稱
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_USER_NAME = "USER_NAME";
    
    // 儲存伺服器IP位址、埠號與自動更新設定的Preference資料名稱
    public static final String KEY_SERVER_IP = "SERVER_IP";
    public static final String KEY_SERVER_PORT = "SERVER_PORT";
    public static final String KEY_AUTO_UPDATE = "AUTO_UPDATE";

    public static final String KEY_CUSTOMERID = "CUSTOMERID";
    public static final String KEY_PASSWORD = "PASSWORD";

    /**
     * 儲存使用者帳號與名稱
     * 
     * @param context Android Context物件 
     * @param id 登入的使用者帳號
     * @param name 登入的使用者名稱 
     */
    public static void setAccount(Context context, String id, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(context)
                .edit();
        editor.putString(KEY_USER_ID, id);
        editor.putString(KEY_USER_NAME, name);
        editor.commit();
    }

    /**
     * 取得登入的使用者帳號資訊
     * 
     * @param context Android Context物件
     * @return 登入的使用者帳號
     */
    public static String getUserId(Context context) {
        return getSharedPreferences(context).getString(KEY_USER_ID, "");
    }

    /**
     * 取得登入的使用者名稱資訊
     * 
     * @param context Android Context物件
     * @return 登入的使用者名稱
     */
    public static String getUserName(Context context) {
        return getSharedPreferences(context).getString(KEY_USER_NAME, "");
    }

    /**
     * 取得儲存的伺服器IP位址資訊
     * 
     * @param context Android Context物件
     * @return 儲存的伺服器IP位址
     */
    public static String getServerIP(Context context) {
        return getSharedPreferences(context).getString(
                KEY_SERVER_IP, "192.168.1.1");
    }

    /**
     * 取得儲存的伺服器埠號資訊
     * 
     * @param context Android Context物件
     * @return 儲存的伺服器埠號
     */
    public static String getServerPort(Context context) {
        return getSharedPreferences(context).getString(
                KEY_SERVER_PORT, "8080");
    }

    /**
     * 取得儲存的自動更新資訊
     * 
     * @param context Android Context物件
     * @return 是否自動更新
     */
    public static boolean getAutoUpdate(Context context) {
        return getSharedPreferences(context).getBoolean(
                KEY_AUTO_UPDATE, false);
    }

    /**
     * 取得目前的格式化日期
     * 
     * @param format 日期格式
     * @return 格式化日期
     */
    public static String getFormatDate(String format) {
        Date now = new Date();
        // 預設區域的格式化物件
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(now);
    }

    /**
     * 把字串轉換為int整數
     * 
     * @param s 要轉換為int整數的字串
     * @return 轉換後的int整數，如果字串格式錯誤的話傳回0
     */
    public static int parseInt(String s) {
        int result = 0;

        try {
            result = Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
        	// do nothing
        }

        return result;
    }

    /**
     * 合併陣列
     * 
     * @param x 要合併的陣列
     * @param y 要合併的陣列
     * @return 合併後的陣列
     */
    public static int[] mergeArray(int[] x, int[] y) {
        int lengthX = x.length;
        int lengthY = y.length;
        int[] result = new int[lengthX + lengthY];

        for (int i = 0; i < lengthX; i++) {
            result[i] = x[i];
        }

        for (int i = 0; i < lengthY; i++) {
            result[i + lengthX] = y[i];
        }

        return result;
    }

    /**
     * 合併陣列
     * 
     * @param x 要合併的陣列
     * @param y 要合併的陣列
     * @return 合併後的陣列
     */
    public static String[] mergeArray(String[] x, String[] y) {
        int lengthX = x.length;
        int lengthY = y.length;
        String[] result = new String[lengthX + lengthY];

        for (int i = 0; i < lengthX; i++) {
            result[i] = x[i];
        }

        for (int i = 0; i < lengthY; i++) {
            result[i + lengthX] = y[i];
        }

        return result;
    }

    // 取得Android應用程式預設的SharedPreferences物件
    private static SharedPreferences getSharedPreferences(Context context) {
        if (sp == null) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }
        else {
            return sp;
        }
    }

    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        if (info == null || !info.isConnected()) {
            return false;
        }

        return true;
    }

    public static void savePref(Context context, String key, String value) {
        SharedPreferences.Editor editor =
                getSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getPref(Context context, String key, String def) {
        return getSharedPreferences(context).getString(key, def);
    }

}
