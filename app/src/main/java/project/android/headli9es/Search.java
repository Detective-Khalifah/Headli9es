package project.android.headli9es;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Search {
    private static final String grab = "6111dbc091194e9d9c5ba3d413d15971";

    /**
     * @param requestURL an initial, unready search query
     */
    protected static List<News> lookUpVolumes (String requestURL) {
        Log.i(Search.class.getName(), "lookUpVolumes in action.");

        // Create URL obj
        URL url = createURL(requestURL);

        // Perform HTTP request to the URL and receive a JSON response back
        String JSONResponse = null;

        try {
            JSONResponse = makeHTTPRequest(url);
        } catch (IOException e) {

        }

        // Generate a list of newsList from fetched JSON
        List<News> articles = extractJSONData(JSONResponse);
//        Log.i(Search.class.getName(), "SearchResult List is made of: " + articles);
        return articles;
    }

    /**
     * @param stringURL
     * @return
     */
    private static URL createURL (String stringURL) {
        Log.i(Search.class.getName(), "createURL");
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException mURLE) {

        }
        return url;
    }

    /**
     * @param url
     * @return
     * @throws IOException
     */
    private static String makeHTTPRequest (URL url) throws IOException {
        String JSONResponse = "";

        // If the URL is null, then return early
        if (url == null) {
            return JSONResponse;
        }

        HttpURLConnection urlConn = null;
        InputStream inputStream = null;

        try {
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("X-Api-Key", grab);
            urlConn.connect();

            // If the request was successful (response code 200), then read the input stream and parse the response.
            if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i(Search.class.getName(), "from makeHTTPRequest: Response code 200");

                inputStream = urlConn.getInputStream();
//                Log.i(Search.class.getName(), "inputStream:: " + inputStream);

                JSONResponse = readFromStream(inputStream);
//                Log.i(Search.class.getName(), "JSONResponse:: " + JSONResponse);

            } else {
//                 Toast.makeText(MainActivity.getApplicationContext(), "Please enable network connection", Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {

        } finally {
            if (urlConn != null)
                urlConn.disconnect();
            if (inputStream != null)
                inputStream.close();
        }
        return JSONResponse;
    }

    /**
     * @param inStream
     * @return
     * @throws IOException
     */
    private static String readFromStream (InputStream inStream) throws IOException {
        StringBuilder myList = new StringBuilder();
        if (inStream != null) {
            InputStreamReader inStreamDecode = new InputStreamReader(inStream);
            BufferedReader reader = new BufferedReader(inStreamDecode);

            String line = reader.readLine();
            while (line != null) {
                myList.append(line);
                line = reader.readLine();
            }
        }
        return myList.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<News> extractJSONData (String newsJSONResponse) {

        // If the JSON String is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSONResponse))
            return null;

        // Create an empty ArrayList that we can start adding newsList to
        List<News> news = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        // Otherwise, build up a list of News objects with the corresponding data.
        try {

            // Create a JSON object from the SAMPLE_JSON_RESPONSE string
            JSONObject rootJSONObj = new JSONObject(newsJSONResponse);

            // Number of articles
            String articlesNumber = rootJSONObj.getString("totalResults");

            // Extract the JSONArray associated with the key called "articles"
            // which represents a list newsList.
            JSONArray articlesArray = rootJSONObj.getJSONArray("articles");

            // For each article in the articlesArray, create a {@link news} object
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject currentArticle = articlesArray.getJSONObject(i);

                /* Extract author(s)*/
                String author = currentArticle.optString("author");

                if (author.equals("null")) {
                    /* Make TextView display something nice or disappear. */
                }
                Log.i(Search.class.getName(), "author:: " + author);

                String description = currentArticle.optString("description");

                /* Extract publish_date*/
                String publish_date = currentArticle.optString("publishedAt");
                Log.i(Search.class.getName(), "publish_date:: " + publish_date);

                String source = currentArticle.getJSONObject("source").getString("name");

                /* Extract title*/
                String title = currentArticle.getString("title");
                Log.i(Search.class.getName(), "title:: " + title);

                /* Extract volume/book ID - a unique identifier for the current book, which can be used to uniquely open book info online */
                String web_page = currentArticle.optString("url");
                Log.i(Search.class.getName(), "web_page:: " + web_page);

//                Log.i(Search.class.getName(), "book:: " + book);
                news.add(new News(articlesNumber, author, description, publish_date, source, title, web_page));
            }

        } catch (
                JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(Search.class.getName(), "Problem parsing the JSON results_page", e);
        }

        // Return the list (polymorphed ArrayList) of newsList
        return news;
    }
}