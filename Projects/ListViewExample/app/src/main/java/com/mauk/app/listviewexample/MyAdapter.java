package com.mauk.app.listviewexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mauk.app.listviewexample.R;

class MyAdapter extends ArrayAdapter<String> {

    public MyAdapter(Context context, String[] values) {
        super(context, R.layout.row_layout_2, values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater theInflater = LayoutInflater.from(getContext());

        View theView = theInflater.inflate(R.layout.row_layout_2, parent, false);

        String club = getItem(position);

        TextView theTextView = (TextView) theView.findViewById(R.id.textView01);

        theTextView.setText(club);

        ImageView theImageView = (ImageView) theView.findViewById(R.id.imageView01);

        theImageView.setImageResource(R.drawable.mog);

        return theView;

    }
}