package com.moroze.flare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements
SurfaceHolder.Callback, PreviewCallback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private static String TAG = "flare";
	// This variable is responsible for getting and setting the camera settings
	private Parameters parameters;
	// this variable stores the camera preview size
	private Size previewSize;
	private int[] pixels;
	int[] prevColors = { -1, -1, -1 };
	private static int FRAMES = 100;
	public static int R = 0;
	public static int G = 0;
	public static int B = 0;
	ArrayList<Integer> recordedColors;

	int numIndexes = 3;
	int same = 0;
	int lastR = -2;
	int lastG = -2;
	int lastB = -2;
	int lastestR = -2;
	int lastestG = -2;
	int lastestB = -2;

	int lastNormR = -2;
	int lastNormG = -2;
	int lastNormB = -2;
	
	boolean toggle = true;
	int ct = 0;
	String msg = "";
	FrameLayout background;

	public CameraPreview(Context context, Camera camera, FrameLayout background) {
		super(context);
		recordedColors = new ArrayList<Integer>();
		this.background = background;
		mCamera = camera;
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@SuppressLint("NewApi")
	public void surfaceCreated(SurfaceHolder holder) {
		// The Surface has been created, now tell the camera where to draw the
		// preview.
		try {
			mCamera.setPreviewDisplay(mHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parameters = mCamera.getParameters();
		parameters.setPreviewSize(32, 32);
		List<String> focuses = parameters.getSupportedFocusModes();
		for (String focus : focuses) {
			System.out.println(focus);
		}
		parameters.setFocusMode(Parameters.FOCUS_MODE_INFINITY);
		previewSize = parameters.getPreviewSize();
		parameters.setExposureCompensation(parameters
				.getMinExposureCompensation());
		parameters.setAutoWhiteBalanceLock(true);
		// parameters.setAutoExposureLock(true);
		pixels = new int[previewSize.height * previewSize.width];
		mCamera.setParameters(parameters);
		mCamera.setDisplayOrientation(90);
		mCamera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		// empty. Take care of releasing the Camera preview in your activity.
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.

		if (mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewCallback(this);

			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// transforms NV21 pixel data into RGB pixels
		decodeYUV420SP(pixels, data, previewSize.width, previewSize.height);
		int[] avg = averagePixels(pixels);
		int[] colors = new int[3];
		for (int i = 0; i < 3; i++) {
			if (avg[i] < 1)
				colors[i] = 0;
			else if (avg[i] < 160)
				colors[i] = 138;
			else	
				colors[i] = 255;
		}
		int r = avg[0];
		int g = avg[1];
		int b = avg[2];
		int cr = colors[0];
		int cg = colors[1];
		int cb = colors[2];

		if(cr * cg * cb == 0 && cr+cg+cb>0) return;

		if((cr == lastR && cr != lastestR) || (cg == lastG && cg != lastestG) || (cb == lastB && cb != lastestB)) {
			recordedColors.add(cr);
			recordedColors.add(cg);
			recordedColors.add(cb);
			background.setBackgroundColor(Color.rgb(cr, cg, cb));
			System.out.println(String.format("%d, %d, %d", cr, cg, cb));
		}
		lastestR = lastR;
		lastestG = lastG;
		lastestB = lastB;
		lastR = cr;
		lastG = cg;
		lastB = cb;
	}


private int modVal(int val, int up) {
    if (up == 1){
        return 255;
    } else {
        return 138;
    }
}

private void translateCode() {
	String lookUpTable = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
	for (int i = 0; i < numIndexes; i+= 3) {
		System.out.println(String.format("%d, %d, %d",recordedColors.get(i), recordedColors.get(i+1), recordedColors.get(i+2)));
	}

	String b64 = "";
	int startPosition = -1;
	for (int i = 0; i < numIndexes; i+=6){
		if(recordedColors.get(i) == 0 && recordedColors.get(i+1)==0 && recordedColors.get(i+2)==0){
			startPosition = i+3;
			break;
		}
		if(recordedColors.get(i+3) == 0 && recordedColors.get(i+4)==0 && recordedColors.get(i+5)==0){
			startPosition = i+6;
			break;
		}
	}

	System.out.println(String.format("starting position: %d", startPosition));
	for (int i= startPosition; i<numIndexes;i+=6){
		if (recordedColors.get(i) + recordedColors.get(i+1) + recordedColors.get(i + 2) == 0) {
			recordedColors.set(i + 2, recordedColors.get(i + 2 - 3));
			recordedColors.set(i + 1, recordedColors.get(i + 1 - 3));
			recordedColors.set(i,  recordedColors.get(i - 3));
		}

		if (recordedColors.get(i + 5) + recordedColors.get(i + 4) + recordedColors.get(i + 3) == 0) {
			recordedColors.set(i + 5, recordedColors.get(i + 5 - 3));
			recordedColors.set(i + 4, recordedColors.get(i + 4 - 3));
			recordedColors.set(i + 3, recordedColors.get(i + 3 - 3));
		}

		int num = (recordedColors.get(i + 5) == 138 ? 0 : 1);
		num += 2 * (recordedColors.get(i + 4) == 138 ? 0 : 1);
		num += 2 * 2 * (recordedColors.get(i + 3) == 138 ? 0 : 1);

		num += 2 * 2 * 2 * (recordedColors.get(i + 2) == 138 ? 0 : 1);
		num += 2 * 2 * 2 * 2 * (recordedColors.get(i + 1) == 138 ? 0 : 1);
		num += 2 * 2 * 2 * 2 * 2 * (recordedColors.get(i ) == 138 ? 0 : 1);

		b64 += lookUpTable.charAt(num);
	}

	System.out.println(b64);
	int spaceLeft = 4-(b64.length() % 4);
	for(int i =0;i<spaceLeft;i+=1){
		b64+="=";
	}
	byte[] output = Base64.decode(b64.getBytes(), Base64.DEFAULT);
	System.out.println(new String(output));
}

int[] averagePixels(int[] pixels) {
	int average[] = new int[3];
	for (int i = 0; i < pixels.length; i++) {
		average[0] += Color.red(pixels[i]);
		average[1] += Color.green(pixels[i]);
		average[2] += Color.blue(pixels[i]);
	}
	average[0] /= pixels.length;
	average[1] /= pixels.length;
	average[2] /= pixels.length;
	return average;
}

// Method from Ketai project! Not mine! See below...
void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {

	final int frameSize = width * height;

	for (int j = 0, yp = 0; j < height; j++) {
		int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
		for (int i = 0; i < width; i++, yp++) {
			int y = (0xff & ((int) yuv420sp[yp])) - 16;
			if (y < 0)
				y = 0;
			if ((i & 1) == 0) {
				v = (0xff & yuv420sp[uvp++]) - 128;
				u = (0xff & yuv420sp[uvp++]) - 128;
			}

			int y1192 = 1192 * y;
			int r = (y1192 + 1634 * v);
			int g = (y1192 - 833 * v - 400 * u);
			int b = (y1192 + 2066 * u);

			if (r < 0)
				r = 0;
			else if (r > 262143)
				r = 262143;
			if (g < 0)
				g = 0;
			else if (g > 262143)
				g = 262143;
			if (b < 0)
				b = 0;
			else if (b > 262143)
				b = 262143;

			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
					| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
		}
	}
}
}
