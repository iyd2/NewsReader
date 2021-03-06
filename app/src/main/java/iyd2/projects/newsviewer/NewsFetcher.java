package iyd2.projects.newsviewer;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import iyd2.projects.newsviewer.utils.DateUtil;

public class NewsFetcher {

    private static final String TAG = "NewsFetcher";
    private static final String API_KEY = "8e86a3242262432e88bd7a723e457b8c";
    private static final String ENDPOINT_BASE = "https://newsapi.org/v2/";
    private static final String ENDPOINT_TOP = "top-headlines";
    private static final String ENDPOINT_EVERYTHING = "everything";
    private static final String ENDPOINT_SOURCES = "sources";


    public List<NewsItem> fetchNewsItems(String category, Date lastDate) {
        List<NewsItem> items = new LinkedList<>();

        try {
            String urlSpec = buildBaseUri(ENDPOINT_TOP)
                    .buildUpon()
                    //.appendQueryParameter("sources", "rt,lenta")
                    .appendQueryParameter("country", "ru")
                    .appendQueryParameter("category", category)
                    .build()
                    .toString();

            Log.i(TAG, urlSpec);
            JSONObject jsonResponse = new JSONObject(getUrlString(urlSpec));

            parseItems(items, jsonResponse, lastDate);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<NewsItem> searchNewsItems(String query) {
        List<NewsItem> items = new LinkedList<>();

        try {
            String urlSpec = buildBaseUri(ENDPOINT_EVERYTHING)
                    .buildUpon()
                    .appendQueryParameter("q", query)
                    .appendQueryParameter("sortBy", "publishedAt")
                    .appendQueryParameter("country", "ru")
                    .appendQueryParameter("language", "ru")
                    //.appendQueryParameter("sources", "rt,lenta")
                    .build()
                    .toString();

            JSONObject jsonResponse = new JSONObject(getUrlString(urlSpec));

            parseItems(items, jsonResponse, null);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void parseItems(List<NewsItem> items, JSONObject jsonObject, Date lastDate) {

        try {
            JSONArray jsonArticles = jsonObject.getJSONArray("articles");
            SimpleDateFormat dateFormat = DateUtil.getMainSimpleDateFormat();

            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject jsonItem =  jsonArticles.getJSONObject(i);

                NewsItem item = new NewsItem(jsonItem.getString("title"),
                        jsonItem.getString("description"));

                Log.d(TAG, item.getTitle());
                Date publishedAt = dateFormat.parse(jsonItem.getString("publishedAt"));

                if (lastDate != null && publishedAt.compareTo(lastDate) <= 0) {
                    break;
                }

                item.setPublishedAt(publishedAt);
                //}

                if (jsonItem.has("urlToImage")) {
                    Log.d(TAG, "hasURL");
                    String urlToImage = jsonItem.getString("urlToImage");
                    item.setUrlToImage(isUrlValid(urlToImage) ? urlToImage : null);
                    Log.d(TAG, "url is valid:" + String.valueOf(isUrlValid(urlToImage)));
                    Log.d(TAG, "url to image:" + urlToImage);
                }

                item.setUrl(jsonItem.getString("url"));

                items.add(item);
            }

        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        } catch (ParseException pe) {
            Log.e(TAG, "Failed to parse Date", pe);
        }
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

    private boolean isUrlValid(String urlSpec) {

        try {
            new URL(urlSpec);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public Uri buildBaseUri(String endpoint) {
        return Uri.parse(ENDPOINT_BASE + endpoint)
                .buildUpon()
                .appendQueryParameter("apiKey", API_KEY)
                .build();
    }
}
