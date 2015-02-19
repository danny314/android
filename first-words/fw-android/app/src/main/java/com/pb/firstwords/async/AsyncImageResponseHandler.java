package com.pb.firstwords.async;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by puneet on 10/20/14.
 */
public interface AsyncImageResponseHandler {

    public void processDownloadImageResult(Bitmap image, ImageView imageView);
}
