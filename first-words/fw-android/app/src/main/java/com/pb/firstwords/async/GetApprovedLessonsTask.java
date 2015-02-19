package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class GetApprovedLessonsTask extends PostRequestTask {

    private GetApprovedLessonsResponseHandler delegate;

    public GetApprovedLessonsTask(Map<String, String> postParams, GetApprovedLessonsResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processGetApprovedLessonsResults(result);
    }
}
