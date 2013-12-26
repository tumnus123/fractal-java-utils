package com.seethefractals.androidfractallandscape;

public class FractalLandscape
{
	public int flSize;
	public float[][] flArray;
	private float spacing;
	private float mag;
	private float centerX;
	private float centerY;

	public FractalLandscape(int iSize, float ctrX, float ctrY, float mag, float spacing) {
		flArray = new float[iSize][iSize];
		flSize = iSize;
		init();
		centerX = ctrX;
		centerY = ctrY;
		this.mag = mag;
		this.spacing = spacing;
		render();
	}

	private void render()
	{
		for(int i=0; i<flSize; i++) {
			for(int j=0;j<flSize;j++) {
				// determine multipliers
				int xMult = (flSize / 2) - i;
				int yMult = (flSize / 2) - j;
				flArray[i][j] = calcHeight(centerX + (spacing*xMult), centerY+ (spacing*yMult));
			}
		}
		
	}

	private float calcHeight(float x, float y)
	{
		// TODO: Replace with Mset calc
		return x+y;
	}

	public void setMag(float mag)
	{
		this.mag = mag;
	}

	public float getMag()
	{
		return mag;
	}

	public float getSpacing()
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
	
	public int getSize()
	{
		return flSize;
	}	
}
