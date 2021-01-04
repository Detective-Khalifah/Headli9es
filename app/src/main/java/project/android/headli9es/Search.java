package project.android.headli9es;

import android.text.TextUtils;
import android.util.Log;

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

    private final static String LOG_TAG = Search.class.getName();

    /**
     * @param requestURL an initial, unready search query
     */
    protected static List<News> lookUpVolumes (String requestURL) {
        Log.i(LOG_TAG, "lookUpVolumes in action.");

        // Create URL obj
        URL url = createURL(requestURL);

        // Perform HTTP request to the URL and receive a JSON response back
        String JSONResponse = null;

        try {
            JSONResponse = makeHTTPRequest(url);
        } catch (IOException ignored) {

        }

        // Generate a list of newsList from fetched JSON
        List<News> articles = extractNYTData(JSONResponse);
//        List<News> articles = extractJSONData(JSONResponse);
//        Log.i(LOG_TAG, "SearchResult List is made of: " + articles);
        return articles;
    }

    /**
     * @param stringURL
     * @return
     */
    private static URL createURL (String stringURL) {
        URL url = null;
        try {
            url = new URL(stringURL);
        } catch (MalformedURLException mURLE) {

        }
        Log.i(LOG_TAG, "createURL::" + url);
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
            urlConn.connect();

            // If the request was successful (response code 200), then read the input stream and parse the response.
            switch (urlConn.getResponseCode() ) {
                case HttpURLConnection.HTTP_OK:
                    Log.i(LOG_TAG, "from makeHTTPRequest: Response code 200");

                    inputStream = urlConn.getInputStream();
//                    Log.i(LOG_TAG, "from makeHTTPRequest inputStream:: " + inputStream/*.read()*/);

                    JSONResponse = readFromStream(inputStream);
//                    Log.i(LOG_TAG, "from makeHTTPRequest JSONResponse:: " + JSONResponse);
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                case 429:
                    Log.i(LOG_TAG, "from makeHTTPRequest response::");
                    break;
                default:
                    Log.i(LOG_TAG, "from makeHTTPRequest responseCode:: " + urlConn.getResponseCode());
                    Log.i(LOG_TAG, "from makeHTTPRequest responseMessage:: " + urlConn.getResponseMessage());
//                    Log.i(LOG_TAG, "from makeHTTPRequest ErrorStream:: " + urlConn.getErrorStream().read());
                    Log.i(LOG_TAG, "from makeHTTPRequest inputStream:: " + urlConn.getInputStream().read());
//                    Toast.makeText(MainActivity.class, "Please enable network connection", Toast.LENGTH_LONG).show();
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

    private static List<News> extractNYTData (String newsJSONResponse) {

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

            /** From root JSON OBJ:: {@link articlesNumber}, {@link articlesArray}, {@link copyrright}, {@link section}, {@link status}, {@link last_updated} */
            // Number of articles
            int articlesNumber = rootJSONObj.getInt("num_results");

            // Extract the JSONArray associated with the key called "results"
            // which represents a list newsList.
            JSONArray articlesArray = rootJSONObj.getJSONArray("results");

            // For each article in the articlesArray, create a {@link news} object
            for (int i = 0; i < articlesArray.length(); i++) {
                JSONObject currentArticle = articlesArray.getJSONObject(i);

                /* Extract content */
                String content = currentArticle.optString("abstract");
                Log.i(LOG_TAG, "content:: " + content);

                /* Extract an excerpt of news article */
                String description = currentArticle.optJSONArray("des_facet").toString();
//                for (int j = 0; j < description.length(); j++) {
//                    description.getString(j);
//                }

                /* Extract publish_date*/
                String publish_date = currentArticle.optString("published_date");
                Log.i(LOG_TAG, "publish_date:: " + publish_date);

                /* Extract news sourceName */
                String sourceName = currentArticle.optString("byline");
                Log.i(LOG_TAG, "sourceName:: " + sourceName);

                /* Extract title */
                String title = currentArticle.optString("title");
                Log.i(LOG_TAG, "title:: " + title);

                /* Extract news link - a uri for the current article,
                 which can be used to uniquely open article info online */
                String web_page = currentArticle.optString("short_url");
                Log.i(LOG_TAG, "web_page:: " + web_page);

                /** Thumbnail deets
                 * multimedia array, has 5 image objects:: format (height, width)
                 * 0 => superJumbo (1366, 2048)
                 * 1 => Standard Thumbnail (75, 75)
                 * 2 => thumbLarge (150, 150)
                 * 3 => mediumThreeByTwo210 (140, 210)
                 * 4 => Normal (127, 190)
                 * */
                JSONArray images = currentArticle.getJSONArray("multimedia");
                String thumbnailURL = images.getJSONObject(1).optString("url");
                String thumbnailCaption = images.getJSONObject(1).optString("caption");
                String thumbnailSrc = images.getJSONObject(1).optString("copyright");
                Log.i(LOG_TAG, "Image Thumbnail::" + "\nthumbnailURL"+ thumbnailURL + "\nthumbnailCaption" + thumbnailCaption + "\nthumbnailSrc" + thumbnailSrc);

                news.add(new News(articlesNumber,
                        content,
                        description,
                        publish_date,
                        sourceName,
                        title,
                        web_page));
            }

        } catch (
                JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.i(LOG_TAG, "Problem parsing the JSON results_page", e);
        }

        // Return the list (polymorphed ArrayList) of newsList
        return news;
    }

}