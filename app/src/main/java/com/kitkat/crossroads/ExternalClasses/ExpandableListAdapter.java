package com.kitkat.crossroads.ExternalClasses;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kitkat.crossroads.R;

import java.util.HashMap;
import java.util.List;

/**
 * Creates new Expandable lists for content to be stored in
 * This is used in MyAdverts, MyJobs and their linked fragments and activities.
 * An Expandable List View can store text and other information that is collapsible upon the users request
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter
{
    /**
     * The context of the activity or fragment that the expandable list is being
     * created in
     */
    private Context context;

    /**
     * The contents of the list being passed in
     */
    private List<String> list;

    /**
     * The stored data
     */
    private HashMap<String, List<String>> listHashMap;

    /**
     * Creates a new ExpandableListView that takes a list, Hash Map and the context from where the method was called
     *
     * @param context     - the content from where the class was called
     * @param list        - the content to be added to the list
     * @param listHashMap - Space in the list expandable list
     */
    public ExpandableListAdapter(Context context, List<String> list, HashMap<String, List<String>> listHashMap)
    {
        this.context = context;
        this.list = list;
        this.listHashMap = listHashMap;
    }

    /**
     * Gets the number of groups in the adapter
     *
     * @return - Number in group
     */
    @Override
    public int getGroupCount()
    {
        return list.size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition -  The position of the group for which the children count should be returned
     * @return -  	The children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return listHashMap.get(list.get(groupPosition)).size();
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition - the position of the group
     * @return - the data child for the specified group
     */
    @Override
    public Object getGroup(int groupPosition)
    {
        return list.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition - the position of the group that the child resides in
     * @param childPosition - the position of the child with respect to other children in the group
     * @return
     */
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return listHashMap.get(list.get(groupPosition)).get(childPosition);
    }

    /**
     * @param groupPosition int: the position of the group for which the ID is wanted
     * @return - the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    /**
     * Gets the ID for the given child within the given group.
     * This ID must be unique across all children within the group.
     * The combined ID (see getCombinedChildId(long, long)) must be unique across
     * ALL items (groups and all children).
     *
     * @param groupPosition int: the position of the group that contains the child
     * @param childPosition int: the position of the child within the group for which the ID is wanted
     * @return
     */
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the underlying data.
     *
     * @return boolean: whether or not the same ID always refers to the same object
     */
    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the group--the Views for
     * the group's children will be fetched using getChildView(int, int, boolean, View, ViewGroup).
     *
     * @param groupPosition int: the position of the group for which the View is returned
     * @param isExpanded    boolean: whether the group is expanded or collapsed
     * @param convertView   View: the old view to reuse, if possible. You should check that this view is non-null and of an appropriate type before using.
     *                      If it is not possible to convert this view to display the correct data, this method can create a new view. It is not guaranteed that the convertView will
     *                      have been previously created by getGroupView(int, boolean, View, ViewGroup).
     * @param parent        ViewGroup: the parent that this view will eventually be attached to
     * @return View: the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.job_information_expandable_list_header, null);
        }
        TextView header = (TextView) convertView.findViewById(R.id.listHeader);
        header.setTypeface(null, Typeface.BOLD);
        header.setText(headerTitle);
        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given group.
     *
     * @param groupPosition int: the position of the group that contains the child
     * @param childPosition int: the position of the child (for which the View is returned) within the group
     * @param isLastChild   boolean: Whether the child is the last child within the group
     * @param convertView   View: the old view to reuse, if possible. You should check that this view is non-null and of an appropriate type before using.
     *                      If it is not possible to convert this view to display the correct data, this method can create a new view.
     *                      It is not guaranteed that the convertView will have been previously created by getChildView(int, int, boolean, View, ViewGroup).
     * @param parent        ViewGroup: the parent that this view will eventually be attached to
     * @return View: the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {

        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.job_information_expandable_list, null);
        }

        TextView textListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        textListChild.setText(childText);
        return convertView;

    }

    /**
     * @param groupPosition int: the position of the group that contains the child
     * @param childPosition int: the position of the child within the group
     * @return boolean: whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
