package com.moroze.flare;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
