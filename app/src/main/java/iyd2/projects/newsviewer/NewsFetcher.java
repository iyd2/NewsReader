package iyd2.projects.newsviewer;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsFetcher {

    private static final String TAG = "NewsFetcher";

    private static final String API_KEY = "8e86a3242262432e88bd7a723e457b8c";
    private static final String ENDPOINT_BASE = "https://newsapi.org/v2/";
    private static final String ENDPOINT_TOP = "top-headlines";
    private static final String ENDPOINT_EVERYTHING = "everything";
    private static final String ENDPOINT_SOURCES = "sources";

    public List<NewsItem> fetchNewsItems() throws IOException {
        List<NewsItem> items = new ArrayList<>();

        String urlSpec = Uri.parse(ENDPOINT_BASE + ENDPOINT_TOP)
                .buildUpon()
                .appendQueryParameter("country", "ru")
                .appendQueryParameter("apiKey", API_KEY)
                .build()
                .toString();

        String jsonResponse = getUrlString(urlSpec);
        Log.i(TAG, jsonResponse);

        return items;
    }

    public String getUrlString (String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             InputStream in = urlConnection.getInputStream()){

            if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(urlConnection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            return out.toByteArray();

        } finally {
            urlConnection.disconnect();
        }
    }

}
