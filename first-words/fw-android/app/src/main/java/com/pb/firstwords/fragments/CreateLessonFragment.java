package com.pb.firstwords.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.pb.firstwords.R;
import com.pb.firstwords.activity.MainActivity;
import com.pb.firstwords.adapter.ViewPagerAdapter;
import com.pb.firstwords.async.ActivateLessonResponseHandler;
import com.pb.firstwords.async.ActivateLessonTask;
import com.pb.firstwords.async.AsyncImageResponseHandler;
import com.pb.firstwords.async.CreateLessonResponseHandler;
import com.pb.firstwords.async.DownloadImageTask;
import com.pb.firstwords.async.GetImageResponseHandler;
import com.pb.firstwords.async.GetImageTask;
import com.pb.firstwords.async.UpdateLessonResponseHandler;
import com.pb.firstwords.async.UpdateLessonTask;
import com.pb.firstwords.beans.FWResponse;
import com.pb.firstwords.utils.ColorMap;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.pb.firstwords.utils.FWUtils.ACTIVATE_LESSON_URL;
import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.UPDATE_LESSON_URL;

/**
 * Created by puneet on 10/23/14.
 */
public class CreateLessonFragment extends Fragment implements CreateLessonResponseHandler, GetImageResponseHandler,
        AsyncImageResponseHandler, UpdateLessonResponseHandler, ActivateLessonResponseHandler, View.OnClickListener {

    private String username;

    // Declare Variables
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private String currentLetterStr;
    private String currentWord;

    private String lessonName;

    private View mProgressView;

    private EditText wordEditText;

    private TextView currentLetterTextView;

    private ImageView useThisImagePic;
    private ImageView useCameraPic;
    private ImageView uploadImagePic;

    private static final int SELECT_PICTURE = 0;

    private Boolean isEmptyLesson = Boolean.TRUE;

    private Boolean imageUploadedForLetter = Boolean.FALSE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(DEBUG_TAG,"OncreateView called for create lesson fragment");

        View v = inflater.inflate(R.layout.create_lesson_fragment, container, false);

        Button b = (Button) v.findViewById(R.id.newLessonDoneBtn);
        b.setOnClickListener(this);

        b = (Button) v.findViewById(R.id.getImageBtn);
        b.setOnClickListener(this);

        TextView lessonTitleText = (TextView) v.findViewById(R.id.createLessonTitle);
        lessonTitleText.setText(this.lessonName);

        TextView letter = (TextView) v.findViewById(R.id.createLessonA);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonB);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonC);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonD);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonE);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonF);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonG);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonH);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonI);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonJ);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonK);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonL);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonM);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonN);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonO);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonP);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonQ);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonR);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonS);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonT);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonU);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonV);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonW);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonX);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonY);
        letter.setOnClickListener(this);

        letter = (TextView) v.findViewById(R.id.createLessonZ);
        letter.setOnClickListener(this);

        this.useThisImagePic = (ImageView) v.findViewById(R.id.useThisImage);
        this.useThisImagePic.getDrawable().setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);

        this.useCameraPic = (ImageView) v.findViewById(R.id.useCameraPic);
        this.useCameraPic.getDrawable().setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);

        this.uploadImagePic = (ImageView) v.findViewById(R.id.uploadImagePic);
        this.uploadImagePic.getDrawable().setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);

        viewPager = (ViewPager) v.findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(getActivity());
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

        mProgressView = v.findViewById(R.id.upload_progress);

        this.wordEditText = (EditText) v.findViewById(R.id.word);
        this.wordEditText.setOnClickListener(this);

        this.currentLetterTextView = (TextView) v.findViewById(R.id.wordStartingFrom);

        MainActivity mainActivity = (MainActivity) getActivity();

        if (!mainActivity.getAddedLetters().isEmpty()) {
            Log.d(DEBUG_TAG, "saved letter list is not empty");
            this.isEmptyLesson = false;
            for (String savedLetter : mainActivity.getAddedLetters()) {
                Log.d(DEBUG_TAG, "Highlighting " + letter);
                TextView clickedLetterTextView  = (TextView) v.findViewById(getResources().getIdentifier("createLesson" + savedLetter, "id", getActivity().getPackageName()));
                clickedLetterTextView.setBackgroundColor(ColorMap.getColorCode("SkyBlue"));
                clickedLetterTextView.setTextColor(ColorMap.getColorCode("Blue"));
                clickedLetterTextView.setTypeface(null, Typeface.BOLD);
            }
        } else {
            Log.e(DEBUG_TAG, "saved letter list is empty");
        }

        setCustomImage();

        return v;
    }

    private void setCustomImage() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.getCameraBitmap() != null) {
            adapter.addImageResult(mainActivity.getCameraBitmap());
            adapter.notifyDataSetChanged();
            this.useThisImagePic.getDrawable().clearColorFilter();
            this.useThisImagePic.setOnClickListener(this);

            this.useCameraPic.getDrawable().clearColorFilter();
            this.useCameraPic.setOnClickListener(this);

            this.uploadImagePic.getDrawable().clearColorFilter();
            this.uploadImagePic.setOnClickListener(this);
        } else {
            Log.w(DEBUG_TAG, "Camera bitmap is null");
        }
    }

    public void showProgress(final boolean show) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
    }


    public void processDownloadImageResult(Bitmap image, ImageView imageView) {
        Log.d(DEBUG_TAG, "Download image complete...");
        adapter.addImageResult(image);
        adapter.notifyDataSetChanged();

        this.useThisImagePic.getDrawable().clearColorFilter();
        this.useThisImagePic.setOnClickListener(this);


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.username = args.getString("username");
            this.lessonName = args.getString("lessonName");
            Log.d(DEBUG_TAG, "Create lesson fragment : Found username = " + this.username);
            Log.d(DEBUG_TAG, "Inflating create lesson fragment...");
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
            case R.id.word:
                Log.d(DEBUG_TAG,"Word clicked ");
                this.wordEditText.setError(null);

                this.uploadImagePic.getDrawable().clearColorFilter();
                this.uploadImagePic.setOnClickListener(this);

                this.useCameraPic.getDrawable().clearColorFilter();
                this.useCameraPic.setOnClickListener(this);

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(this.wordEditText, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.newLessonDoneBtn:

                if (this.isEmptyLesson) {
                    Log.w(DEBUG_TAG, "No lesson contents");
                    this.wordEditText.setError("Add lesson content!");
                    break;
                }

                if (!this.imageUploadedForLetter) {
                    Toast.makeText(getActivity(), "Don't forget to upload the image!", Toast.LENGTH_SHORT).show();
                    break;
                }

                Log.d(DEBUG_TAG,"Done New Lesson button clicked ");
                Log.i("BUNDLE", bundle.toString());
                Map<String,String> postParams = new HashMap<String, String>();
                postParams.put("lessonName",this.lessonName);
                postParams.put("username",username);
                ActivateLessonTask activateLessonTask = new ActivateLessonTask(postParams,this);
                activateLessonTask.execute(new String[] {ACTIVATE_LESSON_URL});
                ((MainActivity) getActivity()).getAddedLetters().clear();
                break;
            case R.id.createLessonA:
            case R.id.createLessonB:
            case R.id.createLessonC:
            case R.id.createLessonD:
            case R.id.createLessonE:
            case R.id.createLessonF:
            case R.id.createLessonG:
            case R.id.createLessonH:
            case R.id.createLessonI:
            case R.id.createLessonJ:
            case R.id.createLessonK:
            case R.id.createLessonL:
            case R.id.createLessonM:
            case R.id.createLessonN:
            case R.id.createLessonO:
            case R.id.createLessonP:
            case R.id.createLessonQ:
            case R.id.createLessonR:
            case R.id.createLessonS:
            case R.id.createLessonT:
            case R.id.createLessonU:
            case R.id.createLessonV:
            case R.id.createLessonW:
            case R.id.createLessonX:
            case R.id.createLessonY:
            case R.id.createLessonZ:
                TextView currentLetterTextView = ((TextView) view);
                Log.d(DEBUG_TAG,"Letter clicked " + currentLetterTextView.getText());

                if (!this.isEmptyLesson && !this.imageUploadedForLetter && this.currentWord != null) {
                    Toast.makeText(getActivity(), "Don't forget to upload the image!", Toast.LENGTH_SHORT).show();
                    break;
                }
                this.currentLetterStr =  currentLetterTextView.getText().toString();
                this.currentLetterTextView.setText(this.currentLetterStr);
                this.wordEditText.setHint("Type a word starting from " + this.currentLetterStr);
                this.imageUploadedForLetter = false;
                this.isEmptyLesson = false;
                this.wordEditText.requestFocus();
                this.wordEditText.callOnClick();
                break;
            case R.id.useThisImage:
                Log.d(DEBUG_TAG,"Use this image clicked ");

                this.useThisImagePic.getDrawable().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
                this.useThisImagePic.setOnClickListener(null);

                this.useCameraPic.getDrawable().setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);
                this.useCameraPic.setOnClickListener(null);

                this.uploadImagePic.getDrawable().setColorFilter(Color.TRANSPARENT,PorterDuff.Mode.SRC_IN);
                this.uploadImagePic.setOnClickListener(null);

                showProgress(true);
                ImageView currentImageView = (ImageView) getActivity().findViewById(R.id.testImage);
                Log.d(DEBUG_TAG,"Image view = " + currentImageView);

                Bitmap currentImage = ((ViewPagerAdapter) viewPager.getAdapter()).getImage(viewPager.getCurrentItem());
                currentImageView.setImageBitmap(currentImage);
                Log.d(DEBUG_TAG,"currentImage = " + currentImage);

                //Upload the new letter and image to the server...
                postParams = new HashMap<String, String>();
                postParams.put("lessonName",this.lessonName);
                postParams.put("username",username);
                postParams.put("currentLetter",this.currentLetterStr);
                postParams.put("currentWord",this.currentWord);

                TextView clickedLetterTextView  = (TextView) getActivity().findViewById(getResources().getIdentifier("createLesson" + this.currentLetterStr, "id", getActivity().getPackageName()));
                UpdateLessonTask updateLessonTask = new UpdateLessonTask(postParams,currentImageView, clickedLetterTextView);
                updateLessonTask.setDelegate(this);
                updateLessonTask.execute(UPDATE_LESSON_URL);

               break;
            case R.id.getImageBtn:

                if (!validateWord()) {
                    break;
                }
                showProgress(true);
                Log.d(DEBUG_TAG, "Get image button clicked " + this.currentWord);
                GetImageTask getImageTask = new GetImageTask(new HashMap<String, String>(),this);
                getImageTask.execute(new String[]{"https://api.datamarket.azure.com/Bing/Search/v1/Image?Query=%27" + this.currentWord + "%27&$format=json&$top=3"});
                break;
            case R.id.useCameraPic:
                Log.w(DEBUG_TAG, "Use camera button clicked");
                if (!validateWord()) {
                    break;
                }
                bundle = new Bundle();
                bundle.putString("username", this.username);

                TakePicFragment takePicFragment = new TakePicFragment();
                takePicFragment.setArguments(bundle);
                fragmentTransaction.replace(android.R.id.content, takePicFragment);
                fragmentTransaction.addToBackStack("create_lesson_fragment");
                fragmentTransaction.commit();
                break;
            case R.id.uploadImagePic:
                Log.d(DEBUG_TAG,"Choose from library button clicked ");
                if (!validateWord()) {
                    break;
                }
                this.adapter = new ViewPagerAdapter(getActivity());
                this.viewPager.setAdapter(adapter);
                pickPhoto(getView());
                break;
            default:
                Log.w(DEBUG_TAG, "Into default case = " + view.getId());
        }
    }

    private boolean validateWord() {

        wordEditText.requestFocus();

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (getActivity().getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if (TextUtils.isEmpty(this.currentLetterStr)) {
            wordEditText.setError("First tap the letter you want to add to the lesson");
            wordEditText.requestFocus();
            wordEditText.selectAll();

            if (getActivity().getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_SHOWN);
            }
            return false;
        }

        this.currentWord = wordEditText.getText().toString();

        if (TextUtils.isEmpty(this.currentWord) || !(this.currentWord.toUpperCase().startsWith(this.currentLetterStr)) ) {
            wordEditText.setError("Enter a word starting with " + this.currentLetterStr);
            wordEditText.requestFocus();
            wordEditText.selectAll();

            if (getActivity().getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.RESULT_SHOWN);
            }
            return false;
        }

        this.currentWord = this.currentWord.trim();
        String[] splitStr = this.currentWord.split(" ");

        if (splitStr.length > 1) {
            wordEditText.setError("No multiple words!");
            wordEditText.requestFocus();
            wordEditText.selectAll();

            if (getActivity().getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.RESULT_SHOWN);
            }
            return false;
        }
        return true;
    }

    @Override
    public void processCreateLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG,"Create lesson task completed! ");
    }

    @Override
    public void processGetImageResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Received get image json response " + jsonResponse);
        JsonParser parser = new JsonParser();
        JsonObject o = (JsonObject)parser.parse(jsonResponse);
        Log.d(DEBUG_TAG, "Converted json object = " + o);
        JsonObject d = o.getAsJsonObject("d");
        JsonArray results = d.getAsJsonArray("results");
        if (results != null && results.size() > 0) {
            Log.d(DEBUG_TAG, "Results size = " + results.size());
            String imageUrl = null;
            for (JsonElement result : results) {
                imageUrl = result.getAsJsonObject().getAsJsonObject("Thumbnail").get("MediaUrl").getAsString();
                Log.d(DEBUG_TAG, "Array element = " + imageUrl);
                DownloadImageTask downloadImageTask = new DownloadImageTask(null);
                downloadImageTask.setDelegate(this);
                downloadImageTask.execute(imageUrl);
            }
        }
        viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(getActivity());
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

        showProgress(false);


    }

    @Override
    public void processUpdateLessonResults(String jsonResponse, TextView clickedLetterTextView) {
        Log.d(DEBUG_TAG, "Update lesson response json response = " + jsonResponse);

        Type fwResponseType = new TypeToken<FWResponse>(){}.getType();
        final FWResponse fwResponse = new Gson().fromJson(jsonResponse, fwResponseType);

        Log.d(DEBUG_TAG, "fwResponse = " + fwResponse.getStatus());

        if (fwResponse != null && fwResponse.getStatus() != null) {
            if (fwResponse.getStatus().equals("OK")) {
                this.isEmptyLesson = false;
                this.imageUploadedForLetter = true;
            } else {
                Log.e(DEBUG_TAG, " Image upload failed " + jsonResponse);
            }
        } else {
            Log.e(DEBUG_TAG, " Image upload failed " + jsonResponse);
        }

        clickedLetterTextView.setBackgroundColor(ColorMap.getColorCode("SkyBlue"));
        clickedLetterTextView.setTextColor(ColorMap.getColorCode("Blue"));
        clickedLetterTextView.setTypeface(null, Typeface.BOLD);

        MainActivity mainActivity = (MainActivity) getActivity();

        mainActivity.getAddedLetters().add(this.currentLetterStr);

        this.wordEditText.setText(null);
        this.wordEditText.setHint("Tap on a letter");

        this.currentWord = null;
        this.currentLetterStr = null;
        this.currentLetterTextView.setText(null);
        mainActivity.setCameraBitmap(null);

        showProgress(false);
    }

    @Override
    public void processActivateLessonResults(String jsonResponse) {
        Log.d(DEBUG_TAG, "Activate lesson response json response = " + jsonResponse);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("username", this.username);
        LandingFragment landingFragment = new LandingFragment();
        landingFragment.setArguments(bundle);
        fragmentTransaction.replace(android.R.id.content, landingFragment);
        fragmentTransaction.commit();

    }


    public void pickPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(DEBUG_TAG,"Result code = " + resultCode);
        if(resultCode == Activity.RESULT_OK) {
            String filePath = getPath(getActivity(),data.getData());
            Log.d(DEBUG_TAG,"File path = " + filePath);


            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            Log.d(DEBUG_TAG,"Chosen picture = "  + bitmap);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500,  500, true);
            bitmap.recycle();

            bitmap = scaledBitmap;

            System.gc();

            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.setCameraBitmap(bitmap);
            setCustomImage();
        } else {
            Log.e(DEBUG_TAG,"Result code into else ");
        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


}
