package com.mc.sensortag.personalhealthassistant.types;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mc.sensortag.personalhealthassistant.R;

/**
 * Custom adapter for handling Recommendation items for sectional list display
 * 
 * @author Sadaf
 *
 */
public class ListBaseAdapter extends BaseAdapter{
	Context context;
	LayoutInflater layoutInflater;
	ArrayList<PhaListItem> phaList = new ArrayList<PhaListItem>();;
	
	public ListBaseAdapter(Context context, List<PhaListItem> phaList){
		this.context = context;
		this.phaList.addAll(phaList);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return phaList.size();
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
            convertView = (View) layoutInflater.inflate(R.layout.pha_recommendations_list, parent, false);            
        }
		TextView text=(TextView)convertView.findViewById(R.id.menuItem);
        Button button=(Button)convertView.findViewById(R.id.calories);
        text.setText(phaList.get(position).getTitle());
        button.setText(phaList.get(position).getValue());
      
        return convertView;
	}
}
