package iyd2.projects.newsviewer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static iyd2.projects.newsviewer.BitmapUtil.getImageSize;

public class NewsRecyclerFragment extends RecyclerFragment {

    private static final String TAG = "NewsRecyclerFragment";

    private SwipeRefreshLayout mSwipeRefreshLayout;
 //   private RecyclerView mRecyclerView;
  //  private ImageDownloader<NewsHolder> mImageDownloader;
  //  private List<NewsItem> mNewsItems = new LinkedList<>();

    public static Fragment newInstance() {
        return new NewsRecyclerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        setRetainInstance(true);
        // Получение обратных вызовов меню.
        setHasOptionsMenu(true);

        new FetchNewsItems(null).execute();

        PollService.setServiceAlarm(getActivity(), true);

        /*Handler responseHandler = new Handler();

        mImageDownloader = new ImageDownloader<>(getImageSize(getActivity()), responseHandler);
        mImageDownloader.setImageDownloadListener(new ImageDownloader.ImageDownloadListener<NewsHolder>() {
            @Override
            public void onImageDownloaded(NewsHolder holder, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                holder.onBindViewDrawable(drawable);
            }
        });
        mImageDownloader.start();
        mImageDownloader.getLooper();*/

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        //View v = inflater.inflate(R.layout.news_recycler, container, false);
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mSwipeRefreshLayout = v.findViewById(R.id.recycler_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchNewsItems(mNewsItems.get(0).getPublishedAt()).execute();
                Log.d(TAG, "last date = " + mNewsItems.get(0).getPublishedAt().toString());

            }
        });
        /*mRecyclerView = v.findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // Предотвращает вызовы элеменов дерева представлений в recyclerview
        // (не перерисовывает все элементы views/layouts).
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();*/
        return v;
    }


    /*@Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.i(TAG, "onDestroyView");

        mImageDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(TAG, "onDestroy");

        mImageDownloader.quit();
    }*/

    private class FetchNewsItems extends AsyncTask<Void, Void, List<NewsItem>> {

        private Date lastDate;

        public FetchNewsItems(Date lastDate) {
            this.lastDate = lastDate;
        }

        @Override
        protected List<NewsItem> doInBackground(Void... voids) {
            return new NewsFetcher().fetchNewsItems(lastDate);
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

    /* Вызывается при создании нового объекта RecyclerView.
    public void setupAdapter() {
        if (isAdded()) {
            Log.i(TAG, "setupAdapter");
            mRecyclerView.setAdapter(new NewsAdapter(mNewsItems));
        }
    }*/

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

    /*private class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mItemTitle;
        private ImageView mItemImage;
        private TextView mIemPublishedAt;
        private NewsItem mNewsItem;
        private Drawable mImageBackground;

        public NewsHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mItemTitle = itemView.findViewById(R.id.news_item_title);
            mItemImage = itemView.findViewById(R.id.news_item_image);
            mIemPublishedAt = itemView.findViewById(R.id.news_item_published_at);
            mImageBackground = getResources().getDrawable(R.drawable.image_background);
        }

        public void onBindNewsItem(NewsItem item) {
            mNewsItem = item;
            mItemTitle.setText(mNewsItem.getTitle());
            String publishedAt = mNewsItem.getPublishedAt().toString();
            if (publishedAt != null) {
                mIemPublishedAt.setText(publishedAt);
            }
        }

        public void onBindViewDrawable(Drawable drawable) {
            TransitionDrawable mTransitionDrawable = new TransitionDrawable(new Drawable[]{mImageBackground, drawable});
            mItemImage.setImageDrawable(mTransitionDrawable);
            mTransitionDrawable.startTransition(300);
        }

        public void clearImage() {
            mItemImage.setImageDrawable(null);
        }

        @Override
        public void onClick(View v) {
            startActivity(NewsItemActivity.newIntent(getActivity(), mNewsItem.getUrl()));
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

        private List<NewsItem> mNewsItems;

        public NewsAdapter(List<NewsItem> items) {
            mNewsItems = items;
        }

        @Override
        public int getItemViewType(int position) {
            if (mNewsItems.get(position).getUrlToImage() != null) {
                return R.layout.recycler_imaged_news_item;
            } else {
                return R.layout.recycler_news_item;
            }
        }

        @NonNull
        @Override
        public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_imaged_news_item, parent, false);;
            switch (viewType) {
                case R.layout.recycler_imaged_news_item:
                    break;
                case R.layout.recycler_news_item:
                    itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_news_item, parent, false);
                    break;
            }

            return new NewsHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
            NewsItem item = mNewsItems.get(position);
            holder.onBindNewsItem(item);

            String imageUrl = item.getUrlToImage();
            if (imageUrl != null) {
                holder.clearImage();
                mImageDownloader.queueImage(holder, imageUrl);
            }
        }

        @Override
        public int getItemCount() {
            return mNewsItems.size();
        }
    }*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.news_recycler_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                getActivity().startActivity(NewsSearchActivity.newIntent(getActivity()));
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