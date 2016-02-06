package com.mauk.app.izilist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        holder.title.setText(events.get(position).getTitle());
        holder.hostname.setText(events.get(position).getHostname());
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
        private Context context;

        public SingleEventHolder(Context context, View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            hostname = (TextView) itemView.findViewById(R.id.hostname);
            this.context = context;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("SINGLE_EVENT_OBJECT", events.get(position));
            context.startActivity(intent);
        }
    }
}
