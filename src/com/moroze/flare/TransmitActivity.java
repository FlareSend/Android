package com.moroze.flare;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
		setContentView(R.layout.activity_transmit);
		display = (FrameLayout) findViewById(R.id.transmitter);
		display.setBackgroundColor(Color.rgb(0, 0, 0));
		input = (EditText) findViewById(R.id.input);

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    input.setText(sharedText);
                }
            } 
        }

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
		    	byte[] bytes = new byte[encodedBytes.length-1];
		    	for(int i=0; i<bytes.length; i++) {
		    		bytes[i] = encodedBytes[i]; 
		    	}
		    	String base64 = new String(bytes);
		    	System.out.println(base64);
		    	String bitString = "";
		    	for(int i=0; i<base64.length(); i++) {
		    		char c = base64.charAt(i);
		    		String key = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		    		if(c != '=') {
		    			int index = key.indexOf(c);
		    			System.out.println(index);
		    			bitString += String.format("%8s", Integer.toBinaryString(index & 0xFF)).replace(' ', '0').substring(2, 8);
		    		}

		    	}
		    	System.out.println(bitString);
				String[] chunks = new String[bitString.length() / 3];
				for (int i = 0; i < bitString.length()-2; i += 3) {
					chunks[i / 3] = bitString.substring(i, i + 3);
				}
				for(int i=0; i<chunks.length; i++) {
					System.out.println(chunks[i]);
					String red = chunks[i].substring(0, 1);
					String green = chunks[i].substring(1, 2);
					String blue = chunks[i].substring(2, 3);
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
						Thread.sleep(1000/15);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Transmission over", 
                                Toast.LENGTH_LONG).show();
                        display.setBackgroundColor(Color.rgb(0, 0, 0));
                    }
                });
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