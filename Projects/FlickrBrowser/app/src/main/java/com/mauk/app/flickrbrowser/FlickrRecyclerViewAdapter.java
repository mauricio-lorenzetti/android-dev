package com.mauk.app.flickrbrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mauk on 20/10/2015.
 */
public class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrImageViewHolder> {

    private List<Photo> photosList;
    private Context context;
    private final String LOG_TAG = this.getClass().getSimpleName();

    public FlickrRecyclerViewAdapter(List<Photo> photosList, Context context) {
        this.photosList = photosList;
        this.context = context;
    }

    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, null);
        FlickrImageViewHolder flickrImageViewHolder = new FlickrImageViewHolder(view);

        return flickrImageViewHolder;
    }

    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {

        Photo photoItem = photosList.get(position);
        Log.d(LOG_TAG, "Processing: " + photoItem.getTitle() + " --> " + position + " --> " + photoItem.getImage());

        Picasso.with(context).load(photoItem.getImage()).error(R.drawable.default_image).placeholder(R.drawable.default_image).into(holder.thumbnail);
        holder.title.setText(photoItem.getTitle());

    }

    @Override
    public int getItemCount() {
        return (photosList != null ? photosList.size() : 0);
    }

    public void loadNewData (List<Photo> newPhotos) {
        photosList = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position) {
        return (photosList != null ? photosList.get(position) : null);
    }
}
