package com.seethefractals.fractalworld;

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

		final ALSurfaceView myALSV = (ALSurfaceView) findViewById(R.id.myALSV);
		final AL myAL = new AL(10,10);		
		
		Button btnCreateAl = (Button) findViewById(R.id.btnCreateAL);
		btnCreateAl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View p1) {
				EditText etRadius = (EditText) findViewById(R.id.etRadius);
				String sRadius = etRadius.getText().toString();
				int iRadius = Integer.parseInt(sRadius);
				
				EditText etStepSize = (EditText) findViewById(R.id.etStepSize);
				String sStepSize = etStepSize.getText().toString();
				int iStepSize = Integer.parseInt(sStepSize);

				myAL.setRadius(iRadius);
				myAL.setStepSize(iStepSize);
				myALSV.drawFractalByCenter(myAL);
			}

		});

		Button btnRunIncrs = (Button) findViewById(R.id.btnRunIncrs);
		btnRunIncrs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View p1) {
				EditText etStepIncr = (EditText) findViewById(R.id.etStepIncr);
				String sStepIncr = etStepIncr.getText().toString();
				int iStepIncr = Integer.parseInt(sStepIncr);

				EditText etNumIncrs = (EditText) findViewById(R.id.etNumIncrs);
				String sNumIncrs = etNumIncrs.getText().toString();
				int iNumIncrs = Integer.parseInt(sNumIncrs);
				
				// loop through to animate increase in step size
				int iNextSize = myAL.getStepSize();
				for(int i=0;i<iNumIncrs;i++) {
					iNextSize = iNextSize + (iStepIncr);
					myAL.setStepSize(iNextSize);
					//myALSV.destroyDrawingCache();
					myALSV.drawFractalByCenter(myAL);
				}
			}
		});		
		
	}

	private void makeToast(String s)
	{
		Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
	}

}
