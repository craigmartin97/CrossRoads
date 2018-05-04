package com.kitkat.crossroads.ExternalClasses;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * Allows the user to scroll on the inside the scroll view.
 */
public class WorkaroundMapFragment extends SupportMapFragment
{

    private OnTouchListener mListener;


    /**
     * @param layoutInflater Instantiates a layout XML file into its corresponding view Objects
     * @param viewGroup      A view used to contain other views, in this case, the view fragment_upload_image
     *                       This value may be null.
     * @param savedInstance  If the fragment is being re-created from a previous saved state, this is the state.
     * @return Returns inflated view
     */

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance)
    {
        View layout = super.onCreateView(layoutInflater, viewGroup, savedInstance);

        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());

        frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ((ViewGroup) layout).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        return layout;
    }

    /**
     * Sets the listener to equal the listener passed in
     *
     * @param listener - check to see if the user has pressed the map
     */
    public void setListener(OnTouchListener listener)
    {
        mListener = listener;
    }

    /**
     * Check to see if the user hsa touched the map
     */
    public interface OnTouchListener
    {
        public abstract void onTouch();
    }

    /**
     * Make the map scrollable whilst its in the scrollview of the page
     */
    public class TouchableWrapper extends FrameLayout
    {

        public TouchableWrapper(Context context)
        {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    mListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    mListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}