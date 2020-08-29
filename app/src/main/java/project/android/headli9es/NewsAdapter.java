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

    private final ArticleClickListener articleClickListener;
    private final ArrayList<News> news;
    protected ForecastBinding binder;

    /**
     * An interface mechanism to handle news article clicks
     */
    protected interface ArticleClickListener {
        void onArticleClickListener (String link);
    }

    /**
     * @param data     List of News articles
     * @param listener an implementation of @{link=ArticleClickListener}
     */
    public NewsAdapter (List<News> data, ArticleClickListener listener) {
        Log.d(LOG_TAG, "This is NewsPopulator.");
        articleClickListener = listener;
        news = (ArrayList<News>) data;
    }

    @Override
    public NewsCarrier onCreateViewHolder (ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater roller = LayoutInflater.from(context);

        return new NewsCarrier(ForecastBinding.inflate(roller, parent, false));
    }

    @Override
    public void onBindViewHolder (NewsCarrier holder, int position) {
        binder.setNews(news.get(position));
        binder.executePendingBindings();
    }

    @Override
    public int getItemCount () {
        return news != null ? news.size() : 0;
    }


    protected class NewsCarrier extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * @Link{Constructor} for initializing the Data Binding object,
         * and setting an @link{View.OnClickListener} on it
         * @param forecastBinding an auto-gen Data Binding object
         */
        public NewsCarrier (ForecastBinding forecastBinding) {
            super(forecastBinding.getRoot());
            binder = forecastBinding;
            forecastBinding.getRoot().setOnClickListener(this);
        }

        /**
         * An override of @link{View.OnClickListener}'s @link{onClick} method
         * for locating the clicked news article
         * @param view item clicked on
         */
        @Override
        public void onClick (View view) {
            articleClickListener.onArticleClickListener(news.get(getAdapterPosition()).getPage());
        }
    }
}