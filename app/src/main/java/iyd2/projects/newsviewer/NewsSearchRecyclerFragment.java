package iyd2.projects.newsviewer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.List;

public class NewsSearchRecyclerFragment extends NewsRecyclerFragment {

    public static NewsSearchRecyclerFragment newInstance() {
        return new NewsSearchRecyclerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Получение обратных вызовов меню.
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.search_news_recycler;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.news_search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.news_search_view);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.onActionViewExpanded();
        searchView.setQueryHint(getString(R.string.search_menu_item));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new FetchNewsItems(query).execute();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.news_search_view:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final class FetchNewsItems extends AsyncTask<Void, Void, List<NewsItem>> {

        private String query;

        public FetchNewsItems(String query) {
            this.query = query;
        }

        @Override
        protected List<NewsItem> doInBackground(Void... voids) {
            return new NewsFetcher().searchNewsItems(query);
        }

        @Override
        protected void onPostExecute(List<NewsItem> items) {
            mNewsItems = items;
            setupAdapter();
            if (mNewsItems.size() != 0) {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }
    }
}
