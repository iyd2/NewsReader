package iyd2.projects.newsviewer;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class ImageDownloader<T> extends HandlerThread {

    private static final String TAG = "ImageDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;
    private boolean mHasStopped;
    private Handler mRequestHandler;
    private Handler mResponseHandler;
    private ImageDownloadListener<T> mImageDownloadListener;
    private ConcurrentHashMap<T, String> mHashMap = new ConcurrentHashMap<>();
    private Point mReqSize;

    public interface ImageDownloadListener<T> {
        void onImageDownloaded(T holder, Bitmap bitmap);
    }

    public void setImageDownloadListener(ImageDownloadListener<T> listener) {
        mImageDownloadListener = listener;
    }

    public ImageDownloader(Point reqSize, Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mReqSize = reqSize;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                T obj = (T) msg.obj;
                handleRequest(obj);
            }
        };
    }

    private void handleRequest(final T obj) {
        final String url = mHashMap.get(obj);

        if (url == null) {
            return;
        }

        try {
            byte[] imageBytes = new NewsFetcher().getUrlBytes(url);
            final Bitmap imageBitmap = BitmapUtil.decodeByteArray(imageBytes, mReqSize.x);

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mHasStopped || mHashMap.get(obj) != url) {
                        return;
                    }

                    mHashMap.remove(obj);
                    mImageDownloadListener.onImageDownloaded(obj, imageBitmap);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queueImage(T holder, String url) {

        // Из-за повторного использования объектов
        // ViewHolder, удаляем из коллекции объекты,
        // ссылки на которые пусты.

        if (url == null) {
            mHashMap.remove(holder);
        } else {
            mHashMap.put(holder, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, holder)
                    .sendToTarget();
        }
    }

    public boolean quit() {
        mHasStopped = true;
        return super.quit();
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
        mHashMap.clear();
    }
}
