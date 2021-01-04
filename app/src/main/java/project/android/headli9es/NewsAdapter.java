package project.android.headli9es;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import project.android.headli9es.databinding.ForecastBinding;

import static androidx.core.content.ContextCompat.startActivity;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsCarrier> {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    private final ArticleClickListener articleClickListener;
    private final ArrayList<News> news;
    protected ForecastBinding binder;

    public interface ArticleClickListener {
        void onArticleClickListener (String link);
    }

    public NewsAdapter (List<News> data, ArticleClickListener listener) {
        Log.d(LOG_TAG, "This is NewsPopulator.");
        articleClickListener = listener;
        news = (ArrayList<News>) data;
    }

    @Override
    public NewsCarrier onCreateViewHolder (ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder::");
        Context context = parent.getContext();
        LayoutInflater roller = LayoutInflater.from(context);

        binder = ForecastBinding.inflate(roller, parent, false); //works
        return new NewsCarrier(binder/*, parent*/);
    }

    @Override
    public void onBindViewHolder (NewsCarrier holder, int position) {
        Log.d(LOG_TAG, "onBindViewHolder.");
        binder.setNews(news.get(position));
        binder.executePendingBindings();
    }

    @Override
    public int getItemCount () {
        Log.d(LOG_TAG,"\narticlesNum::" + news.size());
        return news != null ? news.size() : 0;
    }

    /**
     *
     */
    protected class NewsCarrier extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NewsCarrier(ForecastBinding forecastBinding/*, ViewGroup parent*/) {
            super(forecastBinding.getRoot());
            binder = forecastBinding;
            forecastBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick (View view) {
            articleClickListener.onArticleClickListener(news.get(getAdapterPosition()).getPage());
        }
    }
}