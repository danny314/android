package com.pb.firstwords.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pb.firstwords.R;
import com.pb.firstwords.beans.LessonOverview;
import com.pb.firstwords.utils.ColorMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom adapter for handling my lessons items for sectional list display
 *
 * @author Puneet
 *
 */
public class ManageLearnerListAdapter extends BaseAdapter{
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<LessonOverview> manageLearnerLessonsList = new ArrayList<LessonOverview>();

    public ManageLearnerListAdapter(Context context, List<LessonOverview> myLessonsList){
        this.context = context;
        this.manageLearnerLessonsList.addAll(myLessonsList);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return manageLearnerLessonsList.size();
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
            convertView = (View) layoutInflater.inflate(R.layout.manage_learner_list, parent, false);
        }
        TextView lessonName =(TextView) convertView.findViewById(R.id.learnerLessonNameListItem);
        TextView lessonDesc = (TextView) convertView.findViewById(R.id.learnerLessonDescListItem);

        LessonOverview lesson = manageLearnerLessonsList.get(position);
        lessonName.setText(lesson.getLessonName());
        lessonDesc.setText(lesson.getLessonDescription());

        if (lesson.getApproved()) {
            lessonName.setBackgroundColor(ColorMap.getColorCode("SkyBlue"));
            lessonDesc.setBackgroundColor(ColorMap.getColorCode("SkyBlue"));
        } else {
            lessonName.setBackgroundColor(Color.TRANSPARENT);
            lessonDesc.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }
}
