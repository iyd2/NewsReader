package iyd2.projects.newsviewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class NewsItemActivity extends SingleFragmentActivity {

    public static final String EXTRA_URL = "news_item_url";

    public static Intent newIntent(Context context, String url) {

        Intent intent = new Intent(context, NewsItemActivity.class);
        intent.putExtra(EXTRA_URL, url);

        return intent;
    }

    @Override
    public Fragment createFragment() {
        return NewsItemFragment.newInstance(getIntent().getStringExtra(EXTRA_URL));
    }
}
