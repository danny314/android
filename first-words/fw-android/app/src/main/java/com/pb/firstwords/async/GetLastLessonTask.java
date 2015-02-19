package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class GetLastLessonTask extends PostRequestTask {

    private GetLastLessonResponseHandler delegate;

    public GetLastLessonTask(Map<String, String> postParams, GetLastLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processGetLastLessonResults(result);
    }
}
