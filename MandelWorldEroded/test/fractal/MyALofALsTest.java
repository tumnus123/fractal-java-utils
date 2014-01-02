package fractal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class MyALofALsTest {
	int testSize = 200;
	private MyALofALs myALofALs;

	@Before
	public void initialize() {
		myALofALs = new MyALofALs(testSize);
	}

	@Test
	public void testGoNorth() throws Exception {

	}

	@Test
	public void testConstruction() {
		assertEquals(testSize, myALofALs.getRowCount());
	}

	@Test
	public void testGetSet() throws Exception {
		myALofALs = new MyALofALs(testSize);
		Double expected = 0.0;
		myALofALs.setXY(0, 0, expected);
		assertEquals(expected, myALofALs.getXY(0, 0));
		// different value
		expected = 1.2;
		myALofALs.setXY(0, 0, expected);
		assertEquals(expected, myALofALs.getXY(0, 0));
		// different index
		myALofALs.setXY(testSize - 1, testSize - 1, expected);
		assertEquals(expected, myALofALs.getXY(testSize - 1, testSize - 1));
		// out of bounds (high)
		try {
			myALofALs.setXY(testSize, testSize, expected);
			fail("Failed to catch out of bounds (high)");
		} catch (Exception e) {
			// this is fine
		}
		// out of bounds (low)
		try {
			myALofALs.setXY(-1, -1, expected);
			fail("Failed to catch out of bounds (low)");
		} catch (Exception e) {
			// this is fine
		}

	}

	@Test
	public void testName() throws Exception {

	}

}
