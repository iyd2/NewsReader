package iyd2.projects.newsviewer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

public class VisibleFragment extends Fragment {
    private static final String TAG = "VisibleFragment";

    private BroadcastReceiver mOnShowNotification = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Мы получили сообщение =>
            // фрагмент запущен =>
            // отменяем рассылку.
            Log.i(TAG, "As long as we got notification, we set result code to canceling notifications");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        // Региструруем приемник на интенты со специальным разрешением
        // для ограничения приема интентов др. приожениями
        getActivity().registerReceiver(mOnShowNotification, intentFilter, PollService.PERMITION_PRIVATE_BROADCAST, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mOnShowNotification);
    }
}
