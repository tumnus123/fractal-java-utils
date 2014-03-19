package com.seethefractals.fractalworld;


import java.util.ArrayList;
import java.util.Collections;

/**
  * @author tumnus123
  * @author tarah.west
  * 
  */
public class AL
{
	// this class contains an ArrayList of ArrayLists
	// and methods for performing ops on that AL
	// add a row/col, remove a row/col, suppress a row/col,
	// erodeHeights...
	//
	// this class depends on the ScapeNode class
	//
	// the ScapeNode class represents a point as a
	// position on a grid of unevenly-spaced points
	// Properties include iXpos, iYpos, fHeight, fOffset  
	// Origin is center of grid
	// Offset is dist away from next innermost node
	// This allows node spacing to vary by dist to origin
	//
	// once a node's height is calculated and eroded, 
	// it does not change while the node exists.
	// New rows/cols of nodes are added as needed as a 
	// result of magnification
	// Newly added nodes are fully suppressed at initialization,
	// meaning their initial height is the 

	private ArrayList<ArrayList<Double>> ba;
	private int radius;
	private int stepSize;
	
	public AL(int iRadius, int iStepSize)
	{
		// AL must have a center node, so
		// double radius and add one
		radius = iRadius;
		int i = (radius * 2) + 1;
		
		// StepSize is the basic space between nodes
		stepSize = iStepSize;
		
		// create and initialize
		ba = new ArrayList<ArrayList<Double>>(i);
		for (int j = 0; j < i; j++)
		{
			ArrayList<Double> col = 
				new ArrayList<Double>(Collections.nCopies(i, 0.0));
			ba.add(col);
		}
	}

	public Double getXY(int x, int y)
	{
		// center node is 0,0
		int offX = x + radius;
		int offY = y + radius;
		return ba.get(offX).get(offY);
	}

	public void setXY(int x, int y, Double val)
	{
		// center node is 0,0
		int offX = x + radius;
		int offY = y + radius;
		ba.get(offX).set(offY, val);
	}

	public void setRadius(int iRadius) {
		radius = iRadius;
	}
	public int getRadius()
	{
		return radius;
	}

	public void setStepSize(int iStepSize) {
		stepSize = iStepSize;
	}
	public int getStepSize()
	{
		return stepSize;
	}

}
