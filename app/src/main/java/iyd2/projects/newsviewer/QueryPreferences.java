package iyd2.projects.newsviewer;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QueryPreferences {
    private static final String TAG = "QueryPreferences";
    private static final String LAST_DATE_QUERY = "last_date";
    private static final String IS_ALARM_ON = "is_alarm_on";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");

    public static Date getLastDateQuery(Context context) {
        String date = PreferenceManager.getDefaultSharedPreferences(context).getString(LAST_DATE_QUERY, null);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
        }

        return null;
    }

    public static void setLastDateQuery(Context context, Date value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(LAST_DATE_QUERY, dateFormat.format(value))
                .apply();
    }

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(IS_ALARM_ON, false);
    }

    public static void setIsAlarmOn(Context context, boolean isAlarmOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(IS_ALARM_ON, isAlarmOn)
                .apply();
    }
}
