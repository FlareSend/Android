package com.moroze.flare;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

public class ReceiveActivity extends Activity {

    private Camera mCamera;
    private CameraPreview mPreview;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_receive);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        FrameLayout background = (FrameLayout) findViewById(R.id.chain_output);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera, background);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
