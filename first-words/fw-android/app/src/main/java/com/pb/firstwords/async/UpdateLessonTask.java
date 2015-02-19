package com.pb.firstwords.async;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;
import static com.pb.firstwords.utils.FWUtils.UPDATE_LESSON_URL;

/**
 * Created by puneet on 10/20/14.
 */
public class UpdateLessonTask extends AsyncTask<String, Void, String> {

    private Map<String,String> postParams = null;
    private ImageView imageView;
    private TextView letterView;

    public UpdateLessonTask(Map<String,String> postParams,ImageView imageView, TextView letterView) {
        this.postParams = postParams;
        this.imageView = imageView;
        this.letterView = letterView;
    }

    private UpdateLessonResponseHandler delegate;

    public UpdateLessonResponseHandler getDelegate() {
        return this.delegate;
    }

    public void setDelegate(UpdateLessonResponseHandler _delegate) {
        this.delegate = _delegate;
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

            Bitmap bitmap = drawable.getBitmap();

            Log.d(DEBUG_TAG, "Update lesson task - Got bitmap " + bitmap);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);

            byte[] data = bos.toByteArray();

            Log.d(DEBUG_TAG, "Update lesson task - byte array length " + data.length);

            HttpClient httpClient = new DefaultHttpClient();

            HttpPost postRequest = new HttpPost(UPDATE_LESSON_URL);
            postRequest.setHeader("Accept", "application/json");

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            entityBuilder.addTextBody("lessonName", postParams.get("lessonName"), ContentType.TEXT_PLAIN);
            entityBuilder.addTextBody("username", postParams.get("username"), ContentType.TEXT_PLAIN);
            entityBuilder.addTextBody("currentLetter", postParams.get("currentLetter"), ContentType.TEXT_PLAIN);
            entityBuilder.addTextBody("currentWord", postParams.get("currentWord"), ContentType.TEXT_PLAIN);
            String fileName = null;
            if (postParams.get("fileName") == null) {
                fileName = UUID.randomUUID().toString();
            }
            entityBuilder.addTextBody("fileName", fileName, ContentType.TEXT_PLAIN);

            if (data != null) {
                entityBuilder.addBinaryBody("img", data, ContentType.create("image/jpeg"), fileName);
            }

            HttpEntity entity = entityBuilder.build();

            postRequest.setEntity(entity);

            HttpResponse response = httpClient.execute(postRequest);

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            String sResponse;

            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }

            Log.d(DEBUG_TAG, "File upload Response: " + s);

            return s.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
    return null;
    }

    @Override
    protected void onPostExecute(String result) {
        getDelegate().processUpdateLessonResults(result, letterView);
    }
}
