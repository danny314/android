package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class CreateLessonTask extends PostRequestTask {

    private CreateLessonResponseHandler delegate;

    public CreateLessonTask(Map<String, String> postParams, CreateLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processCreateLessonResults(result);
    }
}
