package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class SearchLessonsTask extends PostRequestTask {

    private SearchLessonsResponseHandler delegate;

    public SearchLessonsTask(Map<String, String> postParams, SearchLessonsResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processSearchLessonsResults(result);
    }
}
