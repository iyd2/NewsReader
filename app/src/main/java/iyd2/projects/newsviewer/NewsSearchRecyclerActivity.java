package iyd2.projects.newsviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class NewsSearchRecyclerActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, NewsSearchRecyclerActivity.class);
    }

    @Override
    public Fragment createFragment() {
        return NewsSearchRecyclerFragment.newInstance();
    }


}
