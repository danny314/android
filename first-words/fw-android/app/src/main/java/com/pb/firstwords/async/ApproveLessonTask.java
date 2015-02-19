package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class ApproveLessonTask extends PostRequestTask {

    private ApproveLessonResponseHandler delegate;

    public ApproveLessonTask(Map<String, String> postParams, ApproveLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processApproveLessonResults(result);
    }
}
