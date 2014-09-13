package com.moroze.flare;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

public class TransmitActivity extends Activity {
	Thread thread;
	FrameLayout display;
	EditText input;
	static String msg = "";
	int color = 0;
	int[][] bounds = {{100, 210}, {100, 210}, {100, 210}};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transmit);
		display = (FrameLayout) findViewById(R.id.transmitter);
		input = (EditText) findViewById(R.id.input);
	}
	public void transmitData(View v) {
		msg = input.getText().toString();
		Thread thread = new Thread() {
			@Override
			public void run() {
		    	String prevRed = "";
		    	String prevGreen = "";
		    	String prevBlue = "";
		    	byte[] encodedBytes = Base64.encode(msg.getBytes(), Base64.DEFAULT);
		    	System.out.println(new String(encodedBytes));
		    	String bitString = "";
		    	for(int i=0; i<encodedBytes.length; i++) {
		    		bitString += String.format("%8s", Integer.toBinaryString(encodedBytes[i] & 0xFF)).replace(' ', '0');
				}
				String[] chunks = new String[bitString.length() / 2];
				for (int i = 0; i < bitString.length(); i += 2) {
					chunks[i / 6] = bitString.substring(i, i + 2);
				}
				for(int i=0; i<chunks.length; i++) {
					String red = chunks[i].substring(0, 2);
					String green = chunks[i].substring(2, 4);
					String blue = chunks[i].substring(4, 6);
					int r = bitsToColor(red);
					int g = bitsToColor(green);
					int b = bitsToColor(blue);

					if(prevRed.equals(red) && prevGreen.equals(green) && prevBlue.equals(blue)) {
						r = g = b = 0;
						prevRed = prevGreen = prevBlue = "";
					}
					else {
						prevRed = red;
						prevGreen = green;
						prevBlue = blue;
					}
					Log.i("flare", String.format("(%d, %d, %d)", r, g, b));
					color = Color.rgb(r, g, b);
	                runOnUiThread(new Runnable() {
	                    @Override
	                    public void run() {
	                        display.setBackgroundColor(color);
	                    }
	                });
					try {
						Thread.sleep(1000/1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	private int bitsToColor(String bits) {
		if (bits.equals("0"))
			return bounds[0][0];
		else
			return bounds[0][1];
	}

	
}