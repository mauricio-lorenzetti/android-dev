package com.mauk.app.flickrbrowser;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mauk on 19/10/2015.
 */
public class GetFlickrJSONData extends GetRawData {

    private String LOG_TAG = GetFlickrJSONData.class.getSimpleName();
    private List<Photo> photoList;
    private Uri destinationURI;

    public GetFlickrJSONData(String searchCriteria, boolean matchAll) {
        super(null);
        photoList = new ArrayList<Photo>();
        createAndUpdateURI(searchCriteria, matchAll);
    }

    public boolean createAndUpdateURI(String searchCriteria, boolean matchAll) {

        final String FLICKR_API_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAGMODE_PARAM = "tagmode";
        final String FORMAT_PARAM = "format";
        final String NO_JSON_CALLBACK_PARAM = "nojsoncallback";

        destinationURI = Uri.parse(FLICKR_API_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
                .build();

        return (destinationURI != null);
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void execute() {
        //super.setmRawUrl(destinationURI.toString());
        DownloadJSONData downloadJSONData = new DownloadJSONData();
        Log.v(LOG_TAG, "Built URI: " + destinationURI.toString());
        downloadJSONData.execute(destinationURI.toString());
    }

    public class DownloadJSONData extends DownloadRawData {

        @Override
        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();
        }

        @Override
        protected String doInBackground(String... params) {
            String[] par = { destinationURI.toString() };
            return super.doInBackground(par);
        }

        private void processResult() {
            if (getmDownloadStatus() != DownloadStatus.OK){
                Log.e(LOG_TAG, "Error downloading raw file");
                return;
            }

            final String FLICKR_ITEMS = "items";
            final String FLICKR_TITLE = "title";
            final String FLICKR_MEDIA = "media";
            final String FLICKR_PHOTO_URL = "m";
            final String FLICKR_AUTHOR = "author";
            final String FLICKR_AUTHOR_ID = "author_id";
            final String FLICKR_LINK = "link";
            final String FLICKR_TAGS = "tags";

            try {

                JSONObject jsonData = new JSONObject(getmData());
                JSONArray itemsArray = jsonData.getJSONArray(FLICKR_ITEMS);

                for (int i = 0; i < itemsArray.length(); i++) {
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString(FLICKR_TITLE);
                    String author = jsonPhoto.getString(FLICKR_AUTHOR);
                    String author_id = jsonPhoto.getString(FLICKR_AUTHOR_ID);
                    //String link = jsonPhoto.getString(FLICKR_LINK);
                    String tags = jsonPhoto.getString(FLICKR_TAGS);

                    JSONObject jsonMedia = jsonPhoto.getJSONObject(FLICKR_MEDIA);
                    String photoUrl = jsonMedia.getString(FLICKR_PHOTO_URL);
                    String link = photoUrl.replaceFirst("_m", "_b");

                    Photo photoObject = new Photo(title, author, author_id, link, tags, photoUrl);

                    photoList.add(photoObject);
                }

                for (Photo photo : photoList) {
                    Log.v(LOG_TAG, photo.toString());
                    Log.v(LOG_TAG, String.valueOf(photoList.indexOf(photo)));
                }

            } catch (JSONException jsone) {
                jsone.printStackTrace();
                Log.e(LOG_TAG, "Error processing Json data");
            }
        }
    }
}
