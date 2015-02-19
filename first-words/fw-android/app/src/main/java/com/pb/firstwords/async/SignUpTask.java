package com.pb.firstwords.async;

import java.util.Map;

/**
 * Created by puneet on 10/22/14.
 */
public class SignUpTask extends PostRequestTask {

    private SignUpResponseHandler delegate;

    public SignUpTask(Map<String, String> postParams, SignUpResponseHandler _delegate) {
        super(postParams);
        this.delegate = _delegate;
    }

    @Override
    protected void onPostExecute(String result) {
        this.delegate.processSignUpResults(result);
    }
}
