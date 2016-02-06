package com.mauk.app.flickrbrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Mauk on 23/10/2015.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

    public static interface OnItemClickListener {

        public void OnItemClick (View view, int position);
        public void OnItemLongClick (View view, int position);

    }

    private OnItemClickListener listener;
    private GestureDetector gestureDetector;

    public RecyclerItemClickListener (Context context, final RecyclerView recyclerView, final OnItemClickListener listener) {
        this.listener = listener;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            public boolean onSingleTapUp (MotionEvent e) {
              return true;
            }

            public void onLongPress (MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (childView != null && listener != null) {
                    listener.OnItemLongClick(childView, recyclerView.getChildPosition(childView));
                }
            }

        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(),e.getY());
        if (childView != null && listener != null && gestureDetector.onTouchEvent(e)) {
            listener.OnItemClick(childView, rv.getChildPosition(childView));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
