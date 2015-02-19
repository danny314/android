package com.pb.firstwords.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.pb.firstwords.R;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles grouping of recommendations by restaurants.
 * Enables sectional display of recommendation per restaurant.
 *
 * @author Puneet
 *
 */
public class SectionedListAdapter extends BaseAdapter {

    public final Map<String,Adapter> sections = new LinkedHashMap<String,Adapter>();
    public final ArrayAdapter<String> headers;
    public final static int SECTION_HEADER = 0;

    public SectionedListAdapter(Context context) {
        headers = new ArrayAdapter<String>(context, R.layout.list_header);
    }

    public void addSection(String section, Adapter adapter) {
        this.headers.add(section);
        this.sections.put(section, adapter);
    }

    public Object getItem(int position) {
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if(position == 0) {
                return section;
            }
            if(position < size) {
                return adapter.getItem(position - 1);
            }

            // otherwise start next section
            position -= size;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getCount() {
        // total together all sections, plus one for each section header
        int total = 0;
        for(Adapter adapter : this.sections.values())
            total += adapter.getCount() + 1;
        return total;
    }

    public int getViewTypeCount() {
        // assume that headers count as one, then total all sections
        int total = 1;
        for(Adapter adapter : this.sections.values())
            total += adapter.getViewTypeCount();
        return total;
    }

    public int getItemViewType(int position) {
        int type = 1;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if(position == 0) {
                return SECTION_HEADER;
            }
            if(position < size) {
                return type + adapter.getItemViewType(position - 1);
            }

            // otherwise move into next section
            position -= size;
            type += adapter.getViewTypeCount();
        }
        return -1;
    }

    public boolean areAllItemsSelectable() {
        return false;
    }

    public boolean isEnabled(int position) {
        return (getItemViewType(position) != SECTION_HEADER);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int sectionNum = 0;
        for(Object section : this.sections.keySet()) {
            Adapter adapter = sections.get(section);
            int size = adapter.getCount() + 1;

            // check if position inside this section
            if(position == 0) {
                //bind header names
                return headers.getView(sectionNum, convertView, parent);
            }
            if(position < size){
                return adapter.getView(position - 1, convertView, parent);
            }

            // otherwise move into next section
            position -= size;
            sectionNum++;
        }
        return null;
    }

    public void clear() {
        headers.clear();
        sections.clear();
    }
}
