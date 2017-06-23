package com.codepath.flickster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.codepath.flickster.models.Config;
import com.codepath.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieList extends AppCompatActivity {
    //constants
    //Base URL for API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    public final static String API_KEY_PARAM = "api_key";
    // the API Key TODO move to a secure location
    //tag for logging calls
    public final static String TAG = "MovieList";

    AsyncHttpClient client;

    //the list of currently playing movies
    ArrayList<Movie> movies;

    RecyclerView rvMovies;
    MovieAdapter adapter;

    Config config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        client = new AsyncHttpClient();
        // get the config
        movies = new ArrayList<>();
        //init adapter
        adapter = new MovieAdapter(movies);
        //resolve and create layout manager

        rvMovies =(RecyclerView) findViewById(R.id.rvMovie);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);
        getConfiguration();
        // get the now playing list

    }
// get the list of currently playing movies from the API
    private void getNowPlaying(){
        String url = API_BASE_URL + "/movie/now_playing";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for(int i =0; i<results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        //notify adapter
                        adapter.notifyItemInserted(movies.size()-1);

                    }
                    Log.i(TAG, String.format("loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get data from now playinf endpoint", throwable, true);
            }
        });

    }// get now playing

    private void getConfiguration(){
        String url = API_BASE_URL + "/configuration";
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override

            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("hello");
                //get the image based url
                try {
                    config = new Config(response);
                    Log.i(TAG, String.format("Loaded configuration with image base URL %s and postersize %s",
                           config.getImageBaseUrl(),
                            config.getPosterSize()));
                    adapter.setConfig(config);
                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("failed getting configuration", throwable, true);
            }
        });
    }// get Config

    //hanle errors log and alert
    private void logError(String message, Throwable error, boolean alertUser){
        Log.e(TAG, message, error);
        //alert user to avoid silent error
        if(alertUser){
            // show long toast
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }// log error
}
