package iyd2.projects.newsviewer;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollService extends IntentService {

    private static final String TAG = "PollService";
    private static final long POLL_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (isOn) {
            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi);
        } else {
            am.cancel(pi);
            pi.cancel();
        }
    }

    public boolean isServiceAlarmOn(Context context) {
        // Т.к. PendingIntent возвращает тот же объект,
        // и его создание с флагом FLAG_NO_CREATE возвращает
        // null елси объект не существут.
        PendingIntent pi = PendingIntent.getService(context, 0, newIntent(context), PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        Log.i(TAG, "Got new request");

        Date lastDate = QueryPreferences.getLastDateQuery(this);

        List<NewsItem> items = new NewsFetcher().fetchNewsItems(lastDate);

        if (items.size() == 0) {
            return;
        }

        Log.i(TAG, "Got new items");

    }

    public boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkConnected = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkAvailableAndConnected = connectivityManager.getActiveNetworkInfo().isConnected()
                && isNetworkConnected;

        return isNetworkAvailableAndConnected;
    }
}
