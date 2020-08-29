package project.android.headli9es;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import project.android.headli9es.databinding.ForecastBinding;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsCarrier> {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    private ArticleClickListener articleClickListener;
    private ArrayList<News> news;
    static int articlesNum;
    protected ForecastBinding binder;
    News currentArticle;
    Activity activity;

    public NewsAdapter (List<News> data, ArticleClickListener listener, Activity theActivity) {
        Log.d(LOG_TAG, "This is NewsPopulator.");
        this.articleClickListener = listener;
        this.news = (ArrayList<News>) data;
        this.activity = theActivity;
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
        currentArticle = news.get(position);
//        binder.setNews(currentArticle);
//        binder.executePendingBindings();

        Log.d(LOG_TAG, "currentArticle:: " + currentArticle);

        Log.d(LOG_TAG, "numArticles:: " + currentArticle.getArticlesNumber());
        binder.tvArticlesCount.numArticles.setText(String.valueOf(currentArticle.getArticlesNumber()));

        Log.d(LOG_TAG, "source:: " + currentArticle.getSource());
        binder.newsSource.setText(currentArticle.getSource());

        Log.d(LOG_TAG, "description:: " + currentArticle.getDescription());
        binder.newsDescription.setText(currentArticle.getDescription());
    }

    @Override
    public int getItemCount () {
        Log.d(LOG_TAG, /*"getItemCount:: " + currentArticle.getArticlesNumber() + */"\narticlesNum::" + articlesNum);
        return news != null ? news.size() : 0;
    }

    /**
     *
     */
    protected class NewsCarrier extends RecyclerView.ViewHolder implements View.OnClickListener {

        public NewsCarrier(ForecastBinding forecastBinding/*, ViewGroup parent*/) {
            super(forecastBinding.getRoot());
            forecastBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick (View view) {
            articleClickListener.onArticleClickListener(getAdapterPosition());
        }
    }
}

//    @Override
//    public View getView (int position, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, parent, false);
//        }
//        News currentArticle = getItem(position);
//
//        TextView numArticles = convertView.findViewById(R.id.num_articles);
//        numArticles.setText(R.string.total_articles) ;
//        numArticles.append(String.valueOf(currentArticle.getArticlesNumber()));
//
//        TextView source = convertView.findViewById(R.id.news_source);
//        source.setText(String.format("%s, for %s.", currentArticle.getAuthor(), currentArticle.getSource()));
//
//        TextView description = convertView.findViewById(R.id.news_description);
//        description.setText(currentArticle.getDescription());
//        description.setText(currentArticle.getTitle());
//
//        TextView date = convertView.findViewById(R.id.news_date);
//        date.setText(currentArticle.getDate());
//        }

//        binder = DataBindingUtil.setContentView(activity, R.layout.forecast); 'nother
//        View view = roller.inflate(articleLayoutId, parent, false); 'nother
//        binder = DataBindingUtil.inflate(roller, R.layout.forecast, parent, false);
//        View viewRoot = roller.inflate(articleLayoutId, parent, false); 'nother
//        ForecastBinding binder = DataBindingUtil.bind(viewRoot);
//        return new NewsCarrier(view);

//        public NewsCarrier (View itemView) {super(itemView); itemView.setOnClickListener(this);}