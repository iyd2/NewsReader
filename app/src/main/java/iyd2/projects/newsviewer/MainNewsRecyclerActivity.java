package iyd2.projects.newsviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

public class MainNewsRecyclerActivity extends SingleFragmentActivity {

    private static final String TAG = "MainNewsRecyclerActivity";

    public static Intent newIntent(Context context) {
        return new Intent(context, MainNewsRecyclerActivity.class);
    }

    @Override
    public Fragment createFragment() {
        Log.d(TAG, "create fragment");
        return MainNewsRecyclerFragment.newInstance();
    }

}
