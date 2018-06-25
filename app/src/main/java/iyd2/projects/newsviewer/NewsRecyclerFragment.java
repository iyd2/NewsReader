package iyd2.projects.newsviewer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import iyd2.projects.newsviewer.utils.DateUtil;

import static iyd2.projects.newsviewer.utils.BitmapUtil.getImageSize;

public abstract class NewsRecyclerFragment extends VisibleFragment {

    protected static final String TAG = "NewsRecyclerFragment";
    protected RecyclerView mRecyclerView;
    protected ImageDownloader<NewsHolder> mImageDownloader;
    protected List<NewsItem> mNewsItems = new LinkedList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Handler responseHandler = new Handler();

        mImageDownloader = new ImageDownloader<>(getImageSize(getActivity()), responseHandler);
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

    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(getLayoutId(), container, false);

        mRecyclerView = v.findViewById(R.id.recycler_view);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        // Предотвращает вызовы элеменов дерева представлений в recyclerview
        // (не перерисовывает все элементы views/layouts).
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();

        return v;
    }

    @Override
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
    }

    // Вызывается при создании нового объекта RecyclerView.
    public void setupAdapter() {
        if (isAdded()) {
            Log.i(TAG, "setupAdapter");
            mRecyclerView.setAdapter(new NewsAdapter(mNewsItems));
        }
    }

    protected class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mItemTitle;
        private ImageView mItemImageView;
        private TextView mIemPublishedAt;
        private NewsItem mNewsItem;
        private Drawable mImageBackground;


        public NewsHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            mItemTitle = itemView.findViewById(R.id.news_item_title);
            mItemImageView = itemView.findViewById(R.id.news_item_image);
            mIemPublishedAt = itemView.findViewById(R.id.news_item_published_at);
            mImageBackground = getResources().getDrawable(R.drawable.image_background);
        }

        public void onBindNewsItem(NewsItem item) {
            mNewsItem = item;
            mItemTitle.setText(mNewsItem.getTitle());
            String publishedAt = DateUtil.getTimeAgo(mNewsItem.getPublishedAt().getTime(), getContext());
            if (publishedAt != null) {
                mIemPublishedAt.setText(publishedAt);
            }
        }

        public void onBindViewDrawable(Drawable drawable) {
            TransitionDrawable mTransitionDrawable = new TransitionDrawable(new Drawable[]{mImageBackground, drawable});
            mItemImageView.setImageDrawable(mTransitionDrawable);
            mTransitionDrawable.startTransition(300);
        }

        public void clearImage() {
            mItemImageView.setImageDrawable(null);
        }

        @Override
        public void onClick(View v) {
            startActivity(NewsItemActivity.newIntent(getActivity(), mNewsItem.getUrl()));
        }
    }

    protected class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

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
    }

}
