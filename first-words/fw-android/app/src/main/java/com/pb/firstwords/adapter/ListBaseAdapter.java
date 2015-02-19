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
public class ListBaseAdapter extends BaseAdapter{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<LessonOverview> myLessonsList = new ArrayList<LessonOverview>();

    public ListBaseAdapter(Context context, List<LessonOverview> myLessonsList){
        this.context = context;
        this.myLessonsList.addAll(myLessonsList);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myLessonsList.size();
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
            convertView = (View) layoutInflater.inflate(R.layout.my_lessons_list, parent, false);
        }
        TextView lessonName =(TextView) convertView.findViewById(R.id.lessonNameListItem);
        TextView lessonDesc = (TextView) convertView.findViewById(R.id.lessonDescListItem);
        lessonName.setText(myLessonsList.get(position).getLessonName());
        lessonDesc.setText(myLessonsList.get(position).getLessonDescription());

        return convertView;
    }
}
