package com.moroze.flare;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
	private SurfaceHolder mHolder;
	private Camera mCamera;
	private static String TAG = "flare";
	//This variable is responsible for getting and setting the camera settings  
	private Parameters parameters;  
	//this variable stores the camera preview size   
	private Size previewSize;  
	private int[] pixels;  
	int[] prevColors = {-1, -1, -1}; 
	private static int FRAMES = 100;
	int[][] frames = new int[FRAMES][3];
	public static int R = 0;
	public static int G = 0;
	public static int B = 0;
	boolean toggle = true;
	int ct=0;
	String msg = "";
	FrameLayout background;
	
	public CameraPreview(Context context, Camera camera, FrameLayout background) {
		super(context);
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
		// The Surface has been created, now tell the camera where to draw the preview.
		try {  
			mCamera.setPreviewDisplay(mHolder);  
		} catch (IOException e) {  	
			// TODO Auto-generated catch block  
			e.printStackTrace();  
		}  
		parameters = mCamera.getParameters();  
		parameters.setPreviewSize(32, 32);
		List<String> focuses = parameters.getSupportedFocusModes();
		for(String focus : focuses) {
			System.out.println(focus);
		}
		parameters.setFocusMode(Parameters.FOCUS_MODE_INFINITY);
		previewSize = parameters.getPreviewSize();  
		parameters.setExposureCompensation((parameters.getMinExposureCompensation() + parameters.getMaxExposureCompensation()) / 2);
		parameters.setAutoWhiteBalanceLock(true);
		//parameters.setAutoExposureLock(true);
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

		if (mHolder.getSurface() == null){
			// preview surface does not exist
			return;
		}

		// stop preview before making changes
		try {
			mCamera.stopPreview();
		} catch (Exception e){
			// ignore: tried to stop a non-existent preview
		}

		// set preview size and make any resize, rotate or
		// reformatting changes here

		// start preview with new settings
		try {
			mCamera.setPreviewCallback(this);

			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();

		} catch (Exception e){
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {  
		//transforms NV21 pixel data into RGB pixels  
		decodeYUV420SP(pixels, data, previewSize.width,  previewSize.height);  
		int[] avg = averagePixels(pixels);
		int[][] bounds = {{100, 210}, {100, 210}, {100, 210}};
		int threshold = 50; 
		int[] colors = {-2, -2, -2};
		ct++;
		for(int i=0; i<3; i++) {
			for(int j=0; j<2; j++) {
				if(avg[i] < bounds[i][j] + threshold && avg[i] >= bounds[i][j] - threshold) {
					colors[i] = j;
				}
				else if(avg[i] < 10) {
					colors[i] = -1;
				}
			}
		}
//		System.out.format("%d, %d, %d%n", avg[0], avg[1], avg[2]);
		background.setBackgroundColor(Color.rgb(avg[0], avg[1], avg[2]));
		if(colors[0] != prevColors[0] || colors[1] != prevColors[1] || colors[2] != prevColors[2]) {
			if(colors[0] == -1 && colors[1] == -1 && colors[2] == -1) {
				ct = 0;
				colors[0] = prevColors[0];
				colors[1] = prevColors[1];
				colors[2] = prevColors[2];
				prevColors[0] = -1;
				prevColors[1] = -1;
				prevColors[2] = -1;
			}
			if(colors[0] != -2 && colors[1] != -2 && colors[2] != -2) {
				ct = 0;
				prevColors[0] = colors[0];
				prevColors[1] = colors[1];
				prevColors[2] = colors[2];
			
				for(int i=0; i<3; i++) {
					switch(colors[i]) {
					case 0:
						msg+="0";
						break;
					case 1:
						msg+="1";
						break;
					}
				}
			}
			if(ct > 25 && !msg.equals("")) {
				new AlertDialog.Builder(
						this.getContext()).setTitle("Message Received!").setMessage(msg).create().show();
				camera.stopPreview();
			}
		}
	}  

	int[] averagePixels(int[] pixels) {
		int average[] = new int[3];
		for(int i=0; i<pixels.length; i++) { 
			average[0] += Color.red(pixels[i]);
			average[1] += Color.green(pixels[i]);
			average[2] += Color.blue(pixels[i]);
		}
		System.out.println(pixels.length);
		average[0] /= pixels.length;
		average[1] /= pixels.length;
		average[2] /= pixels.length;
		return average;
	}
	//Method from Ketai project! Not mine! See below...  
	void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {  

		final int frameSize = width * height;  

		for (int j = 0, yp = 0; j < height; j++) {       int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;  
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

			if (r < 0)                  r = 0;               else if (r > 262143)  
				r = 262143;  
			if (g < 0)                  g = 0;               else if (g > 262143)  
				g = 262143;  
			if (b < 0)                  b = 0;               else if (b > 262143)  
				b = 262143;  

			rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);  
		}  
		}  
	} 
}

