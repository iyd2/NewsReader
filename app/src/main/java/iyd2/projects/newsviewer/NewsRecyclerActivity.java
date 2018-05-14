package iyd2.projects.newsviewer;

import android.support.v4.app.Fragment;

public class NewsRecyclerActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return NewsRecyclerFragment.newInstance();
    }

}
