package iyd2.projects.newsviewer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import java.util.Date;
import java.util.List;

import iyd2.projects.newsviewer.utils.QueryPreferences;

public class MainNewsRecyclerFragment extends NewsRecyclerFragment {

    private static final String TAG = "MainNewsRecycleFragment";
    private static final String ARG_CATEGORY = "category";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String mCategory;

    public static Fragment newInstance(String category) {
        Fragment fragment = new MainNewsRecyclerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");

        // Получение обратных вызовов меню.
        setHasOptionsMenu(true);
        mCategory = getArguments().getString(ARG_CATEGORY);

        new FetchNewsItems(mCategory, null).execute();

        PollService.setServiceAlarm(getActivity(), true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.main_news_recycler;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        View v = super.onCreateView(inflater, container, savedInstanceState);


        mSwipeRefreshLayout = v.findViewById(R.id.recycler_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchNewsItems(mCategory, mNewsItems.get(0).getPublishedAt()).execute();
                Log.d(TAG, "last date = " + mNewsItems.get(0).getPublishedAt().toString());

            }
        });
        return v;
    }

    private class FetchNewsItems extends AsyncTask<Void, Void, List<NewsItem>> {

        private Date lastDate;
        private String category;

        public FetchNewsItems(String category, Date lastDate) {
            this.lastDate = lastDate;
            this.category = category;
        }

        @Override
        protected List<NewsItem> doInBackground(Void... voids) {
            return new NewsFetcher().fetchNewsItems(category, lastDate);
        }

        // Выполняется в главном потоке.
        @Override
        protected void onPostExecute(List<NewsItem> items) {

            if (mNewsItems.isEmpty()) {
                mNewsItems = items;
                setupAdapter();
            } else {
                addNewsItems(items);
            }

            if (items.size() != 0) {
                saveLastDate(items.get(0).getPublishedAt());
            }

        }
    }

    public void addNewsItems(List<NewsItem> items) {
        Log.i(TAG, "addNewsItems");
        if (items.size() != 0) {
            Log.d(TAG, "have new items");
            mNewsItems.addAll(0, items);
            NewsAdapter adapter = (NewsAdapter) mRecyclerView.getAdapter();
            //adapter.notifyDataSetChanged();
            adapter.notifyItemRangeInserted(0, items.size());
            mRecyclerView.scrollToPosition(0);
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.news_recycler_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                getActivity().startActivity(NewsSearchRecyclerActivity.newIntent(getActivity()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveLastDate(Date date) {
        QueryPreferences.setLastDateQuery(getContext(), date);
    }

    public Date getLastDate() {
        return QueryPreferences.getLastDateQuery(getContext());
    }

}
