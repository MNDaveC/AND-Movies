package com.mndavec.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.gson.Gson;
import com.mndavec.movies.model.Movie;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ImageAdapter moviePosterAdapter;

    final String MOVIES_API_URL = "http://api.themoviedb.org/3/movie";
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_INFO = "movie_info";
    public static final String MOVIE_TITLE = "original_title";
    public static final String SORT_BY_RATING = "top_rated";
    public static final String SORT_BY_POPULARITY = "popular";
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner sort_spinner = (Spinner) findViewById(R.id.sort_spinner);
        sort_spinner.setOnItemSelectedListener(this);

        updateMovies();
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
        Spinner sort_spinner = (Spinner) findViewById(R.id.sort_spinner);

        String sortSelection = sort_spinner.getSelectedItem().toString();
        String mostPopular = new String(getString(R.string.item_most_popular));
        String highestRated = new String(getString(R.string.item_highest_rated));

        if (sortSelection.equalsIgnoreCase(mostPopular)) {
            movieTask.execute(SORT_BY_POPULARITY);
        } else {
            movieTask.execute(SORT_BY_RATING);
        }

    }

    @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        updateMovies();
    }

    @Override public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class FetchMovieList extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMovieList.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }

            String sortOption = params[0];
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;

            try {

                final String APIKEY_PARAM = "api_key";
                final String LANGUAGE_PARAM = "language";
                final String LANG_ENGLISH = "en-US";
                final String ADULT_PARAM = "include_adult";

                Uri builtUri = Uri.parse(MOVIES_API_URL).buildUpon()
                        .appendPath(sortOption)
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.MOVIES_API_KEY)
                        .appendQueryParameter(LANGUAGE_PARAM, LANG_ENGLISH)
                        .appendQueryParameter(ADULT_PARAM, "false")
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                try {
                    urlConnection.connect();
                } catch (Exception e) {
                    Log.e(this.toString(), e.toString());
                    return null;
                }

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
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> result) {

            if (result != null) {
                GridView gridview = (GridView) findViewById(R.id.grid_posters);
                moviePosterAdapter = new ImageAdapter(context, result);
                gridview.setAdapter(moviePosterAdapter);
                gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long itemID) {
                        Intent myIntent = new Intent(getApplicationContext(), DetailActivity.class);
                        myIntent.putExtra(MOVIE_INFO, (Movie) view.getTag());
                        startActivity(myIntent);
                    }
                });
                Log.d(this.toString(), "loaded posters");
            } else {
                Toast.makeText(context, "Failed to load updated movie results. Check your internet connection.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private List<Movie> getMovieDataFromJson(String moviesJsonStr)
            throws JSONException {

        WrappedMovieResponse wrappedMovieResponse =  new Gson().fromJson(moviesJsonStr, WrappedMovieResponse.class);

        return wrappedMovieResponse.wrappedMovies;
    }

}
