package com.codepath.flickster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by emilylroth on 6/22/17.
 */

public class Config {
    // BASE URL for images
    String imageBaseUrl;
    //poster size
    String posterSize;
    //backdrop size
    String backdropSize;

    public Config (JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");

        imageBaseUrl = images.getString("secure_base_url");
        // get the poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        //use option at index 3
        posterSize = posterSizeOptions.optString(3, "w342");
        //parse
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");
    }

    //helper method for creating urls
    public String getImageUrl(String size, String path){
        return String.format("%s%s%s", imageBaseUrl, size, path);
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
