package com.pb.firstwords.async;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;


    public DownloadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }


    private AsyncImageResponseHandler delegate;

    public AsyncImageResponseHandler getDelegate() {
        return this.delegate;
    }

    public void setDelegate(AsyncImageResponseHandler _delegate) {
        this.delegate = _delegate;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Log.d(DEBUG_TAG, "Retrieving image from " + urldisplay);
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        getDelegate().processDownloadImageResult(result, imageView);
    }
}
