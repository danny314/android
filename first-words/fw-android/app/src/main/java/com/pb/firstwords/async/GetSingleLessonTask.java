package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class GetSingleLessonTask extends PostRequestTask {

    private GetSingleLessonResponseHandler delegate;

    public GetSingleLessonTask(Map<String, String> postParams, GetSingleLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processGetSingleLessonResults(result);
    }
}
