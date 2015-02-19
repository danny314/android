package com.pb.firstwords.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pb.firstwords.R;
import com.pb.firstwords.beans.LessonOverview;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for handling my lessons items for sectional list display
 *
 * @author Puneet
 *
 */
public class SearchResultsListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<LessonOverview> searchResultsList = new ArrayList<LessonOverview>();

    public SearchResultsListAdapter(Context context, List<LessonOverview> searchResultsList){
        this.context = context;
        this.searchResultsList.addAll(searchResultsList);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return searchResultsList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = (View) layoutInflater.inflate(R.layout.search_results_list, parent, false);
        }
        TextView lessonName =(TextView) convertView.findViewById(R.id.searchResultLessonNameListItem);
        TextView lessonDesc = (TextView) convertView.findViewById(R.id.searchResultLessonDescListItem);

        LessonOverview lesson = searchResultsList.get(position);
        lessonName.setText(lesson.getLessonName());
        lessonDesc.setText(lesson.getLessonDescription());

        return convertView;
    }
}
