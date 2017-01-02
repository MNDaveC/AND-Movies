package com.mndavec.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageAdapter moviePosterAdapter;


    final String MOVIES_BASE_URL =
            "https://api.themoviedb.org/3/discover/movie?";
    final String POSTERS_BASE_URL = "https://image.tmdb.org/t/p/";
    final String POSTER_SIZE = "w500";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        moviePosterAdapter =
//                new ArrayAdapter<String>(
//                        this, // The current context (this activity)
//                        R.layout.content_main, // The name of the layout ID.
//                        R.id.grid_posters, // The ID of the textview to populate.
//                        new ArrayList<String>());

        updateMovies();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovies() {
        FetchMovieList movieTask = new FetchMovieList();
        String sort = new String("");
        movieTask.execute(sort);
    }

    public class FetchMovieList extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchMovieList.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

                final String APIKEY_PARAM = "api_key";
                final String LANGUAGE_PARAM = "language";
                final String LANG_ENGLISH = "en-US";
                final String SORT_PARAM = "sort_by";
                final String POPULAR_SORT = "popularity.desc";
                final String ADULT_PARAM = "include_adult";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIES_API_KEY)
                        .appendQueryParameter(LANGUAGE_PARAM, LANG_ENGLISH)
                        .appendQueryParameter(SORT_PARAM, POPULAR_SORT)
                        .appendQueryParameter(ADULT_PARAM, "false")
//                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
//            if (result != null) {
//                if (moviePosterAdapter != null) {
//                    moviePosterAdapter.clear();
//                }
//                for(String posterURL : result) {
//                    moviePosterAdapter.add(posterURL);
//                }
            moviePosterAdapter = new ImageAdapter(context, result);
            GridView gridview = (GridView) findViewById(R.id.grid_posters);
            gridview.setAdapter(moviePosterAdapter);
            Log.d(this.toString(), "loaded posters");
        }
    }

    private String[] getMovieDataFromJson(String moviesJsonStr)
            throws JSONException {

        final String MOVIE_LIST = "results";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray movieArray = moviesJson.getJSONArray(MOVIE_LIST);

        int numPosters = movieArray.length();
        String[] moviePosters = new String[numPosters];
        for(int i = 0; i < numPosters; i++) {
            JSONObject movie = movieArray.getJSONObject(i);
            moviePosters[i] = POSTERS_BASE_URL + POSTER_SIZE + movie.getString("poster_path");

        }
        return moviePosters;
    }

}
