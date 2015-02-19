package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class VoteLessonTask extends PostRequestTask {

    private VoteLessonResponseHandler delegate;

    public VoteLessonTask(Map<String, String> postParams, VoteLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processVoteLessonResults(result);
    }
}
