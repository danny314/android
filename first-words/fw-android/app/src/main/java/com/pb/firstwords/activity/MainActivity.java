package com.pb.firstwords.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.pb.firstwords.R;
import com.pb.firstwords.fragments.LandingFragment;
import com.pb.firstwords.fragments.SignUpFragment;

import java.util.HashSet;
import java.util.Set;

import static com.pb.firstwords.utils.FWUtils.DEBUG_TAG;


public class MainActivity extends Activity {

    private Set<String> addedLetters = new HashSet<String>();

    private Boolean learnerMode = Boolean.FALSE;

    private Bitmap cameraBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Boolean modeFromBundle = bundle.getBoolean("learnerMode");
            if (modeFromBundle != null) {
                this.learnerMode = modeFromBundle;
            } else {
                Log.e(DEBUG_TAG,"learnerMode is null! Should be either true or false");
            }
            LandingFragment landingFragment = new LandingFragment();
            landingFragment.setArguments(bundle);
            fragmentTransaction.add(android.R.id.content, landingFragment);
        } else {
            SignUpFragment signUpFragment = new SignUpFragment();
            fragmentTransaction.add(android.R.id.content, signUpFragment);
        }

        fragmentTransaction.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Log.d(DEBUG_TAG,"Main activity - Logout button clicked ");
            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Set<String> getAddedLetters() {
        return addedLetters;
    }

    public void setCameraBitmap(Bitmap bitmap) {
        setCameraBitmap(bitmap,false);
    }

    public void setCameraBitmap(Bitmap bitmap, boolean rotate) {
        if (bitmap != null && rotate) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            this.cameraBitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        } else {
            this.cameraBitmap = bitmap;
        }
    }

    public Bitmap getCameraBitmap() {
        return this.cameraBitmap;
    }

    public Boolean getLearnerMode() {
        return learnerMode;
    }

    public void setLearnerMode(Boolean learnerMode) {
        this.learnerMode = learnerMode;
    }
}
