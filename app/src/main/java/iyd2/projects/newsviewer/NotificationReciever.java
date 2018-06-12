package iyd2.projects.newsviewer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class NotificationReciever extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Recieved intent");

        if (getResultCode() != Activity.RESULT_OK) {
            return;
        }

        Log.d(TAG, "Show notification");
        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE, 0);
        Notification notification = intent.getParcelableExtra(PollService.NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);;
        notificationManager.notify(requestCode, notification);
    }
}
