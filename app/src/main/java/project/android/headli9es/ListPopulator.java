package project.android.headli9es;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListPopulator extends ArrayAdapter<News> {

    private ArrayList<News> news;

    public ListPopulator (Context context, List<News> data) {
        super(context, 0, data);
        data = news;
    }

    @NonNull
    @Override
    public View getView (int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, parent, false);
        }

        News currentArticle = getItem(position);

        TextView numArticles = convertView.findViewById(R.id.num_articles);
        numArticles.setText(R.string.total_articles +  currentArticle.getArticlesNumber());

        TextView source = convertView.findViewById(R.id.news_source);
        source.setText(String.format("%s, for %s.", currentArticle.getAuthor(), currentArticle.getSource()));

        TextView description = convertView.findViewById(R.id.news_description);
//        description.setText(currentArticle.getDescription());
        description.setText(currentArticle.getTitle());

        TextView date = convertView.findViewById(R.id.news_date);
        date.setText(currentArticle.getDate());

        return convertView;
//        return super.getView(position, convertView, parent);
    }
}