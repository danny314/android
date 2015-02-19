package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
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
import com.pb.firstwords.async.AsyncImageResponseHandler;
import com.pb.firstwords.async.DownloadImageTask;
import com.pb.firstwords.async.UpdateLastLessonResponseHandler;
import com.pb.firstwords.async.UpdateLastLessonTask;
import com.pb.firstwords.beans.FWFlashCard;
import com.pb.firstwords.beans.FWLesson;
import com.pb.firstwords.utils.ColorMap;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.LESSON_TYPE_ALPHA;
import static com.pb.firstwords.utils.FWUtils.LESSON_TYPE_COLORS;
import static com.pb.firstwords.utils.FWUtils.LESSON_TYPE_NUMBERS;
import static com.pb.firstwords.utils.FWUtils.UPDATE_USER_LAST_LESSON_URL;

/**
 * Created by puneet on 10/23/14.
 */
public class ReviewLessonFragment extends Fragment implements AsyncImageResponseHandler, UpdateLastLessonResponseHandler, View.OnClickListener {

    private String username;

    private FWLesson lesson;

    private NavigableMap<String,FWFlashCard> flashCardMap;

    private TextView letterView;
    private TextView letterView2;
    private TextView wordView;
    private ImageView imageView;
    private TextView numberView;

    private String currentLetter;

    private TextToSpeech ttobj;

    private Button previousBtn;
    private Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.review_lesson_fragment, container, false);

        this.letterView = (TextView) v.findViewById(R.id.letter);
        this.letterView2 = (TextView) v.findViewById(R.id.letter2);
        this.wordView = (TextView) v.findViewById(R.id.flashCardWord);
        this.imageView = (ImageView) v.findViewById(R.id.flashCardImage);
        this.numberView = (TextView) v.findViewById(R.id.numberText);
        this.previousBtn = (Button) v.findViewById(R.id.previousFlashCardBtn);
        this.nextBtn = (Button) v.findViewById(R.id.nextFlashCardBtn);

        setFlashCardContents();

        Button b = (Button) v.findViewById(R.id.nextFlashCardBtn);
        b.setOnClickListener(this);

        b = (Button) v.findViewById(R.id.previousFlashCardBtn);
        b.setOnClickListener(this);

        ImageView speakerImage = (ImageView) v.findViewById(R.id.imgSpeaker);
        speakerImage.setOnClickListener(this);

        Map<String,String> postParams = new HashMap<String, String>();
        postParams.put("username", this.username);
        postParams.put("lessonName", this.lesson.getLessonName());
        UpdateLastLessonTask updateLastLessonTask = new UpdateLastLessonTask(postParams,this);
        updateLastLessonTask.execute(UPDATE_USER_LAST_LESSON_URL);

        ((TextView) v.findViewById(R.id.lessonNameTxt)).setText(this.lesson.getLessonName());

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
            Log.d(DEBUG_TAG, "Review Lesson fragment : Found username = " + this.username + "; jsonResponse=" + args.get("jsonResponse"));

            String lessonJson = args.getString("lessonJson");

            Gson gson = new Gson();
            Type type = new TypeToken<FWLesson>(){}.getType();

            this.lesson = gson.fromJson(lessonJson, type);

            this.flashCardMap = new TreeMap<String, FWFlashCard>(new Comparator<String>() {
                @Override
                public int compare(String s, String s2) {
                    if (s == null && s2 != null) return -1;
                    if (s != null && s2 == null) return 1;

                    if (LESSON_TYPE_NUMBERS.equals(ReviewLessonFragment.this.lesson.getLessonType())) {
                        Log.d(DEBUG_TAG,"Sorting based on numbers...");
                        return Integer.valueOf(s).compareTo(Integer.valueOf(s2));
                    } else {
                        return s.compareTo(s2);
                    }
                }
            });

            this.flashCardMap.putAll(this.lesson.getLessonContents());
            this.currentLetter = flashCardMap.firstEntry().getKey();

            ttobj=new TextToSpeech(getActivity(),
                    new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            if(status != TextToSpeech.ERROR){
                                ttobj.setLanguage(Locale.US);
                            }
                        }
                    });

            Log.d(DEBUG_TAG, "Inflating review lesson fragment...");
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

                break;
            case R.id.imgSpeaker:
                Log.d(DEBUG_TAG,"Speaker image clicked!");
                String toSpeak = this.wordView.getText().toString();
                Toast.makeText(getActivity(), toSpeak, Toast.LENGTH_SHORT).show();
                ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.nextFlashCardBtn:
                Log.d(DEBUG_TAG,"Next flash card button clicked ");

                this.currentLetter = this.flashCardMap.higherKey(this.currentLetter);

                if (this.currentLetter == null) {
                    //Lesson has ended
                    LessonEndFragment lessonEndFragment = new LessonEndFragment();
                    bundle.putString("lessonName",lesson.getLessonName());
                    lessonEndFragment.setArguments(bundle);
                    fragmentTransaction.replace(android.R.id.content, lessonEndFragment);
                    fragmentTransaction.addToBackStack("review_lesson_fragment");
                    fragmentTransaction.commit();

                } else {
                    setFlashCardContents();
                }
                break;
            case R.id.previousFlashCardBtn:
                Log.d(DEBUG_TAG,"Previous flash card button clicked ");

                this.currentLetter = this.flashCardMap.lowerKey(this.currentLetter);
                setFlashCardContents();
                break;
            default:
                Log.w(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }


    @Override
    public void processUpdateLastLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Update last lesson task completed. json response = " + jsonResponse);
    }

    private void setFlashCardContents() {
        if (this.currentLetter != null) {
            FWFlashCard flashCard = this.flashCardMap.get(this.currentLetter);

            String word = flashCard.getWord();
            word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
            this.wordView.setText(word);

            if (LESSON_TYPE_ALPHA.equals(this.lesson.getLessonType())) {
                letterView.setText(this.currentLetter);
                letterView2.setText(this.currentLetter.toLowerCase());
                DownloadImageTask downloadImageTask = new DownloadImageTask(this.imageView);
                downloadImageTask.setDelegate(this);
                downloadImageTask.execute(flashCard.getImageUrl());
            }
            else if (LESSON_TYPE_COLORS.equals(this.lesson.getLessonType())) {
                this.imageView.setBackgroundColor(ColorMap.getColorCode(this.currentLetter));
            }
            else {
                this.numberView.setText(this.currentLetter);
            }


            if (this.currentLetter.equals(flashCardMap.firstKey())) {
                this.previousBtn.setVisibility(View.GONE);
                this.nextBtn.setGravity(Gravity.CENTER_HORIZONTAL);
            } else {
                this.previousBtn.setVisibility(View.VISIBLE);
            }
        }
    }
}
