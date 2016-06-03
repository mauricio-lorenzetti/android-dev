package com.mauk.app.parsesnapp.adapters;

/**
 * Created by Mauk on 26/10/2015.
 */

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

import com.mauk.app.parsesnapp.R;
import com.mauk.app.parsesnapp.ui.FriendsFragment;
import com.mauk.app.parsesnapp.ui.InboxFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment_inbox corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context context;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment_inbox for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new InboxFragment();
            case 1:
                return new FriendsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Drawable d;
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(" ");

        if (position == 0)
            d = ContextCompat.getDrawable(context, R.drawable.ic_tab_inbox);
        else
            d = ContextCompat.getDrawable(context, R.drawable.ic_tab_friends);

        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
        stringBuilder.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return stringBuilder;
    }

}
