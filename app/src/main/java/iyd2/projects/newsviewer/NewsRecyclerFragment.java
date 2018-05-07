package iyd2.projects.newsviewer;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerFragment extends Fragment {

    private static final String TAG = "NewsRecyclerFragment";

    private RecyclerView mRecyclerView;
    private List<NewsItem> mNewsItems = new ArrayList<>();

    public static Fragment newInstance() {
        return new NewsRecyclerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        new FetchNewsItems().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_recycler, container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return v;
    }

    private class FetchNewsItems extends AsyncTask<Void, Void, List<NewsItem>> {

        @Override
        protected List<NewsItem> doInBackground(Void... voids) {
            return new NewsFetcher().fetchNewsItems();

        }

        // Выполняется в главном потоке.
        @Override
        protected void onPostExecute(List<NewsItem> items) {
            mNewsItems = items;
            setupAdapter();
        }
    }

    // Вызывается при создании нового объекта RecyclerView.
    public void setupAdapter() {
        if (isAdded()) {
            mRecyclerView.setAdapter(new NewsAdapter(mNewsItems));
        }
    }


    private static class NewsHolder extends RecyclerView.ViewHolder {

        private TextView mItemTitle;
        private NewsItem mNewsItem;

        public NewsHolder(View itemView) {
            super(itemView);

            mItemTitle = itemView.findViewById(R.id.news_item_title);
        }

        public void onBindNewsItem(NewsItem item) {
            mNewsItem = item;
            mItemTitle.setText(mNewsItem.getTitle());
        }
    }

    private static class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

        private List<NewsItem> mNewsItems;

        public NewsAdapter(List<NewsItem> items) {
            mNewsItems = items;
        }

        @NonNull
        @Override
        public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            return new NewsHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
            holder.onBindNewsItem(mNewsItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mNewsItems.size();
        }
    }


}
