package com.moroze.flare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView leftArrow, rightArrow;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftArrow = (TextView) findViewById(R.id.left_arrow);
        rightArrow = (TextView) findViewById(R.id.right_arrow);
        Typeface corbert = Typeface.createFromAsset( getAssets(), "Corbert-Regular.otf" );

        Typeface fontAwesome = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        ((TextView) findViewById(R.id.title)).setTypeface(corbert);
        ((TextView) findViewById(R.id.title)).setShadowLayer(5, 0, 0, Color.BLACK);

        leftArrow.setTypeface(fontAwesome);
        rightArrow.setTypeface(fontAwesome);
    }
    
    public void launchReceive(View v) {
    	Intent intent = new Intent(this, ReceiveActivity.class);
    	startActivity(intent);
    }
    public void launchTransmit(View v) {
    	Intent intent = new Intent(this, TransmitActivity.class);
    	startActivity(intent);
    }
}
