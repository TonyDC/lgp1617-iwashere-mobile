package com.teamc.mira.iwashere.presentation.searchList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamc.mira.iwashere.R;
import com.teamc.mira.iwashere.domain.model.PoiModel;
import com.teamc.mira.iwashere.domain.model.RouteModel;
import com.teamc.mira.iwashere.domain.model.TagModel;
import com.teamc.mira.iwashere.presentation.poi.PoiDetailActivity;
import com.teamc.mira.iwashere.presentation.route.RouteDetailActivity;

import java.util.ArrayList;

public class SearchExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;

    public SearchExpandableListAdapter(Context mContext
            , ArrayList<ParentRow> originalList) {
        this.mContext = mContext;
        this.parentRowList = new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }

    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return parentRowList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentRowList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentRowList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentRow parentRow = (ParentRow) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_row, null);
        }

        TextView heading = (TextView) convertView.findViewById(R.id.parent_text);

        heading.setText(parentRow.getName().trim());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildRow childRow = (ChildRow) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_row, null);
        }

        ImageView childIcon = (ImageView) convertView.findViewById(R.id.child_icon);
        childIcon.setImageResource(childRow.getIcon());

        final TextView childText = (TextView) convertView.findViewById(R.id.child_text);
        childText.setText(childRow.getBasicModel().getName().trim());

        final View finalConvertView = convertView;
        childText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (childRow.getBasicModel() instanceof PoiModel) {
                    Intent intent = new Intent(mContext, PoiDetailActivity.class);
                    intent.putExtra("poi", (PoiModel) childRow.getBasicModel());
                    mContext.startActivity(intent);
                } else if (childRow.getBasicModel() instanceof RouteModel) {
                    Intent intent = new Intent(mContext, RouteDetailActivity.class);
                    intent.putExtra(RouteDetailActivity.EXTRA_ROUTE, (RouteModel) childRow.getBasicModel());
                    mContext.startActivity(intent);

                } else if (childRow.getBasicModel() instanceof TagModel) {
                    //TODO 01.05.2017 Start Tag Activity
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}