package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pb.firstwords.R;
import com.pb.firstwords.activity.LoginActivity;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;

/**
 * Created by puneet on 10/23/14.
 */
public class SignUpResultFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.signup_result_fragment, container, false);

        Button b = (Button) v.findViewById(R.id.signUpResultLoginBtn);
        b.setOnClickListener(this);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        Bundle args = getArguments();
        if (args  != null) {
            this.userCreationStatus = args.getString("status");
            Log.d(DEBUG_TAG, "Inflating sign up result fragment...");
        } else {
            Log.e(DEBUG_TAG,"No bundle found");
        }
*/
    }

    @Override
    public void onClick(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.signUpResultLoginBtn:
                Log.d(DEBUG_TAG,"Sign up result fragment - Login button clicked ");
                getActivity().finish();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Log.w(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }

}
