package com.seethefractals.androidfractallandscape;
import android.view.*;
import android.content.*;
import android.graphics.*;


public class FractalSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{

	private SurfaceHolder sh;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private FractalLandscape fl;

	public FractalSurfaceView(Context context, FractalLandscape fl) {
		super(context);
		sh = getHolder();
		sh.addCallback(this);
		paint.setColor(Color.WHITE);
		paint.setStyle(Paint.Style.FILL);
		this.fl = fl;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		//Canvas canvas = sh.lockCanvas();
		//canvas.drawColor(Color.BLACK);
		drawFractal();
		//sh.unlockCanvasAndPost(canvas);
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
	
	private void drawFractal() {
		Canvas canvas = sh.lockCanvas();
		int[] cXY = getFSVCenterXY();
		
		for(int x=0;x<fl.getSize();x++) {
			for(int y=0;y<fl.getSize();y++) {
				paint.setColor(fl.getHeightAtIndex(x,y));
				canvas.drawLine(x+cXY[0],y+cXY[1],x+1+cXY[0],y+1+cXY[1],paint);
			}
		}
		sh.unlockCanvasAndPost(canvas);
	}

	private int[] getFSVCenterXY() {
		Rect r = sh.getSurfaceFrame();
		int[] res = new int[2];
		if(r.width()%2==0){
			res[0] = r.width()/2;
		} else{
			res[0] = (r.width()-1)/2;
		}
		if(r.height()%2==0){
			res[1] = r.height()/2;
		} else{
			res[1] = (r.height()-1)/2;
		}
		return res; 
	}

}
