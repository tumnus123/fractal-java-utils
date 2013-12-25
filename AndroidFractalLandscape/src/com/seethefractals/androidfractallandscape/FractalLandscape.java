package com.seethefractals.androidfractallandscape;

public class FractalLandscape
{
	public int flSize;
	public float[][] flArray;
	private float spacing;
	private float mag;
	private float[] centerXY;

	public FractalLandscape(int iSize) {
		flArray = new float[iSize][iSize];
		flSize = iSize;
		init();
		centerXY = new float[] {0.0f,0.0f};
	}

	public void setCenterXY(float[] centerXY)
	{
		this.centerXY = centerXY;
	}

	public float[] getCenterXY()
	{
		return centerXY;
	}

	public void setFlMag(float flMag)
	{
		this.mag = flMag;
	}

	public float getFlMag()
	{
		return mag;
	}

	public float getFlSpacing()
	{
		return spacing;
	}
	
	public void init() {
		for(int i=0; i<flSize; i++) {
			for(int j=0;j<flSize;j++) {
				flArray[i][j] = 0.0f;
			}
		}
	}
	
	public float getHeightAtIndex(int i, int j) {
		return flArray[i][j];
	}
	
	public void setFlSize(int flSize)
	{
		this.flSize = flSize;
	}

	public int getFlSize()
	{
		return flSize;
	}	
}
