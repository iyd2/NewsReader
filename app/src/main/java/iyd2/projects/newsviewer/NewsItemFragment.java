package iyd2.projects.newsviewer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NewsItemFragment extends VisibleFragment {

    private static final String ARG_URL = "news_item_url";
    private String mUrl;
    private WebView mWebView;

    public static Fragment newInstance(String url) {
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        NewsItemFragment newsItemFragment = new NewsItemFragment();
        newsItemFragment.setArguments(args);
        return newsItemFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getArguments().getString(ARG_URL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.news_item_fragment, container, false);

        mWebView = v.findViewById(R.id.web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(mUrl);

        return v;
    }
}
