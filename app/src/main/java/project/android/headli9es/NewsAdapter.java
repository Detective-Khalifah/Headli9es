package project.android.headli9es;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import project.android.headli9es.databinding.ForecastBinding;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsCarrier> {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    private ArticleClickListener articleClickListener;
    private ArrayList<News> news;
    protected ForecastBinding binder;

    public NewsAdapter (List<News> data, ArticleClickListener listener) {
        Log.d(LOG_TAG, "This is NewsPopulator.");
        this.articleClickListener = listener;
        this.news = (ArrayList<News>) data;
    }

    public interface ArticleClickListener {
        void onArticleClickListener (int articlePosition);
    }

    @Override
    public NewsCarrier onCreateViewHolder (ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder::");
        Context context = parent.getContext();
        int articleLayoutId = R.layout.forecast;
        LayoutInflater roller = LayoutInflater.from(context);

        binder = ForecastBinding.inflate(roller,parent, false); //works
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
            articleClickListener.onArticleClickListener(getAdapterPosition());
        }
    }
}