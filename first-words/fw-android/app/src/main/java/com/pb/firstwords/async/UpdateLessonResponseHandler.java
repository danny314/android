package com.pb.firstwords.async;

import android.widget.TextView;

/**
 * Created by puneet on 11/17/14.
 */
public interface UpdateLessonResponseHandler {
    public void processUpdateLessonResults(String jsonResponse, TextView letterView);
}
