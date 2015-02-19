package com.pb.firstwords.beans;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by puneet on 11/17/14.
 */
public class FWLesson {

    private String lessonName;

    private String lessonDescription;

    private String lessonType;

    private NavigableMap<String,FWFlashCard> lessonContents = new TreeMap<String,FWFlashCard>();

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getLessonDescription() {
        return lessonDescription;
    }

    public void setLessonDescription(String lessonDescription) {
        this.lessonDescription = lessonDescription;
    }

    public NavigableMap<String, FWFlashCard> getLessonContents() {
        return lessonContents;
    }

    public void setLessonContents(NavigableMap<String, FWFlashCard> lessonContents) {
        this.lessonContents = lessonContents;
    }

    public String getLessonType() {
        return lessonType;
    }

    public void setLessonType(String lessonType) {
        this.lessonType = lessonType;
    }


}
