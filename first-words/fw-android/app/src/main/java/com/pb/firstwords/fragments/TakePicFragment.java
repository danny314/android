package com.pb.firstwords.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pb.firstwords.R;
import com.pb.firstwords.activity.MainActivity;
import com.pb.firstwords.utils.CameraPreview;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;


public class TakePicFragment extends Fragment {

    private String streamId;

    private Camera mCamera;
    private CameraPreview mCameraPreview;

    private TextView statusText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args  != null) {
            this.streamId = args.getString("streamId");
        } else {
            Log.e(DEBUG_TAG,"No bundle found");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.takepic_fragment, container, false);

        Log.d(DEBUG_TAG,"Inflating take pic fragment...");

        super.onCreate(savedInstanceState);

        this.statusText = (TextView) v.findViewById(R.id.takePicStatusText);
        mCamera = getCameraInstance();
        mCamera.setDisplayOrientation(90);
        mCameraPreview = new CameraPreview(getActivity(), mCamera);
        FrameLayout preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        preview.addView(mCameraPreview);

        ImageButton captureButton = (ImageButton) v.findViewById(R.id.shutterBtn);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mPicture);
                statusText.setText("Picture taken.");

            }
        });

        ImageButton useThisPicButton = (ImageButton) v.findViewById(R.id.usePictureBtn);
        useThisPicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusText.setText("Uploading photo. Please wait...");
                MainActivity mainActivity = (MainActivity) getActivity();
                ImageView imagePreview = (ImageView) getView().findViewById(R.id.myImageView);
                mainActivity.setCameraBitmap(((BitmapDrawable) imagePreview.getDrawable()).getBitmap(),true);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.popBackStack("create_lesson_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        return v;
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.e(DEBUG_TAG,e.toString());
        }
        return camera;
    }

    Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(DEBUG_TAG, "Picture taken callback called.");
            ImageView imagePreview = (ImageView) getView().findViewById(R.id.myImageView);
            Log.d(DEBUG_TAG, "Obtained image preview object.");

            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 500,  500, true);
            bitmap.recycle();

            bitmap = scaledBitmap;

            System.gc();

            imagePreview.setImageBitmap(bitmap);

        }
    };
}