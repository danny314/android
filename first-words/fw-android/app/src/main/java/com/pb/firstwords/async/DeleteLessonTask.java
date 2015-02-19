package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class DeleteLessonTask extends PostRequestTask {

    private DeleteLessonResponseHandler delegate;

    public DeleteLessonTask(Map<String, String> postParams, DeleteLessonResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processDeleteLessonResults(result);
    }
}
