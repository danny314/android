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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.adapter.DeleteLessonsListAdapter;
import com.pb.firstwords.adapter.SectionedListAdapter;
import com.pb.firstwords.async.DeleteLessonResponseHandler;
import com.pb.firstwords.async.DeleteLessonTask;
import com.pb.firstwords.async.GetLessonsResponseHandler;
import com.pb.firstwords.async.GetLessonsTask;
import com.pb.firstwords.async.GetSingleLessonResponseHandler;
import com.pb.firstwords.async.GetSingleLessonTask;
import com.pb.firstwords.beans.LessonOverview;
import com.pb.firstwords.utils.ColorMap;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.DELETE_LESSON_URL;
import static com.pb.firstwords.utils.FWUtils.GET_OWN_LESSONS_URL;
import static com.pb.firstwords.utils.FWUtils.GET_SINGLE_LESSON_URL;

/**
 * Created by puneet on 10/23/14.
 */
public class DeleteLessonsFragment extends Fragment implements GetSingleLessonResponseHandler,
        GetLessonsResponseHandler, DeleteLessonResponseHandler, View.OnClickListener {

    private String username;

    private ListView deleteLessonsListView;

    private SectionedListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.delete_lessons_fragment, container, false);

        this.deleteLessonsListView = (ListView) v.findViewById(R.id.deleteLessonListView);
        initializeMyLessonsList();

        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("username", this.username);

        GetLessonsTask getLessonsTask = new GetLessonsTask(postParams, this);
        getLessonsTask.execute(new String[] {GET_OWN_LESSONS_URL});


        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.username = args.getString("username");
            //this.lessonName = args.getString("lessonName");
            Log.d(DEBUG_TAG, "Delete lesson fragment : Found username = " + this.username);
            Log.d(DEBUG_TAG, "Inflating Delete lesson fragment...");
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
        adapter.addSection("Retrieving my lessons...", new DeleteLessonsListAdapter(getActivity(), new ArrayList<LessonOverview>()));
        deleteLessonsListView.setAdapter(adapter);
    }

    @Override
    public void processGetLessonsResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"Get user lessons task completed " + jsonResponse);
        Type collectionType = new TypeToken<List<LessonOverview>>(){}.getType();
        final ArrayList<LessonOverview> myLessons = new Gson().fromJson(jsonResponse, collectionType);

        Collections.sort(myLessons, new Comparator<LessonOverview>() {
            @Override
            public int compare(LessonOverview lessonOverview, LessonOverview lessonOverview2) {
                return lessonOverview.getCreatedDate().compareTo(lessonOverview2.getCreatedDate());
            }
        });

        Log.d(DEBUG_TAG,"My lessons size = " + myLessons.size());
        if (adapter.getCount() == 1) {
            adapter.clear();
        }

        if (!myLessons.isEmpty()) {
            Log.d(DEBUG_TAG, "Adding section ---");
            adapter.addSection("My Lessons", new DeleteLessonsListAdapter(getActivity(), myLessons ));
            deleteLessonsListView.setAdapter(adapter);

            deleteLessonsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,	long arg3) {
                    //Toast.makeText(getActivity(), "arg1="+ arg1 + "  arg2="+ + arg2 + " arg3=" + arg3,Toast.LENGTH_SHORT).show();
                    String clickedLessonName =  myLessons.get(arg2 - 1).getLessonName();
                    Log.d(DEBUG_TAG,"Clicked lesson name = " + clickedLessonName);

                    //Retrieve clicked lesson contents
                    Map<String,String> postParams = new HashMap<String, String>();
                    postParams.put("username",DeleteLessonsFragment.this.username);
                    postParams.put("lessonName",clickedLessonName);

                    GetSingleLessonTask getSingleLessonTask = new GetSingleLessonTask(postParams, DeleteLessonsFragment.this);
                    getSingleLessonTask.execute(new String[] {GET_SINGLE_LESSON_URL});

                }
            });



            deleteLessonsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LessonOverview lesson = myLessons.get(i - 1);
                    String longClickedLessonName =  lesson.getLessonName();
                    Log.d(DEBUG_TAG,"Long clicked lesson name = " + longClickedLessonName + "; approved = " + lesson.getApproved());

                    TextView textView = (TextView) view.findViewById(R.id.deleteLessonNameListItem);
                    TextView descTextView = (TextView) view.findViewById(R.id.deleteLessonDescListItem);
                    Map<String,String> postParams = new HashMap<String, String>();
                    postParams.put("username",DeleteLessonsFragment.this.username);
                    postParams.put("lessonName",longClickedLessonName);

                    //Delete lesson
                    DeleteLessonTask deleteLessonTask = new DeleteLessonTask(postParams,DeleteLessonsFragment.this);
                    deleteLessonTask.execute(DELETE_LESSON_URL);
                    textView.setBackgroundColor(ColorMap.getColorCode("Red"));
                    descTextView.setBackgroundColor(ColorMap.getColorCode("Red"));
                    Toast.makeText(getActivity(), longClickedLessonName + " deleted", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        } else {
            adapter.addSection("No Lessons Found", new DeleteLessonsListAdapter(getActivity(), myLessons ));
            deleteLessonsListView.setAdapter(adapter);
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
    public void processDeleteLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Delete lesson results = " + jsonResponse);
    }

}
