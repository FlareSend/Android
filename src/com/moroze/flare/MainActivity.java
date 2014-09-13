package com.moroze.flare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements 
GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener{

    private GestureDetectorCompat mDetector; 
    private TextView leftArrow, rightArrow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftArrow = (TextView) findViewById(R.id.left_arrow);
        rightArrow = (TextView) findViewById(R.id.right_arrow);
        Typeface fontAwesome = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        leftArrow.setTypeface(fontAwesome);
        rightArrow.setTypeface(fontAwesome);
        mDetector = new GestureDetectorCompat(this,this);
        mDetector.setOnDoubleTapListener(this);
    }
    
    public void launchReceive(View v) {
    	Intent intent = new Intent(this, ReceiveActivity.class);
    	startActivity(intent);
    }
    public void launchTransmit(View v) {
    	Intent intent = new Intent(this, TransmitActivity.class);
    	startActivity(intent);
    }
    
    @Override 
    public boolean onTouchEvent(MotionEvent event){ 
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(velocityX < -1000)
			launchReceive(null);
		if(velocityX > 1000)
			launchTransmit(null);
		return false;
	}
}
