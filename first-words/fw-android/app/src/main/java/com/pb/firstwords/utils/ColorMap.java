package com.pb.firstwords.utils;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by puneet on 11/18/14.
 */
public class ColorMap {

    private static final Map<String,Integer> colorMap = new HashMap<String,Integer>();

    static {
        colorMap.put("Red",Color.rgb(255,0,0));
        colorMap.put("Blue",Color.rgb(0,0,255));
        colorMap.put("Green",Color.rgb(0,255,0));
        colorMap.put("Yellow",Color.rgb(255,255,0));
        colorMap.put("Black",Color.rgb(0,0,0));
        colorMap.put("Brown",Color.rgb(165,42,42));
        colorMap.put("Orange",Color.rgb(255,165,0));
        colorMap.put("Pink",Color.rgb(255,192,203));
        colorMap.put("White",Color.rgb(255,255,255));
        colorMap.put("Gray",Color.rgb(128,128,128));
        colorMap.put("Purple",Color.rgb(128,0,128));
        colorMap.put("SkyBlue",Color.rgb(135,206,235));
    }

    public static Integer getColorCode(String color) {
        return colorMap.get(color);
    }
}
