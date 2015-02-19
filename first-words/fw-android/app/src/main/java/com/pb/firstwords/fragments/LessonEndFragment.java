package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.async.GetLastLessonResponseHandler;
import com.pb.firstwords.async.GetLastLessonTask;
import com.pb.firstwords.async.VoteLessonResponseHandler;
import com.pb.firstwords.async.VoteLessonTask;
import com.pb.firstwords.beans.FWFlashCard;
import com.pb.firstwords.beans.FWLesson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.GET_LAST_LESSON_URL;
import static com.pb.firstwords.utils.FWUtils.VOTETYPE_DOWN;
import static com.pb.firstwords.utils.FWUtils.VOTETYPE_UP;
import static com.pb.firstwords.utils.FWUtils.VOTE_LESSONS_URL;

/**
 * Created by puneet on 10/23/14.
 */
public class LessonEndFragment extends Fragment implements GetLastLessonResponseHandler,
        VoteLessonResponseHandler, View.OnClickListener {

    private String username;

    private String lessonName;

    private ImageView upVoteImage;

    private ImageView downVoteImage;

    private TextView upVoteText;

    private TextView downVoteText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.lesson_end_fragment, container, false);

        Button b = (Button) v.findViewById(R.id.reviewAgainBtn);
        b.setOnClickListener(this);

        b = (Button) v.findViewById(R.id.goHomeBtn);
        b.setOnClickListener(this);

        this.upVoteImage = (ImageView) v.findViewById(R.id.upVoteImg);
        this.upVoteImage.setOnClickListener(this);

        this.downVoteImage = (ImageView) v.findViewById(R.id.downVoteImg);
        this.downVoteImage .setOnClickListener(this);

        this.upVoteText = (TextView) v.findViewById(R.id.upVoteText);
        this.downVoteText = (TextView) v.findViewById(R.id.downVoteText);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.username = args.getString("username");
            this.lessonName = args.getString("lessonName");
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
            case R.id.reviewAgainBtn:
                Log.d(DEBUG_TAG,"Review again button clicked ");
                Log.i("BUNDLE", bundle.toString());

                Map<String,String> postParams = new HashMap<String, String>();
                postParams.put("username",bundle.getString("username"));

                GetLastLessonTask getLastLessonTask = new GetLastLessonTask(postParams, this);
                getLastLessonTask.execute(new String[] {GET_LAST_LESSON_URL});
                break;
            case R.id.goHomeBtn:
                Log.d(DEBUG_TAG,"Go home button clicked ");

                LandingFragment fragment = new LandingFragment();
                bundle.putString("username", this.username);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, fragment);
                fragmentTransaction.addToBackStack("lesson_end_fragment");
                fragmentTransaction.commit();
                break;
            case R.id.upVoteImg:
                Log.d(DEBUG_TAG,"Up vote image clicked ");

                postParams = new HashMap<String, String>();
                postParams.put("username",bundle.getString("username"));
                postParams.put("lessonName",this.lessonName);
                postParams.put("voteType",VOTETYPE_UP);

                VoteLessonTask upVoteLessonTask = new VoteLessonTask(postParams, this);
                upVoteLessonTask.execute(new String[]{VOTE_LESSONS_URL});
                hideVotingControls();
                Toast.makeText(getActivity(), "Lesson voted up!", Toast.LENGTH_SHORT).show();

                break;
            case R.id.downVoteImg:
                Log.d(DEBUG_TAG,"Up vote image clicked ");

                postParams = new HashMap<String, String>();
                postParams.put("username",bundle.getString("username"));
                postParams.put("lessonName",this.lessonName);
                postParams.put("voteType",VOTETYPE_DOWN);

                VoteLessonTask downVoteLessonTask = new VoteLessonTask(postParams, this);
                downVoteLessonTask.execute(new String[]{VOTE_LESSONS_URL});
                hideVotingControls();
                Toast.makeText(getActivity(), "Lesson voted down!", Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.w(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }

    private void hideVotingControls() {
        this.upVoteImage.setVisibility(View.GONE);
        this.upVoteText.setVisibility(View.GONE);

        this.downVoteImage.setVisibility(View.GONE);
        this.downVoteText.setVisibility(View.GONE);
    }

    @Override
    public void processGetLastLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"GetLastLesson task completed " + jsonResponse);

        Gson gson = new Gson();
        Type type = new TypeToken<FWLesson>(){}.getType();
        FWLesson fwLesson = gson.fromJson(jsonResponse, type);

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
        //fragmentTransaction.addToBackStack("landing_fragment");
        fragmentTransaction.commit();
    }


    @Override
    public void processVoteLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "VoteLesson task completed. jsonResponse = " + jsonResponse);
    }
}
