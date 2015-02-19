package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class ActivateLessonTask extends PostRequestTask {

    private ActivateLessonResponseHandler delegate;

    public ActivateLessonTask(Map<String, String> postParams, ActivateLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processActivateLessonResults(result);
    }
}
