package com.mauk.app.flickrbrowser;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mauk on 20/10/2015.
 */
public class FlickrImageViewHolder extends RecyclerView.ViewHolder {

    protected ImageView thumbnail;
    protected TextView title;

    public FlickrImageViewHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.title = (TextView) view.findViewById(R.id.title);
    }
}
