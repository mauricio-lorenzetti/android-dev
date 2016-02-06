package com.mauk.app.parsesnapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Mauk on 26/10/2015.
 */
public class FriendsFragment extends ListFragment {

    private static final String TAG = FriendsFragment.class.getSimpleName();
    protected ParseRelation<ParseUser> friendsRelation;
    protected ParseUser currentUser;
    protected List<ParseUser> friends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        currentUser = ParseUser.getCurrentUser();
        friendsRelation = currentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ((MainActivity) getActivity()).getProgressBar().setVisibility(View.VISIBLE);

        ParseQuery<ParseUser> query = friendsRelation.getQuery();
        query.addAscendingOrder(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                ((MainActivity) getActivity()).getProgressBar().setVisibility(View.INVISIBLE);
                if (e == null) {
                    friends = list;
                    String[] usernames = new String[friends.size()];
                    int i = 0;
                    for (ParseUser user : friends) {
                        usernames[i++] = user.getUsername();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            getListView().getContext(),
                            android.R.layout.simple_list_item_1,
                            usernames);
                    setListAdapter(adapter);
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getListView().getContext());
                    builder.setMessage(e.getMessage())
                            .setTitle(R.string.error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

}
