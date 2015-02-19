package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class GetLessonsTask extends PostRequestTask {

    private GetLessonsResponseHandler delegate;

    public GetLessonsTask(Map<String, String> postParams, GetLessonsResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processGetLessonsResults(result);
    }
}
