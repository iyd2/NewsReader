package iyd2.projects.newsviewer;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerFragment extends Fragment {

    private static final String TAG = "NewsRecyclerFragment";

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageDownloader<NewsHolder> mImageDownloader;
    private List<NewsItem> mNewsItems = new ArrayList<>();

    public static Fragment newInstance() {
        return new NewsRecyclerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        new FetchNewsItems().execute();

        Handler responseHandler = new Handler();
        mImageDownloader = new ImageDownloader<>(responseHandler);
        mImageDownloader.setImageDownloadListener(new ImageDownloader.ImageDownloadListener<NewsHolder>() {
            @Override
            public void onImageDownloaded(NewsHolder holder, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                holder.onBindViewDrawable(drawable);
            }
        });
        mImageDownloader.start();
        mImageDownloader.getLooper();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.news_recycler, container, false);

        mSwipeRefreshLayout = v.findViewById(R.id.recycler_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchNewsItems().execute();

            }
        });
        mRecyclerView = v.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mImageDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageDownloader.quit();
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
            Log.i(TAG, "Fragment has been added");
            NewsAdapter adapter = new NewsAdapter(mNewsItems);
            mRecyclerView.setAdapter(adapter);
            adapter.update();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    private class NewsHolder extends RecyclerView.ViewHolder {

        private TextView mItemTitle;
        private ImageView mItemImage;
        private NewsItem mNewsItem;

        public NewsHolder(View itemView) {
            super(itemView);

            mItemTitle = itemView.findViewById(R.id.news_item_title);
            mItemImage = itemView.findViewById(R.id.news_item_image);
        }

        public void onBindNewsItem(NewsItem item) {
            mNewsItem = item;
            mItemTitle.setText(mNewsItem.getTitle());
        }

        public void onBindViewDrawable(Drawable drawable) {
            mItemImage.setImageDrawable(drawable);
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

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
            NewsItem item = mNewsItems.get(position);
            holder.onBindNewsItem(item);
            mImageDownloader.queueImage(holder, item.getUrlToImage());
        }

        @Override
        public int getItemCount() {
            return mNewsItems.size();
        }

        public void update() {
            notifyDataSetChanged();
        }

    }



}
