package com.moroze.flare;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

	class Transmitter extends SurfaceView implements Runnable{

		Thread thread = null;
		SurfaceHolder surfaceHolder;
		volatile boolean running = false;
		public Transmitter(Context context) {
			super(context);       
			// TODO Auto-generated constructor stub
			surfaceHolder = getHolder();
		}

		public void onResumeMySurfaceView(){
			running = true;
			thread = new Thread(this);
			thread.start();
		}

		public void onPauseMySurfaceView(){
			boolean retry = true;
			running = false;
			while(retry){
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(running){
				if(surfaceHolder.getSurface().isValid()){
						Canvas canvas = surfaceHolder.lockCanvas();
						canvas.drawRGB(CameraPreview.R, CameraPreview.G, CameraPreview.B);
						surfaceHolder.unlockCanvasAndPost(canvas);
					}
				}
			}
		}
