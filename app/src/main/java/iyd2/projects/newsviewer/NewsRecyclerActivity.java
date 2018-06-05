package iyd2.projects.newsviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class NewsRecyclerActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, NewsItemActivity.class);
    }

    @Override
    public Fragment createFragment() {
        return NewsRecyclerFragment.newInstance();
    }

}
