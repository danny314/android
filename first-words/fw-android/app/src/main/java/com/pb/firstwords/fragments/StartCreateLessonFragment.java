package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.async.CreateLessonResponseHandler;
import com.pb.firstwords.async.CreateLessonTask;
import com.pb.firstwords.beans.FWResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.CREATE_LESSON_URL;
import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;

/**
 * Created by puneet on 10/23/14.
 */
public class StartCreateLessonFragment extends Fragment implements CreateLessonResponseHandler,  View.OnClickListener {

    private String username;

    private String lessonName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.start_create_lesson_fragment, container, false);

        Button b = (Button) v.findViewById(R.id.createLessonNextBtn);
        b.setOnClickListener(this);

        v.findViewById(R.id.lessonNameTextView).requestFocus();

        EditText lessonDescEditText = (EditText) v.findViewById(R.id.lessonDescTextView);

        lessonDescEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE) {
                    createLesson();
                    return true;
                }
                return false;
            }
        });


        return v;
    }

    private boolean validateLessonName() {
        TextView textView = (TextView) getActivity().findViewById(R.id.lessonNameTextView);
        if (textView.getText() == null || TextUtils.getTrimmedLength(textView.getText()) == 0) {
            textView.setError("Lesson name is required");
            return false;
        } else {
            textView.setError(null);
            this.lessonName = textView.getText().toString().trim();
            return true;
        }

    }

    private void createLesson() {

        TextView textView = (TextView) getActivity().findViewById(R.id.lessonDescTextView);
        String lessonDesc = textView.getText().toString();

        CheckBox sharedChk = (CheckBox) getActivity().findViewById(R.id.chkSharedLesson);

        Map<String,String> postParams = new HashMap<String, String>();
        postParams.put("lesson","{\"lessonName\":\"" + this.lessonName + "\",\"lessonDescription\":\"" + lessonDesc + "\",\"lessonContents\":{},\"shared\":\""+ sharedChk.isChecked() +"\" }");

        postParams.put("username",username);

        CreateLessonTask createLessonTask = new CreateLessonTask(postParams,this);
        createLessonTask.execute(new String[] {CREATE_LESSON_URL});

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.username = args.getString("username");
            Log.d(DEBUG_TAG, "Start Create lesson fragment : Found username = " + this.username);
            Log.d(DEBUG_TAG, "Inflating start create lesson fragment...");
        } else {
            Log.e(DEBUG_TAG,"No bundle found");
        }
    }

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", this.username);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()) {
            case R.id.createLessonNextBtn:
                Log.d(DEBUG_TAG,"Start create lesson next button clicked ");
                Log.i("BUNDLE", bundle.toString());
                if (!validateLessonName()) {
                    break;
                }
                createLesson();
                break;
            default:
                Log.w(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }

    @Override
    public void processCreateLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"Create lesson task completed! " + jsonResponse);

        Type fwResponseType = new TypeToken<FWResponse>(){}.getType();
        final FWResponse fwResponse = new Gson().fromJson(jsonResponse, fwResponseType);

        Log.d(DEBUG_TAG, "fwResponse = " + fwResponse.getStatus());

        if (fwResponse != null && fwResponse.getStatus() != null) {
            if (fwResponse.getStatus().equals("FAILED")) {
                EditText lessonNameEditText = (EditText) getActivity().findViewById(R.id.lessonNameTextView);
                if (fwResponse.getError() != null) {
                    if (fwResponse.getError().equals("LESSON_ALREADY_EXISTS")) {
                        lessonNameEditText.setError("This lesson already exists");
                    } else {
                        lessonNameEditText.setError(fwResponse.getError());
                    }
                } else {
                    lessonNameEditText.setError("An unexpected error occurred");
                }
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("username", this.username);
                bundle.putString("lessonName", this.lessonName);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                CreateLessonFragment fragment = new CreateLessonFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, fragment);
                fragmentTransaction.addToBackStack("landing_fragment");
                fragmentTransaction.commit();
            }
        } else {
            Log.e(DEBUG_TAG, "Unexpected response! " + jsonResponse);
        }
    }

}
