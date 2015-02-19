package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class UnapproveLessonTask extends PostRequestTask {

    private UnapproveLessonResponseHandler delegate;

    public UnapproveLessonTask(Map<String, String> postParams, UnapproveLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processUnapproveLessonResults(result);
    }
}
