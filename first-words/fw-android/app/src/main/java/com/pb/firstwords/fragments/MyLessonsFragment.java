package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.adapter.ListBaseAdapter;
import com.pb.firstwords.adapter.SectionedListAdapter;
import com.pb.firstwords.async.GetApprovedLessonsResponseHandler;
import com.pb.firstwords.async.GetApprovedLessonsTask;
import com.pb.firstwords.async.GetSingleLessonResponseHandler;
import com.pb.firstwords.async.GetSingleLessonTask;
import com.pb.firstwords.beans.LessonOverview;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.GET_APPROVED_LESSONS_URL;
import static com.pb.firstwords.utils.FWUtils.GET_SINGLE_LESSON_URL;
import static com.pb.firstwords.utils.FWUtils.LESSON_OWNERSHIP_SELF;
import static com.pb.firstwords.utils.FWUtils.LESSON_OWNERSHIP_SYSTEM;

/**
 * Created by puneet on 10/23/14.
 */
public class MyLessonsFragment extends Fragment implements GetApprovedLessonsResponseHandler, GetSingleLessonResponseHandler, View.OnClickListener {

    private String username;

    private ListView myLessonsListView;

    private SectionedListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.my_lessons_fragment, container, false);

        this.myLessonsListView = (ListView) v.findViewById(R.id.myLessonsListView);
        initializeMyLessonsList();

        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("username", this.username);
        GetApprovedLessonsTask getApprovedLessonsTask = new GetApprovedLessonsTask(postParams, this);
        getApprovedLessonsTask.execute(new String[]{GET_APPROVED_LESSONS_URL});


        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.username = args.getString("username");
            Log.d(DEBUG_TAG, "My lessons fragment : Found username = " + this.username);
            Log.d(DEBUG_TAG, "Inflating My lessons fragment...");
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
            case R.id.reviewAgainBtn:
                Log.d(DEBUG_TAG,"Review again button clicked ");
                Log.i("BUNDLE", bundle.toString());

                Map<String,String> postParams = new HashMap<String, String>();
                postParams.put("username",bundle.getString("username"));
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
        adapter.addSection("Retrieving my lessons...", new ListBaseAdapter(getActivity(), new ArrayList<LessonOverview>()));
        myLessonsListView.setAdapter(adapter);
    }

    @Override
    public void processGetApprovedLessonsResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"Get approved lessons task completed " + jsonResponse);
        Type collectionType = new TypeToken<List<LessonOverview>>(){}.getType();
        final ArrayList<LessonOverview> myLessons = new Gson().fromJson(jsonResponse, collectionType);

        Collections.sort(myLessons, new Comparator<LessonOverview>() {
            @Override
            public int compare(LessonOverview lessonOverview, LessonOverview lessonOverview2) {
                if (lessonOverview.getOwnership().equals(lessonOverview2)) {
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
            adapter.addSection("My Lessons", new ListBaseAdapter(getActivity(), myLessons ));
            myLessonsListView.setAdapter(adapter);
            myLessonsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,	long arg3) {
                    String clickedLessonName =  myLessons.get(arg2 - 1).getLessonName();
                    Log.d(DEBUG_TAG,"Clicked lesson name = " + clickedLessonName);

                    //Retrieve clicked lesson contents
                    Map<String,String> postParams = new HashMap<String, String>();
                    postParams.put("username",MyLessonsFragment.this.username);
                    postParams.put("lessonName",clickedLessonName);

                    GetSingleLessonTask getSingleLessonTask = new GetSingleLessonTask(postParams, MyLessonsFragment.this);
                    getSingleLessonTask.execute(new String[] {GET_SINGLE_LESSON_URL});

                }
            });
        } else {
            adapter.addSection("No lessons found", new ListBaseAdapter(getActivity(), myLessons ));
            myLessonsListView.setAdapter(adapter);
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


}
