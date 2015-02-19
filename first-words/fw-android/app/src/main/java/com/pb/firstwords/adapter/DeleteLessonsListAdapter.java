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
public class DeleteLessonsListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<LessonOverview> ownLessonsList = new ArrayList<LessonOverview>();

    public DeleteLessonsListAdapter(Context context, List<LessonOverview> ownLessonsList){
        this.context = context;
        this.ownLessonsList.addAll(ownLessonsList);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ownLessonsList.size();
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
            convertView = (View) layoutInflater.inflate(R.layout.delete_lessons_list, parent, false);
        }
        TextView lessonName =(TextView) convertView.findViewById(R.id.deleteLessonNameListItem);
        TextView lessonDesc = (TextView) convertView.findViewById(R.id.deleteLessonDescListItem);

        LessonOverview lesson = ownLessonsList.get(position);
        lessonName.setText(lesson.getLessonName());
        lessonDesc.setText(lesson.getLessonDescription());

        return convertView;
    }
}
