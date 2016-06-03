package com.mauk.app.izilist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SyncStatusObserver;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by Mauk on 30/10/2015.
 */
public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.SingleEventHolder> {

    public static final String TAG = EventRecyclerViewAdapter.class.getSimpleName();

    private final List<SingleEvent> events;
    private Context context;

    public EventRecyclerViewAdapter(Context context, List<SingleEvent> events) {
        this.events = events;
        this.context = context;
    }

    @Override
    public SingleEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        SingleEventHolder singleEventHolder = new SingleEventHolder(context, v);

        return singleEventHolder;
    }

    @Override
    public void onBindViewHolder(SingleEventHolder holder, int position) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        holder.title.setText(events.get(position).getTitle());
        holder.hostname.setText(events.get(position).getHostname());
        holder.date.setText(events.get(position).getDate() != null ? df.format(events.get(position).getDate()) : null);
        if (events.get(position).getLogoUri() != null)
            Picasso.with(context).load(Uri.parse(events.get(position).getLogoUri())).transform(new CircleTransform()).into(holder.logoImage);
        if (events.get(position).getImageUri() != null){}
            Picasso.with(context).load(Uri.parse(events.get(position).getImageUri())).into(holder.mainImage);
    }

    @Override
    public int getItemCount() {
        return (events != null ? events.size() : 0);
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<SingleEvent> l) {
        events.addAll(l);
        notifyDataSetChanged();
    }

    public class SingleEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView hostname;
        public TextView title;
        public TextView date;
        public ImageView logoImage;
        public ImageView mainImage;
        private Context context;

        public SingleEventHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            title = (TextView) itemView.findViewById(R.id.title);
            hostname = (TextView) itemView.findViewById(R.id.hostname);
            date = (TextView) itemView.findViewById(R.id.date);
            logoImage = (ImageView) itemView.findViewById(R.id.logo_image);
            mainImage = (ImageView) itemView.findViewById(R.id.main_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("SINGLE_EVENT_OBJECT", events.get(position));
            intent.putExtra("EVENT_OBJECT_POSITION", position);
            ((Activity) context).startActivityForResult(intent, MainActivity.UPDATE_EVENT);
        }
    }
}
