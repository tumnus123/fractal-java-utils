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
					FractalLandscape fl = new FractalLandscape();

					showToast("" + fl.func01(123));
					
					StringBuilder sb = new StringBuilder();
					sb.append("" + fl.func01(123));
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
