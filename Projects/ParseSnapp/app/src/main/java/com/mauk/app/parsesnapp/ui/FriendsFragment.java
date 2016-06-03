package com.mauk.app.parsesnapp.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.mauk.app.parsesnapp.adapters.UserAdapter;
import com.mauk.app.parsesnapp.utils.ParseConstants;
import com.mauk.app.parsesnapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Mauk on 26/10/2015.
 */
public class FriendsFragment extends Fragment {

    private static final String TAG = FriendsFragment.class.getSimpleName();
    protected ParseRelation<ParseUser> friendsRelation;
    protected ParseUser currentUser;
    protected List<ParseUser> friends;
    protected GridView gridView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);

        gridView = (GridView) rootView.findViewById(R.id.friendsGrid);

        TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
        gridView.setEmptyView(emptyTextView);

        rootView.findViewById(R.id.tool_bar).setVisibility(View.GONE);

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
                    if (gridView.getAdapter() == null) {
                        UserAdapter adapter = new UserAdapter(getActivity(), friends);
                        gridView.setAdapter(adapter);
                    } else {
                        ((UserAdapter)gridView.getAdapter()).refill(friends);
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
