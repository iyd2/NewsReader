package iyd2.projects.newsviewer;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewsRecyclerFragment extends Fragment {

    private static final String TAG = "NewsRecyclerFragment";

    private RecyclerView mRecyclerView;

    public static Fragment newInstance() {
        return new NewsRecyclerFragment();
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
        return v;
    }


    private static class NewsHolder extends RecyclerView.ViewHolder {

        public NewsHolder(View itemView) {
            super(itemView);
        }
    }

    private static class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {

        @NonNull
        @Override
        public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull NewsHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }


}
