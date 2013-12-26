package com.seethefractals.androidfractallandscape;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.view.View.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		
		Button btnRunTests = (Button) findViewById(R.id.btnRunTests);
		btnRunTests.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View p1)
				{
					// Tests
					StringBuilder sb = new StringBuilder();
					
					// Test01: 
					// New landscape, 10x10 array of 0.0f values
					// Centered at 0f,0f
					FractalLandscape fl = 
						new FractalLandscape(10,0f,0f,0f,1f);
					for(int i=0; i<10; i++) {
						for(int j=0;j<10;j++) {
							sb.append(fl.getHeightAtIndex(i,j) + " ");
						}
						sb.append(System.lineSeparator());
					}
					updateText(sb);
					
				}
	
		});
		
    }
	public void updateText(StringBuilder sb) {
		EditText et = (EditText) findViewById(R.id.etTestResults);
		et.setText(sb.toString());
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
