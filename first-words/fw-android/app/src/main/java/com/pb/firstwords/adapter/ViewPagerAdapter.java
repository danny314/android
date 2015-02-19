package com.pb.firstwords.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.pb.firstwords.R;

import java.util.ArrayList;
import java.util.List;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        Log.d(DEBUG_TAG, "getCount called! Returning " + bitmaps.size());
        return bitmaps.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == ((RelativeLayout) o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Log.d(DEBUG_TAG, "Page called for position " + position);


        // Declare Variables
        ImageView wordImage;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,false);

        // Locate the ImageView in viewpager_item.xml
        wordImage = (ImageView) itemView.findViewById(R.id.testImage);
        // Capture position and set to the ImageView
        if (bitmaps.size() > position) {
            wordImage.setImageBitmap(bitmaps.get(position));
        } else {
            Log.e(DEBUG_TAG,"No image for position " + position + " ImageCount = " + bitmaps.size());
            wordImage.setImageResource(R.drawable.ic_launcher);
        }

        // Add viewpager_item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    public void addImageResult(Bitmap bitmap) {
        if (bitmap != null) {
            Log.d(DEBUG_TAG, "Adding bitmap " + bitmap + " to pager");
            this.bitmaps.add(bitmap);
        } else {
            Log.e(DEBUG_TAG,"Bitmap passed to pager is null");
        }
    }

    public Bitmap getImage(int position) {
        return bitmaps.get(position);
    }
}