package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.activity.MainActivity;
import com.pb.firstwords.async.GetLastLessonResponseHandler;
import com.pb.firstwords.async.GetLastLessonTask;
import com.pb.firstwords.async.GetLessonsResponseHandler;
import com.pb.firstwords.beans.FWFlashCard;
import com.pb.firstwords.beans.FWLesson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.GET_LAST_LESSON_URL;

/**
 * Created by puneet on 10/23/14.
 */
public class LandingFragment extends Fragment implements GetLessonsResponseHandler,GetLastLessonResponseHandler,
         View.OnClickListener {

    private String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.landing_fragment, container, false);

        Button b = (Button) v.findViewById(R.id.reviewLesson);
        b.setOnClickListener(this);

        b = (Button) v.findViewById(R.id.startNewLesson);
        b.setOnClickListener(this);

        Button createLessonBtn = (Button) v.findViewById(R.id.createLesson);
        Button manageLearnerBtn = (Button) v.findViewById(R.id.manageLearnerBtn);
        Button manageLessonsBtn = (Button) v.findViewById(R.id.manageLessonsBtn);

        if (((MainActivity) getActivity()).getLearnerMode()) {
            createLessonBtn.setVisibility(View.GONE);
            manageLearnerBtn.setVisibility(View.GONE);
            manageLessonsBtn.setVisibility(View.GONE);
        } else {
            createLessonBtn.setOnClickListener(this);
            manageLearnerBtn.setOnClickListener(this);
            manageLessonsBtn.setOnClickListener(this);
        }

        return v;
    }

    public void processDownloadImageResult(Bitmap image, ImageView imageView) {
        imageView.setImageBitmap(image);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.username = args.getString("username");
            Log.d(DEBUG_TAG, "Landing fragment : Found username = " + this.username);
            Log.d(DEBUG_TAG, "Inflating view stream fragment...");
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
            case R.id.reviewLesson:
                Log.d(DEBUG_TAG,"Review Lesson button clicked ");
                Log.i("BUNDLE", bundle.toString());

                Map<String,String> postParams = new HashMap<String, String>();
                postParams.put("username",bundle.getString("username"));

                GetLastLessonTask getLastLessonTask = new GetLastLessonTask(postParams, this);
                getLastLessonTask.execute(new String[] {GET_LAST_LESSON_URL});

                break;
            case R.id.startNewLesson:
                Log.d(DEBUG_TAG,"Start new lesson clicked ");
                postParams = new HashMap<String, String>();
                postParams.put("username", bundle.getString("username"));
                MyLessonsFragment myLessonsFragment = new MyLessonsFragment();
                myLessonsFragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, myLessonsFragment);
                fragmentTransaction.addToBackStack("landing_fragment");
                fragmentTransaction.commit();
                break;
            case R.id.createLesson:
                Log.d(DEBUG_TAG,"Create lesson button clicked ");

                StartCreateLessonFragment fragment = new StartCreateLessonFragment();
                fragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, fragment);
                fragmentTransaction.addToBackStack("landing_fragment");
                fragmentTransaction.commit();

                break;
            case R.id.manageLearnerBtn:
                Log.d(DEBUG_TAG,"Manage learner button clicked ");
                ManageLearnerFragment learnerFragment = new ManageLearnerFragment();
                learnerFragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, learnerFragment);
                fragmentTransaction.addToBackStack("landing_fragment");
                fragmentTransaction.commit();
                break;
            case R.id.manageLessonsBtn:
                Log.d(DEBUG_TAG,"Manage lessons button clicked ");
                DeleteLessonsFragment deleteLessonsFragment = new DeleteLessonsFragment();
                deleteLessonsFragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, deleteLessonsFragment);
                fragmentTransaction.addToBackStack("landing_fragment");
                fragmentTransaction.commit();
                break;
            default:
                Log.w(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }

    @Override
    public void processGetLessonsResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"GetLessons task completed " + jsonResponse);
    }

    @Override
    public void processGetLastLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"GetLastLesson task completed " + jsonResponse);

        Gson gson = new Gson();
        Type type = new TypeToken<FWLesson>(){}.getType();
        FWLesson fwLesson = gson.fromJson(jsonResponse, type);

        Log.d(DEBUG_TAG, "Obtained last lesson " + fwLesson);

        if (fwLesson != null && fwLesson.getLessonName() != null
                && fwLesson.getLessonContents() != null && !fwLesson.getLessonContents().isEmpty()) {
            Map<String,FWFlashCard> flashCardMap = fwLesson.getLessonContents();
            Iterator<String> letterIterator = flashCardMap.keySet().iterator();

            while (letterIterator.hasNext()) {
                String letter = letterIterator.next();
                Log.d(DEBUG_TAG,"letter = " + letter + "; " + flashCardMap.get(letter).getImageUrl() + "; " + flashCardMap.get(letter).getWord());
            }


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ReviewLessonFragment fragment = new ReviewLessonFragment();

            Bundle bundle = new Bundle();
            bundle.putString("username", this.username);
            bundle.putString("lessonJson", jsonResponse);
            fragment.setArguments(bundle);

            fragmentTransaction.replace(android.R.id.content, fragment);
            fragmentTransaction.addToBackStack("landing_fragment");
            fragmentTransaction.commit();
        } else {
            Toast.makeText(getActivity(), "Last lesson is not available", Toast.LENGTH_SHORT).show();
        }

    }

}
