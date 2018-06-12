package iyd2.projects.newsviewer;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollService extends IntentService {

    private static final String TAG = "PollService";
    public static final String ACTION_SHOW_NOTIFICATION = "iyd2.projects.newsviewer.SHOW_NOTIFICATION";
    public static final String PERMITION_PRIVATE_BROADCAST = "iyd2.projects.newsviewer.PRIVATE_BROADCAST_INTENT";
    public static final String REQUEST_CODE = "REQUEST_CODE";
    public static final String NOTIFICATION = "NOTIFICATION";

    private static final long POLL_INTERVAL_MS = TimeUnit.HOURS.toMillis(1);
    private NotificationChannel mChannel;

    public PollService() {
        super(TAG);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("NewsReader.Notifications"
                    , "NewsReader notifications"
                    , NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription("NewsReader notifications");
        }
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (isOn) {
            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), POLL_INTERVAL_MS, pi);
        } else {
            am.cancel(pi);
            pi.cancel();
        }

        QueryPreferences.setIsAlarmOn(context, isOn);

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

        Resources resources = getResources();
        Intent i = MainNewsRecyclerActivity.newIntent(this);

        PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            if (nm.getNotificationChannel("NewsReader.Notifications") == null) {
                nm.createNotificationChannel(mChannel);
            }

            notificationBuilder = new NotificationCompat.Builder(this,"NewsReader.Notifications");
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }

        notificationBuilder.setTicker(resources.getString(R.string.notification_ticker))
        .setSmallIcon(android.R.drawable.ic_menu_report_image)
        .setContentTitle(resources.getString(R.string.notification_ticker))
        .setContentText(resources.getString(R.string.notification_ticker))
        .setContentIntent(pi)
        .setAutoCancel(true);

        showBackgroundNotification(0, notificationBuilder.build());
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent i = new Intent(ACTION_SHOW_NOTIFICATION);
        i.putExtra(REQUEST_CODE, requestCode);
        i.putExtra(NOTIFICATION, notification);

        // Порядок приемников определяется приоритетом.
        sendOrderedBroadcast(i, PERMITION_PRIVATE_BROADCAST, null, null,
                Activity.RESULT_OK, null, null);
    }

    public boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkConnected = connectivityManager.getActiveNetworkInfo() != null;
        boolean isNetworkAvailableAndConnected = connectivityManager.getActiveNetworkInfo().isConnected()
                && isNetworkConnected;

        return isNetworkAvailableAndConnected;
    }
}
