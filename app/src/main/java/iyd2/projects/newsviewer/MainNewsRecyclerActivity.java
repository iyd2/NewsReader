package iyd2.projects.newsviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainNewsRecyclerActivity extends AppCompatActivity {

    private static final String TAG = "MainNewRecyclerActivity";
    private ViewPager mNewsPager;
    private String[] mCategories;

    public static Intent newIntent(Context context) {
        return new Intent(context, MainNewsRecyclerActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_news_pager);

        mCategories = getResources().getStringArray(R.array.categories);

        mNewsPager = findViewById(R.id.activity_pager_view);
        mNewsPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return MainNewsRecyclerFragment.newInstance(mCategories[position]);
            }

            @Override
            public int getCount() {
                return mCategories.length;
            }
        });
        mNewsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setTitle(getStringResourceByName(mCategories[position]));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }
}
