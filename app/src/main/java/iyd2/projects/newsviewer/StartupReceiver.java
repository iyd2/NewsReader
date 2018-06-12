package iyd2.projects.newsviewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {

    //Выполняется в гланом потоке.
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isOn = QueryPreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isOn);
    }
}
