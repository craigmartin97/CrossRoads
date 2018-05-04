package com.kitkat.crossroads.ExternalClasses;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * ListViewHeight, changes the height of each expandable list view
 * to match the height of the content
 */
public class ListViewHeight
{
    /**
     * @param listView - the ExpandableListView that is height is being changed
     * @param group    - the position of each element in the group ExpandableListView
     */
    public void setListViewHeight(ExpandableListView listView, int group)
    {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++)
        {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group)))
            {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++)
                {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight() + 5;
                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    /**
     * Allows the Expandable list views to be used in other classes.
     * Calls method setListViewHeight so the height of each adapter wraps the content
     *
     * @param parent        - the ExpandableListView that is height is being changed
     * @param groupPosition - the position of each element in the group ExpandableListView
     */
    public void setExpandableListViewHeight(ExpandableListView parent, int groupPosition)
    {
        setListViewHeight(parent, groupPosition);
    }
}
