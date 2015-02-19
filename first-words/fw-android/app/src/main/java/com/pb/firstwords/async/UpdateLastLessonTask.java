package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class UpdateLastLessonTask extends PostRequestTask {

    private UpdateLastLessonResponseHandler delegate;

    public UpdateLastLessonTask(Map<String, String> postParams, UpdateLastLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processUpdateLastLessonResults(result);
    }
}
