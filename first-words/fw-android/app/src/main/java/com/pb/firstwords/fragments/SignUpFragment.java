package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.async.SignUpResponseHandler;
import com.pb.firstwords.async.SignUpTask;
import com.pb.firstwords.beans.FWResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.CREATE_USER_URL;
import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;

/**
 * Created by puneet on 10/23/14.
 */
public class SignUpFragment extends Fragment implements SignUpResponseHandler, View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.signup_fragment, container, false);

        Button b = (Button) v.findViewById(R.id.signUp);
        b.setOnClickListener(this);

        return v;
    }

    @Override
    public void processSignUpResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Sign up completed! " + jsonResponse);

        Type fwResponseType = new TypeToken<FWResponse>(){}.getType();
        final FWResponse fwResponse = new Gson().fromJson(jsonResponse, fwResponseType);

        Log.d(DEBUG_TAG, "fwResponse = " + fwResponse.getStatus());

        if (fwResponse != null && fwResponse.getStatus() != null) {
            if (fwResponse.getStatus().equals("FAILED")) {
                TextView statusTextView = (TextView) getActivity().findViewById(R.id.signUpStatusMessage);
                if (fwResponse.getError() != null) {
                    if (fwResponse.getError().equals("USER_ALREADY_EXISTS")) {
                        statusTextView.setText("This username is already taken");
                    } else {
                        statusTextView.setText(fwResponse.getError());
                    }
                } else {
                    statusTextView.setText("An unexpected error occurred");
                }
            } else {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SignUpResultFragment fragment = new SignUpResultFragment();
                fragmentTransaction.replace(android.R.id.content, fragment);
                fragmentTransaction.commit();
            }
        } else {
            Log.e(DEBUG_TAG, "Unexpected response! " + jsonResponse);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            Log.d(DEBUG_TAG, "Inflating sign up fragment...");
        } else {
            Log.e(DEBUG_TAG,"No bundle found");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUp:

                Log.d(DEBUG_TAG,"Sign up button clicked ");

                EditText usernameView = (EditText) getView().findViewById(R.id.signup_username);
                CharSequence username = usernameView.getText();
                Log.d(DEBUG_TAG,"Found username = " + username);

                if (TextUtils.isEmpty(username)) {
                    usernameView.setError("Enter username");
                    break;
                } else {
                    usernameView.setError(null);
                }

                EditText passwordView = (EditText) getView().findViewById(R.id.signup_password);
                CharSequence password = passwordView.getText();
                Log.d(DEBUG_TAG,"Found password = " + password);

                if (TextUtils.isEmpty(password)) {
                    passwordView.setError("Enter password");
                    break;
                } else {
                    passwordView.setError(null);
                }


                EditText password2View = (EditText) getView().findViewById(R.id.signup_password2);
                CharSequence password2 = password2View.getText();
                Log.d(DEBUG_TAG, "Found password2 = " + password2);

                if (TextUtils.isEmpty(password2)) {
                    password2View.setError("Enter password");
                    break;
                } else {
                    password2View.setError(null);
                }

                if (!password.toString().equals(password2.toString())) {
                    password2View.setError("Password does not match");
                    break;
                } else {
                    password2View.setError(null);
                }

                Map<String,String> postParams = new HashMap<String, String>();

                postParams.put("username",username.toString());
                postParams.put("password",password.toString());
                postParams.put("password2",password2.toString());

                SignUpTask signUpTask = new SignUpTask(postParams, this);
                signUpTask.execute(new String[]{CREATE_USER_URL});

                break;
            default:
                Log.e(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }

}
