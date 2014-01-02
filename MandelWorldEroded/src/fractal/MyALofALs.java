/**
 * 
 */
package fractal;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author tumnus123
 * 
 */
public class MyALofALs {

	private ArrayList<ArrayList<Double>> ba;

	public MyALofALs(int i) {
		ba = new ArrayList<ArrayList<Double>>(i);
		for (int j = 0; j < i; j++) {
			ArrayList<Double> col = new ArrayList<Double>(Collections.nCopies(
					i, 0.0));
			ba.add(col);
		}
	}

	public Double getXY(int x, int y) {
		return ba.get(x).get(y);
	}

	public void setXY(int x, int y, Double val) {
		ba.get(x).set(y, val);
	}

	public int getRowCount() {
		return ba.size();
	}

}
