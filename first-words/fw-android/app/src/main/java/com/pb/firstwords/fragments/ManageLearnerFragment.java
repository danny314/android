package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.adapter.ManageLearnerListAdapter;
import com.pb.firstwords.adapter.SectionedListAdapter;
import com.pb.firstwords.async.ApproveLessonResponseHandler;
import com.pb.firstwords.async.ApproveLessonTask;
import com.pb.firstwords.async.GetLessonsResponseHandler;
import com.pb.firstwords.async.GetLessonsTask;
import com.pb.firstwords.async.GetSingleLessonResponseHandler;
import com.pb.firstwords.async.GetSingleLessonTask;
import com.pb.firstwords.async.SearchLessonsResponseHandler;
import com.pb.firstwords.async.UnapproveLessonResponseHandler;
import com.pb.firstwords.async.UnapproveLessonTask;
import com.pb.firstwords.beans.LessonOverview;
import com.pb.firstwords.utils.ColorMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.APPROVE_LESSON_URL;
import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.GET_SINGLE_LESSON_URL;
import static com.pb.firstwords.utils.FWUtils.GET_USER_LESSONS_URL;
import static com.pb.firstwords.utils.FWUtils.LESSON_OWNERSHIP_SELF;
import static com.pb.firstwords.utils.FWUtils.LESSON_OWNERSHIP_SYSTEM;
import static com.pb.firstwords.utils.FWUtils.UNAPPROVE_LESSON_URL;

/**
 * Created by puneet on 10/23/14.
 */
public class ManageLearnerFragment extends Fragment implements GetSingleLessonResponseHandler,
        GetLessonsResponseHandler, ApproveLessonResponseHandler, UnapproveLessonResponseHandler,
        SearchLessonsResponseHandler, View.OnClickListener {

    private String username;

    private ListView learnerLessonsListView;

    private SectionedListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.manage_learner_fragment, container, false);

        this.learnerLessonsListView = (ListView) v.findViewById(R.id.manageLearnerLessonListView);
        initializeMyLessonsList();

        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("username", this.username);

        GetLessonsTask getLessonsTask = new GetLessonsTask(postParams, this);
        getLessonsTask.execute(new String[] {GET_USER_LESSONS_URL});

        Button b = (Button) v.findViewById(R.id.searchLessonsBtn);
        b.setOnClickListener(this);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.username = args.getString("username");
            Log.d(DEBUG_TAG, "End lesson fragment : Found username = " + this.username);
            Log.d(DEBUG_TAG, "Inflating end lesson fragment...");
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
            case R.id.searchLessonsBtn:
                Log.d(DEBUG_TAG,"Search lessons button clicked ");
                //Hide the keyboard first
                InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                if (getActivity().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }

                Map<String,String> postParams = new HashMap<String, String>();
                postParams.put("username",bundle.getString("username"));

                EditText queryStringEditText = (EditText) getActivity().findViewById(R.id.queryStringTextView);
                bundle.putString("queryString", queryStringEditText.getText().toString());

                SearchLessonsResultsFragment searchLessonsResultsFragment = new SearchLessonsResultsFragment();
                searchLessonsResultsFragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, searchLessonsResultsFragment);
                fragmentTransaction.addToBackStack("manage_learner_fragment");
                fragmentTransaction.commit();

                break;
            case R.id.goHomeBtn:
                Log.d(DEBUG_TAG,"Go home button clicked ");


                LandingFragment fragment = new LandingFragment();
                bundle.putString("username", this.username);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, fragment);
                fragmentTransaction.commit();
                break;
            default:
                Log.w(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }

    private void initializeMyLessonsList() {
        this.adapter = new SectionedListAdapter(getActivity());
        adapter.addSection("Retrieving my lessons...", new ManageLearnerListAdapter(getActivity(), new ArrayList<LessonOverview>()));
        learnerLessonsListView.setAdapter(adapter);
    }

    @Override
    public void processGetLessonsResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"Get user lessons task completed " + jsonResponse);
        Type collectionType = new TypeToken<List<LessonOverview>>(){}.getType();
        final ArrayList<LessonOverview> myLessons = new Gson().fromJson(jsonResponse, collectionType);

        Collections.sort(myLessons, new Comparator<LessonOverview>() {
            @Override
            public int compare(LessonOverview lessonOverview, LessonOverview lessonOverview2) {
                if (lessonOverview.getOwnership().equals(lessonOverview2.getOwnership())) {
                    return lessonOverview.getCreatedDate().compareTo(lessonOverview2.getCreatedDate());
                } else {
                    if (LESSON_OWNERSHIP_SELF.equals(lessonOverview.getOwnership())) {
                        return -1;
                    } else if (LESSON_OWNERSHIP_SYSTEM.equals(lessonOverview.getOwnership())) {
                        return 1;
                    } else {
                        if (LESSON_OWNERSHIP_SELF.equals(lessonOverview2.getOwnership())) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                }
            }
        });

        Log.d(DEBUG_TAG,"My lessons size = " + myLessons.size());
        if (adapter.getCount() == 1) {
            adapter.clear();
        }

        if (!myLessons.isEmpty()) {
            Log.d(DEBUG_TAG, "Adding section ---");
            adapter.addSection("Learning Mode Lessons", new ManageLearnerListAdapter(getActivity(), myLessons ));
            learnerLessonsListView.setAdapter(adapter);

            learnerLessonsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,	long arg3) {
                    //Toast.makeText(getActivity(), "arg1="+ arg1 + "  arg2="+ + arg2 + " arg3=" + arg3,Toast.LENGTH_SHORT).show();
                    String clickedLessonName =  myLessons.get(arg2 - 1).getLessonName();
                    Log.d(DEBUG_TAG,"Clicked lesson name = " + clickedLessonName);

                    //Retrieve clicked lesson contents
                    Map<String,String> postParams = new HashMap<String, String>();
                    postParams.put("username",ManageLearnerFragment.this.username);
                    postParams.put("lessonName",clickedLessonName);

                    GetSingleLessonTask getSingleLessonTask = new GetSingleLessonTask(postParams, ManageLearnerFragment.this);
                    getSingleLessonTask.execute(new String[] {GET_SINGLE_LESSON_URL});

                }
            });



            learnerLessonsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LessonOverview lesson = myLessons.get(i - 1);
                    String longClickedLessonName =  lesson.getLessonName();
                    Log.d(DEBUG_TAG,"Long clicked lesson name = " + longClickedLessonName + "; approved = " + lesson.getApproved());

                    TextView textView = (TextView) view.findViewById(R.id.learnerLessonNameListItem);
                    TextView descTextView = (TextView) view.findViewById(R.id.learnerLessonDescListItem);
                    Map<String,String> postParams = new HashMap<String, String>();
                    postParams.put("username",ManageLearnerFragment.this.username);
                    postParams.put("lessonName",longClickedLessonName);

                    if (lesson.getApproved()) {
                        //Unapprove lesson
                        UnapproveLessonTask unapproveLessonTask = new UnapproveLessonTask(postParams,ManageLearnerFragment.this);
                        unapproveLessonTask.execute(UNAPPROVE_LESSON_URL);
                        textView.setBackgroundColor(Color.TRANSPARENT);
                        descTextView.setBackgroundColor(Color.TRANSPARENT);
                        lesson.setApproved(false);
                        Toast.makeText(getActivity(), longClickedLessonName + " removed from Learning Mode", Toast.LENGTH_SHORT).show();
                    } else {
                        //Approve lesson
                        ApproveLessonTask approveLessonTask = new ApproveLessonTask(postParams,ManageLearnerFragment.this);
                        approveLessonTask.execute(APPROVE_LESSON_URL);
                        textView.setBackgroundColor(ColorMap.getColorCode("SkyBlue"));
                        descTextView.setBackgroundColor(ColorMap.getColorCode("SkyBlue"));
                        lesson.setApproved(true);
                        Toast.makeText(getActivity(), longClickedLessonName + " added to Learning Mode", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
            });

        } else {
            adapter.addSection("No Lessons Found", new ManageLearnerListAdapter(getActivity(), myLessons ));
            learnerLessonsListView.setAdapter(adapter);
        }

    }

    @Override
    public void processGetSingleLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"GetSingleLesson task completed " + jsonResponse);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ReviewLessonFragment fragment = new ReviewLessonFragment();

        Bundle bundle = new Bundle();
        bundle.putString("username", this.username);
        bundle.putString("lessonJson", jsonResponse);
        fragment.setArguments(bundle);

        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.addToBackStack("my_lessons_fragment");
        fragmentTransaction.commit();
    }


    @Override
    public void processApproveLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Approve lesson results = " + jsonResponse);
    }

    @Override
    public void processUnapproveLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Unapprove lesson results = " + jsonResponse);
    }

    @Override
    public void processSearchLessonsResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Search lesson results = " + jsonResponse);

        Bundle bundle = new Bundle();
        bundle.putString("username", this.username);
        bundle.putString("searchResults", jsonResponse);


    }
}
