package com.seethefractals.fractalworld;

import android.view.*;
import android.content.*;
import android.graphics.*;
import java.lang.Math.*;
import android.util.*;

public class ALSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{

	//private SurfaceHolder sh;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private AL myAL;
	private int iSpacer = -1;

	public ALSurfaceView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		getHolder().addCallback(this);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//Canvas c = getHolder().lockCanvas();
		//drawFractalByCenter(c);
		//getHolder().unlockCanvasAndPost(c);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// TODO: Implement this method
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO: Implement this method
	}

	public void drawFractalByCenter(AL myAL)
	{
		Canvas c = getHolder().lockCanvas();
		int step = myAL.getStepSize();
		int ctrX = (int) Math.floor(c.getWidth() / 2);
		int ctrY = (int) Math.floor(c.getHeight() / 2);
		int diff = step * myAL.getRadius();
		int startX = ctrX - diff;
		int startY = ctrY - diff;
		int endX = ctrX + diff;
		int endY = ctrY + diff;

		for (int x=startX;x <= endX;x += step)
		{
			for (int y=startY;y <= endY;y += step)
			{
				paint.setColor(Color.WHITE);
				c.drawLine(x, y, x + 1, y + 1, paint);
			}
		}
		getHolder().unlockCanvasAndPost(c);
	}

	private int getStepSize(AL myAL)
	{
		int shWidth = getHolder().getSurfaceFrame().width();
		int shHeight = getHolder().getSurfaceFrame().height();
		double dSpacer = 0d;
		if (shWidth > shHeight)
		{
			// divide the sh height by 
			//   the fl size to get a spacer
			dSpacer = shHeight / (myAL.getRadius() + 1) * 2;
			// round spacer size down
			iSpacer = (int) Math.floor(dSpacer);
		}
		else
		{
			// divide the sh width by 
			//   the fl size to get a spacer
			dSpacer = shWidth / (myAL.getRadius() + 1) * 2;
			// round spacer size down
			iSpacer = (int) Math.floor(dSpacer);
		}
		return iSpacer;
	}

	private int[] getFSVCenterXY()
	{
		Rect r = getHolder().getSurfaceFrame();
		int[] res = new int[2];
		if (r.width() % 2 == 0)
		{
			res[0] = r.width() / 2;
		}
		else
		{
			res[0] = (r.width() - 1) / 2;
		}
		if (r.height() % 2 == 0)
		{
			res[1] = r.height() / 2;
		}
		else
		{
			res[1] = (r.height() - 1) / 2;
		}
		return res; 
	}

}
