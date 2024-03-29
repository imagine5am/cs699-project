package app.insti.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.adapter.NewsAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.NewsArticle;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends RecyclerViewFragment<NewsArticle, NewsAdapter> {

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_news);

        setHasOptionsMenu(true);
        updateData();

        postType = NewsArticle.class;
        adapterType = NewsAdapter.class;
        recyclerView = getActivity().findViewById(R.id.news_recycler_view);
        swipeRefreshLayout = getActivity().findViewById(R.id.news_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
    }

    @Override
    protected Call<List<NewsArticle>> getCall(RetrofitInterface retrofitInterface, String sessionIDHeader, int postCount) {
        return retrofitInterface.getNews(sessionIDHeader, postCount, 20, searchQuery);
    }
}
