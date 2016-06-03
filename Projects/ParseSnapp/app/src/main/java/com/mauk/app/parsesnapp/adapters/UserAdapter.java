package com.mauk.app.parsesnapp.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mauk.app.parsesnapp.R;
import com.mauk.app.parsesnapp.utils.MD5Utils;
import com.mauk.app.parsesnapp.utils.ParseConstants;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * Created by Mauk on 30/10/2015.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    private static final String TAG = UserAdapter.class.getSimpleName();
    protected Context context;
    protected List<ParseUser> users;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.message_item, users);
        this.context = context;
        this.users = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_item, null);
            holder = new ViewHolder();
            holder.userImage = (ImageView) convertView.findViewById(R.id.userImage);
            holder.checkImage = (ImageView) convertView.findViewById(R.id.checkImage);
            holder.nameLabel = (TextView) convertView.findViewById(R.id.nameLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ParseUser user = users.get(position);
        String email = user.getEmail();
        if (email != null) email = email.toLowerCase();

        if (email == null || email.equals("")) {
            holder.userImage.setImageResource(R.drawable.avatar_empty);
        } else {
            String hash = MD5Utils.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=204&d=404";
            Log.d(TAG, gravatarUrl);
            Picasso.with(context).load(gravatarUrl).placeholder(R.drawable.avatar_empty).into(holder.userImage);
        }

        holder.nameLabel.setText(user.getUsername());

        GridView gridView = (GridView) parent;
        if (gridView.isItemChecked(position)) {
            holder.checkImage.setVisibility(View.VISIBLE);
        } else {
            holder.checkImage.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView userImage;
        ImageView checkImage;
        TextView nameLabel;
    }

    public void refill(List<ParseUser> users) {
        this.users.clear();
        this.users.addAll(users);
        notifyDataSetChanged();
    }
}
