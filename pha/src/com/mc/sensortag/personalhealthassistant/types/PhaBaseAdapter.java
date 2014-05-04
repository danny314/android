package com.mc.sensortag.personalhealthassistant.types;

import java.util.List;

import com.mc.sensortag.personalhealthassistant.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Custom adapter to handle basic list view (activities view) with activity name and calories burned
 * 
 * @author Sadaf
 *
 */
public class PhaBaseAdapter extends BaseAdapter{
	Context context;
	List<PhaListItem> phaList;
	
	public PhaBaseAdapter(Context context, List<PhaListItem> phaList){
		this.context = context;
		this.phaList = phaList;
	}
	
	public List<PhaListItem> getList() {
		return phaList;
	}

	@Override
	public int getCount() {
		return phaList.size();
	}

	@Override
	public Object getItem(int position) {
		return phaList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return phaList.indexOf(getItem(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhaItemView phaItemView = null;
		
		LayoutInflater inflator = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if (convertView == null){
			convertView = inflator.inflate(R.layout.pha_list_item, null);
			
			phaItemView = new PhaItemView();
			phaItemView.title = (TextView) convertView.findViewById(R.id.title);
			phaItemView.value = (TextView) convertView.findViewById(R.id.value);
			
			convertView.setTag(phaItemView);
		} else {
			phaItemView = (PhaItemView) convertView.getTag();
		}
		
		PhaListItem  phaListItem = (PhaListItem) getItem(position);
		
		phaItemView.title.setText(phaListItem.getTitle());
		phaItemView.value.setText(phaListItem.getValue());
		
		return convertView;
	}
	
	private class PhaItemView {
		TextView title;
		TextView value;
	}
}
