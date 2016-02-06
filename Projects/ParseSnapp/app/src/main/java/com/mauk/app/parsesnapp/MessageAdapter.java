package com.mauk.app.parsesnapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by Mauk on 30/10/2015.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject> {

    protected Context context;
    protected List<ParseObject> messages;

    public MessageAdapter (Context context, List<ParseObject> messages) {
        super(context, R.layout.message_item, messages);
        this.context = context;
        this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.message_item, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.messageIcon);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.senderLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseObject message = messages.get(position);

        if (message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE)) {
            holder.iconImageView.setImageResource(android.R.drawable.ic_menu_gallery);
        } else {
            holder.iconImageView.setImageResource(android.R.drawable.ic_media_play);
        }
        holder.nameLabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
    }

    public void refill(List<ParseObject> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }
}
