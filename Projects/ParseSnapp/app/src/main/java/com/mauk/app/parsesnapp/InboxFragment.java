package com.mauk.app.parsesnapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mauk on 26/10/2015.
 */
public class InboxFragment extends ListFragment {

    private List<ParseObject> messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((MainActivity) getActivity()).getProgressBar().setVisibility(View.VISIBLE);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ((MainActivity) getActivity()).getProgressBar().setVisibility(View.INVISIBLE);
                if (e == null) {
                    messages = list;
                    String[] usernames = new String[messages.size()];
                    int i = 0;
                    for (ParseObject message : messages) {
                        usernames[i++] = message.getString(ParseConstants.KEY_SENDER_NAME);
                    }
                    if (getListView().getAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(
                                getListView().getContext(),
                                messages);
                        setListAdapter(adapter);
                    } else {
                        ((MessageAdapter) getListView().getAdapter()).refill(messages);
                    }
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = messages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());

        if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
            //image
            Intent intent = new Intent(getActivity(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        } else {
            //video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        // delete messsage
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
        if (ids.size() == 1) {
            message.deleteInBackground();
        } else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove = new ArrayList<>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();
        }
    }
}
