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

public class NewsFetcher {

    private static final String TAG = "NewsFetcher";

    private static final String API_KEY = "8e86a3242262432e88bd7a723e457b8c";
    private static final String ENDPOINT_BASE = "https://newsapi.org/v2/";
    private static final String ENDPOINT_TOP = "top-headlines";
    private static final String ENDPOINT_EVERYTHING = "everything";
    private static final String ENDPOINT_SOURCES = "sources";


    public List<NewsItem> fetchNewsItems(Date lastDate) {
        List<NewsItem> items = new LinkedList<>();

        try {
            String urlSpec = Uri.parse(ENDPOINT_BASE + ENDPOINT_TOP)
                    .buildUpon()
                    .appendQueryParameter("country", "us")
                    //.appendQueryParameter("sources", "rt,lenta")
                    .appendQueryParameter("apiKey", API_KEY)
                    .build()
                    .toString();

            Log.i(TAG, urlSpec);
            JSONObject jsonResponse = new JSONObject(getUrlString(urlSpec));

            if (lastDate == null) {
                parseItems(items, jsonResponse);
            } else {
                parseRecentItems(items, jsonResponse, lastDate);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void parseItems(List<NewsItem> items, JSONObject jsonObject) {

        try {
            JSONArray jsonArticles = jsonObject.getJSONArray("articles");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");

            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject jsonItem =  jsonArticles.getJSONObject(i);

                NewsItem item = new NewsItem(jsonItem.getString("title"),
                        jsonItem.getString("description"));

                Log.d(TAG, item.getTitle());
                Date publishedAt = dateFormat.parse(jsonItem.getString("publishedAt"));
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

    public void parseRecentItems(List<NewsItem> items, JSONObject jsonObject, Date lastDate) {

        try {
            JSONArray jsonArticles = jsonObject.optJSONArray("articles");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss");

            for (int i = 0; i < jsonArticles.length(); i++) {
                JSONObject jsonItem = jsonArticles.optJSONObject(i);

                Date publishedAt = dateFormat.parse(jsonItem.getString("publishedAt"));

                if (publishedAt.compareTo(lastDate) <= 0) {
                    break;
                }

                NewsItem item = new NewsItem(jsonItem.getString("title"),
                        jsonItem.getString("description"));

                if (jsonItem.has("urlToImage")) {
                    String urlToImage = jsonItem.getString("urlToImage");
                    item.setUrlToImage(isUrlValid(urlToImage) ? urlToImage : null);
                }

                item.setUrl(jsonItem.getString("url"));

                item.setPublishedAt(publishedAt);

                items.add(item);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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



}
