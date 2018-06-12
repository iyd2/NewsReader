package iyd2.projects.newsviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

public class NewsRecyclerActivity extends SingleFragmentActivity {

    private static final String TAG = "NewsRecyclerActivity";

    public static Intent newIntent(Context context) {
        return new Intent(context, NewsRecyclerActivity.class);
    }

    @Override
    public Fragment createFragment() {
        Log.d(TAG, "create fragment");
        return NewsRecyclerFragment.newInstance();
    }

}
