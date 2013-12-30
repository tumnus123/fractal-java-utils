package fractals;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * An artifical terrain example using an indexed TriangleStripArray as the
 * geometry of the surface, and fractals to generate the height field.
 * 
 * See HelloWorld4 for more explanation of the use of indexed triangle strip
 * array.
 * 
 */
public class MandelWorldEroded extends Applet {
	private static int NUMBER_OF_COLORS = 27;
	private static float SIDE_LENGTH = 350.0f; // Meters
	private static int MAXITER = 150;
	private static java.util.Random generator;
	private float roughness;
	private int divisions;
	private int lod = 0; // level of detail

	public MandelWorldEroded() {
		lod = 6;
		roughness = 0.45f;
	}

	public MandelWorldEroded(int lod, float roughness) {
		this.roughness = roughness;
		this.lod = lod;
		initialize();
	}

	private void initialize() {
		divisions = 1 << lod;
		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		Canvas3D canvas3D = new Canvas3D(config);
		add("Center", canvas3D);
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
		// Position the view
		TransformGroup viewingPlatformGroup = simpleU.getViewingPlatform()
				.getViewPlatformTransform();
		Transform3D t3d = new Transform3D();
		t3d.rotY(-Math.PI / 4);
		t3d.setTranslation(new Vector3f(0, 100, 0));
		viewingPlatformGroup.setTransform(t3d);

		BranchGroup scene = createSceneGraph(simpleU);
		simpleU.addBranchGraph(scene);

		// Adjust the clipping planes
		canvas3D.getView().setBackClipDistance(3000.0d);
		canvas3D.getView().setFrontClipDistance(1d);

	}

	public BranchGroup createSceneGraph(SimpleUniverse su) {

		float[][] hf = getHeightField2();
		// The height field
		BranchGroup objRoot = new BranchGroup();

		GeometryInfo gi = new GeometryInfo(GeometryInfo.TRIANGLE_STRIP_ARRAY);
		float[] coordinates = new float[(divisions + 1) * (divisions + 1) * 3];

		// Convert the height field to x, y, z values
		float metersPerDivision = SIDE_LENGTH / divisions;
		for (int row = 0; row < (divisions + 1); row++) {
			for (int col = 0; col < (divisions + 1); col++) {
				// coordinate index is the column plus
				// the row times the width of a row times
				// the 3 (one for each x, y, and z).
				int ci = (col + row * (divisions + 1)) * 3;
				// x, y, z
				coordinates[ci + 0] = metersPerDivision * col;
				coordinates[ci + 1] = hf[row][col];
				coordinates[ci + 2] = -metersPerDivision * row;
			}
		}
		// The number of indices is based on the
		// number of horizontal strips (height - 1) times the
		// number of vertices per strip (width * 2).
		int[] indices = new int[divisions * (divisions + 1) * 2];
		// The secret is that the strip vertices must be ordered
		// like this: NW, SW, NE, SE for each set of four corners
		// of a quad. A convenient way to accomplish this is to
		// organize the landscape in horizontal strips and iterate
		// across the columns calculating two vertices at a time.
		int pi = 0; // points index
		for (int row = 0; row < divisions; row++) {
			int width = row * (divisions + 1);
			for (int col = 0; col < (divisions + 1); col++) {
				int coordinateIndex = width + col;
				indices[pi] = coordinateIndex + (divisions + 1);
				indices[pi + 1] = coordinateIndex;
				pi = pi + 2;
			}
		}
		int[] stripCounts = new int[divisions];
		for (int strip = 0; strip < divisions; strip++) {
			stripCounts[strip] = (divisions + 1) * 2;
		}
		gi.setStripCounts(stripCounts);
		gi.setCoordinates(coordinates);
		gi.setCoordinateIndices(indices);
		float[] colors = getElevationColors();
		gi.setColors3(colors);
		int[] colorIndices = getElevationColorIndices(hf);
		gi.setColorIndices(colorIndices);
		NormalGenerator ng = new NormalGenerator();
		ng.generateNormals(gi);

		Geometry geometry = gi.getIndexedGeometryArray();
		Shape3D shape = new Shape3D(geometry);
		shape.setAppearance(getAppearance());
		objRoot.addChild(shape);

		// Add ambient light
		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(10000);
		AmbientLight ambient = new AmbientLight();
		ambient.setColor(new Color3f(1f, 1f, 1f));
		ambient.setInfluencingBounds(bounds);
		objRoot.addChild(ambient);

		// Add a directional light
		DirectionalLight directional = new DirectionalLight();
		directional.setDirection(0.3f, -1f, 0.5f);
		directional.setColor(new Color3f(1f, 1f, 1f));
		directional.setInfluencingBounds(bounds);
		objRoot.addChild(directional);

		// Add a keyboard navigator to move around
		TransformGroup vpTrans = su.getViewingPlatform()
				.getViewPlatformTransform();
		KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(vpTrans);
		keyNavBeh.setSchedulingBounds(bounds);
		objRoot.addChild(keyNavBeh);

		// Optimize the scene graph
		objRoot.compile();
		return objRoot;
	}

	private float[][] getHeightField2() {

		float[][] arrIters = new float[divisions + 1][divisions + 1];

		float iter = 0.0f;
		float minFoundIter = 999.0f;
		float maxFoundIter = -999.0f;

		// initialize the array
		/*
		 * for (int x = 0; x <= divisions; x++) { for (int y = 0; y <=
		 * divisions; y++) { arrIters[x][y] = 10.0f; } }
		 */

		// TEST COORDS 02
		double cfX = -0.239846106d;
		double cfY = 0.8455332715d;
		double fmag = 113.903492d;

		// populate the array with iter values
		for (int x = 0; x <= divisions; x++) {
			for (int y = 0; y <= divisions; y++) {
				iter = getMandelZ(x, y, cfX, cfY, fmag);
				// record the max and min values present
				if (iter > maxFoundIter)
					maxFoundIter = iter;
				if (iter < minFoundIter)
					minFoundIter = iter;
				arrIters[x][y] = iter;
			}
		}

		// normalize iters into heights between 0 and 100
		float[][] arrHeights = itersToHeights(arrIters, minFoundIter,
				maxFoundIter);

		// erode heights iteratively
		for (int i = 0; i < 10; i++) {
			arrHeights = erodeHeights(arrHeights);
		}

		// float diff = 0.0f;
		// float conv = 0.0f;
		// float vex = 50.0f; // vertical exaggeration
		// medH = medH / (divisions * divisions);
		// for (int x = 0; x <= divisions; x++) {
		// for (int y = 0; y <= divisions; y++) {
		// if (hf[x][y] > 50.0f) {
		// hf[x][y] = 50.0f;
		// }
		// quasi-log remap
		// test = hf[x][y];
		// hf[x][y] = (float) ((Math.log((double) test) / Math.log((double)
		// maxH)) * vex);
		// }
		// }

		return arrHeights;
	}

	private float[][] erodeHeights(float[][] arrHeights) {
		// create an array of erosion values
		float thisCellHeight, slope, deltaHeight;
		float maxHeightDiff = 0.0f;
		float testHeightDiff;
		int x1, y1;
		String tallShort = "";
		String neighbor = "";
		float talusAngle = 5.0f; // 4/width of map
		for (int x = 0; x <= divisions; x++) {
			for (int y = 0; y <= divisions; y++) {
				// algorithm from
				// http://oddlabs.com/download/terrain_generation.pdf
				// dMax = 0
				// for every i in neighorhood
				// d_i = h - h_i
				// if (d_i > dMax):
				// dMax = d_i
				// l = i
				// if (0 < dMax <= T):
				// delta_h = 0.5dMax
				// h = h - delta_h
				// h_l = h_l + delta_h
				// Use with Von Neumann neighborhood and
				// T between 8/N and 16/N
				//
				// determine tallest/shortest Von Neumann neighbor
				thisCellHeight = arrHeights[x][y];
				maxHeightDiff = 0.0f;
				tallShort = "";
				neighbor = "";
				slope = 0.0f;
				deltaHeight = 0.0f;
				if (x > 0) {
					testHeightDiff = Math.abs(arrHeights[x][y]
							- arrHeights[x - 1][y]);
					if (testHeightDiff > maxHeightDiff) {
						maxHeightDiff = testHeightDiff;
						neighbor = "-x";
						if (arrHeights[x][y] > arrHeights[x - 1][y]) {
							tallShort = "taller";
						} else {
							tallShort = "shorter";
						}
					}
				}
				if (x < divisions) {
					testHeightDiff = Math.abs(arrHeights[x][y]
							- arrHeights[x + 1][y]);
					if (testHeightDiff > maxHeightDiff) {
						maxHeightDiff = testHeightDiff;
						neighbor = "+x";
						if (arrHeights[x][y] > arrHeights[x + 1][y]) {
							tallShort = "taller";
						} else {
							tallShort = "shorter";
						}
					}
				}
				if (y > 0) {
					testHeightDiff = Math.abs(arrHeights[x][y]
							- arrHeights[x][y - 1]);
					if (testHeightDiff > maxHeightDiff) {
						maxHeightDiff = testHeightDiff;
						neighbor = "-y";
						if (arrHeights[x][y] > arrHeights[x][y - 1]) {
							tallShort = "taller";
						} else {
							tallShort = "shorter";
						}
					}
				}
				if (y < divisions) {
					testHeightDiff = Math.abs(arrHeights[x][y]
							- arrHeights[x][y + 1]);
					if (testHeightDiff > maxHeightDiff) {
						maxHeightDiff = testHeightDiff;
						neighbor = "+y";
						if (arrHeights[x][y] > arrHeights[x][y + 1]) {
							tallShort = "taller";
						} else {
							tallShort = "shorter";
						}
					}
				}

				// calculate slope
				slope = (float) maxHeightDiff; // assumes "run" is 1.0

				// if slope exceeds the talus angle, erode
				if (slope > talusAngle) {
					// erosion factor
					deltaHeight = maxHeightDiff / 2.0f;

					// determine x1, y1 where eroded height will end up
					x1 = x;
					y1 = y;
					if (neighbor.equals("-x")) {
						x1 -= 1;
					}
					if (neighbor.equals("+x")) {
						x1 += 1;
					}
					if (neighbor.equals("-y")) {
						y1 -= 1;
					}
					if (neighbor.equals("+y")) {
						y1 += 1;
					}
					// perform the erosion
					if (tallShort.equals("taller")) {
						arrHeights[x][y] -= deltaHeight;
						arrHeights[x1][y1] += deltaHeight;
					} else {
						arrHeights[x][y] += deltaHeight;
						arrHeights[x1][y1] -= deltaHeight;
					}

				}

				// check for values < 0 or > 100
				if ((arrHeights[x][y] < 0.0f) || (arrHeights[x][y] > 100.0f)) {
					System.out.println("arrHeights:" + arrHeights[x][y]);
					arrHeights[x][y] = 10.0f;
				}
			}
		}
		return arrHeights;
	}

	private float[][] itersToHeights(float[][] hf, float minFoundIter,
			float maxFoundIter) {
		float[][] arrHeights = new float[divisions + 1][divisions + 1];
		for (int x = 0; x <= divisions; x++) {
			for (int y = 0; y <= divisions; y++) {
				// Convert the iter to a value between
				float minHeight = 0.0f;
				// and
				float maxHeight = 100.0f;
				arrHeights[x][y] = minHeight + (hf[x][y] - minFoundIter)
						* (maxHeight - minHeight)
						/ (maxFoundIter - minFoundIter);
			}
		}

		/*
		 * truecolor algorithm? double add1, add2, add3, add4 = 0.0d; add1 =
		 * Math.sqrt(testVal); add2 = Math.log(Math.log(add1)); add3 = iter -
		 * add2; add4 = ((add3 / MAXITER) * 100); if (add4 > 100.0f) { //
		 * results must be between 0.0 and 100.0 add4 = 100.0f; }
		 */

		return arrHeights;
	}

	private float getMandelZ(int x, int y, double cfX, double cfY, double fmag) {
		// The canvas is 256x256... why?

		// Need to convert x,y to fractal space coordinates
		// based on center-mag
		// TEST COORDS 01
		// double cfX = -0.25d;
		// double cfY = 1.0d;
		// double fmag = 3.0d;

		// TEST COORDS 02
		// double cfX = -0.239846106d;
		// double cfY = 0.8455332715d;
		// double fmag = 313.903492d;

		// double fminX = fcX - (2.0d / fmag);
		// double fmaxX = fcX + (2.0d / fmag);
		// double fminY = fcY - (3.0d / fmag);
		// double fmaxY = fcY + (3.0d / fmag);
		//
		// conversion factor (world >> fractal)
		double cfactor = (0.75 - (-1.25)) / 256 / fmag;

		// to convert from world coords to fractal coords:
		// 1) find dX/dY from center (128,128)
		int dX = 128 - x;
		int dY = 128 - y;
		// 2) multiply dX,dY by conversion factor (lenFractal/lenWorld)
		double dfX = dX * cfactor;
		double dfY = dY * cfactor * -1;
		// 3) add dfX, dfY to cfX, cfY
		double fX = dfX + cfX;
		double fY = dfY + cfY;

		double zx;
		double zy;
		double lastZY = 0;
		double lastZX = 0;
		double cX;
		double cY;
		double tmp;
		double testVal;
		double lastTestVal = 0;
		int iter = 0;
		zx = zy = 0;
		cX = fX;
		cY = fY;
		while (zx * zx + zy * zy < 16 && iter <= MAXITER) {
			tmp = zx * zx - zy * zy + cX;
			lastZY = zy;
			lastZX = zx;
			zy = 2.0 * zx * zy + cY;
			zx = tmp;
			iter++;
		}

		/*
		 * // determine origin to incoords length double inCoordsLength =
		 * (lastZX * lastZX + lastZY * lastZY); double outCoordsLength = (zx *
		 * zx + zy * zy); // determine % along final segment double trueRatio =
		 * 16.0 - inCoordsLength / outCoordsLength - 16.0;
		 * 
		 * // add to new new value[-1] double trueIter = (double) iter +
		 * trueRatio; System.out.println(trueIter); return (float) trueIter;
		 */
		// }

		return (float) iter;
	}

	private float[][] getHeightField() {

		float[][] hf = new float[divisions + 1][divisions + 1];
		// Initialize the corners of the height field. You
		// could use random() for these values.
		hf[0][0] = 0f;
		hf[0][divisions] = 0f;
		hf[divisions][divisions] = 0f;
		hf[divisions][0] = 0f;
		float rough = roughness;
		// Evaluate the fractal for each of the
		// requested levels of detail.
		for (int detail = lod; detail > 0; detail--) {
			// The length of the side for
			// this level of detail iteration.
			int side = 1 << detail;
			int half = side >> 1;
			// Evaluate each square to create the diamond
			// pattern by finding a corner of each square.
			for (int x = 0; x < divisions; x += side) {
				for (int y = 0; y < divisions; y += side) {
					// x,y is the lower left corner of
					// the square to evaluate...
					diamond(hf, x, y, side, rough);
				}
			}
			if (half > 0) {
				// Evaluate each diamond to create the square
				// pattern by finding the center of each
				// diamond.
				for (int x = 0; x <= divisions; x += half) {
					for (int y = (x + half) % side; y <= divisions; y += side) {
						// x, y is the center of the
						// diamond to evaluate ...
						square(hf, x, y, side, rough);
					}
				}
			}
			// Adjust the roughness factor
			// to gradually scale it down.
			rough *= roughness;
			// Fractal purists would divide the roughness
			// by 2 for each iteration. This creates a
			// scene that is (subjectively) too rough.
			// rough = rough / 2.0f;
		}

		float min = hf[0][0];
		float max = hf[0][0];
		for (int i = 0; i <= divisions; i++) {
			for (int j = 0; j <= divisions; j++) {
				if (hf[i][j] < min) {
					min = hf[i][j];
				} else if (hf[i][j] > max) {
					max = hf[i][j];
				}
			}
		}

		// replace the hf values with the percentage
		// of the range.
		float range = max - min;
		for (int i = 0; i <= divisions; i++) {
			for (int j = 0; j <= divisions; j++) {
				hf[i][j] = 100f * (hf[i][j] - min) / range;
			}
		}
		return hf;
	}

	private void diamond(float[][] terrain, int x, int y, int side,
			float roughness) {
		if (side > 1) {
			int half = side / 2;
			float sum = 0f;
			sum += terrain[x][y];
			sum += terrain[x + side][y];
			sum += terrain[x + side][y + side];
			sum += terrain[x][y + side];
			float average = sum / 4.0f;
			terrain[x + half][y + half] = average + random() * roughness;
		}
	}

	private void square(float[][] terrain, int x, int y, int side,
			float roughness) {
		// Because x, y is the center of the diamond,
		// it is possible that corners of the diamond
		// are outside the bounds of the landscape, so
		// this method has a few boundary conditions
		// to check for this possibility.
		int half = side / 2;
		float sum = 0.0f, number = 0.0f;

		if (x - half >= 0) {
			// West corner
			sum += terrain[x - half][y];
			number += 1.0;
		}
		if (y - half >= 0) {
			// South corner
			sum += terrain[x][y - half];
			number += 1.0;
		}
		if (x + half <= divisions) {
			// East corner
			sum += terrain[x + half][y];
			number += 1.0;
		}
		if (y + half <= divisions) {
			// North corner
			sum += terrain[x][y + half];
			number += 1.0;
		}
		float elevation = sum / number;
		terrain[x][y] = elevation + random() * roughness;
	}

	private float random() {
		return 2.0f * getGenerator().nextFloat() - 1.0f;
	}

	private int[] getElevationColorIndices(float[][] hf) {
		int[] indices = new int[divisions * (divisions + 1) * 2];
		int i = 0;
		for (int row = 0; row < divisions; row++) {
			for (int col = 0; col < (divisions + 1); col++) {
				// Normalize the height value to a
				// color index between 0 and NUMBER_OF_COLORS - 1
				int nw = Math.round((NUMBER_OF_COLORS - 1)
						* ((100f - hf[row + 1][col]) / 100f));
				indices[i] = nw;
				int sw = Math.round((NUMBER_OF_COLORS - 1)
						* ((100f - hf[row][col]) / 100f));
				indices[i + 1] = sw;
				i = i + 2;
			}
		}
		return indices;
	}

	private float[] getElevationColors() {
		// These colors were arrived at through experimentation.
		// A color utility I found very useful is called
		// 'La boite a couleurs' by Benjamin Chartier
		//
		float[] colors = new float[3 * NUMBER_OF_COLORS];
		int i = 0;
		//
		colors[i++] = 0.72f;
		colors[i++] = 0.59f;
		colors[i++] = 0.44f;
		//
		colors[i++] = 0.64f;
		colors[i++] = 0.49f;
		colors[i++] = 0.32f;
		//
		colors[i++] = 0.51f;
		colors[i++] = 0.39f;
		colors[i++] = 0.25f;

		colors[i++] = 0.43f;
		colors[i++] = 0.33f;
		colors[i++] = 0.21f;
		//
		colors[i++] = 0.38f;
		colors[i++] = 0.29f;
		colors[i++] = 0.18f;
		//
		colors[i++] = 0.31f;
		colors[i++] = 0.25f;
		colors[i++] = 0.15f;

		colors[i++] = 0.27f;
		colors[i++] = 0.21f;
		colors[i++] = 0.13f;

		colors[i++] = 0.23f;
		colors[i++] = 0.28f;
		colors[i++] = 0.14f;

		//
		colors[i++] = 0.28f;
		colors[i++] = 0.36f;
		colors[i++] = 0.14f;
		//
		colors[i++] = 0.23f;
		colors[i++] = 0.35f;
		colors[i++] = 0.11f;
		//
		colors[i++] = 0.28f;
		colors[i++] = 0.43f;
		colors[i++] = 0.13f;

		colors[i++] = 0.30f;
		colors[i++] = 0.46f;
		colors[i++] = 0.14f;
		//
		colors[i++] = 0.33f;
		colors[i++] = 0.50f;
		colors[i++] = 0.16f;
		//
		colors[i++] = 0.35f;
		colors[i++] = 0.53f;
		colors[i++] = 0.17f;

		colors[i++] = 0.38f;
		colors[i++] = 0.58f;
		colors[i++] = 0.18f;

		colors[i++] = 0.43f;
		colors[i++] = 0.66f;
		colors[i++] = 0.20f;
		// sandy
		colors[i++] = 0.62f;
		colors[i++] = 0.58f;
		colors[i++] = 0.38f;
		//
		colors[i++] = 0.66f;
		colors[i++] = 0.62f;
		colors[i++] = 0.44f;
		//
		colors[i++] = 0.70f;
		colors[i++] = 0.67f;
		colors[i++] = 0.50f;
		//
		colors[i++] = 0.74f;
		colors[i++] = 0.71f;
		colors[i++] = 0.56f;
		//
		colors[i++] = 0.77f;
		colors[i++] = 0.75f;
		colors[i++] = 0.63f;
		// blue
		colors[i++] = 0.0f;
		colors[i++] = 0.56f;
		colors[i++] = 0.57f;
		//
		colors[i++] = 0.0f;
		colors[i++] = 0.38f;
		colors[i++] = 0.54f;
		//
		colors[i++] = 0.0f;
		colors[i++] = 0.24f;
		colors[i++] = 0.35f;
		//
		colors[i++] = 0.0f;
		colors[i++] = 0.14f;
		colors[i++] = 0.20f;
		//
		colors[i++] = 0.0f;
		colors[i++] = 0.07f;
		colors[i++] = 0.10f;
		//
		colors[i++] = 0.0f;
		colors[i++] = 0.03f;
		colors[i++] = 0.04f;

		return colors;
	}

	private static java.util.Random getGenerator() {
		if (generator == null) {
			long seed = System.currentTimeMillis();
			// long seed = 1089493588870L;
			// long seed = 1089771339464L;

			// The scene is reproducable with the same
			// seed so if you find one you like, keep
			// the seed.
			System.out.println("Seed: " + seed);
			generator = new java.util.Random(seed);
		}
		return generator;
	}

	private Appearance getAppearance() {
		Appearance appearance = new Appearance();
		PolygonAttributes polyAttrib = new PolygonAttributes();
		polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
		polyAttrib.setPolygonMode(PolygonAttributes.POLYGON_FILL);
		// Setting back face normal flip to true allows backfacing
		// polygons to be lit (normal is facing wrong way) but only
		// if the normal is flipped.
		polyAttrib.setBackFaceNormalFlip(true);
		appearance.setPolygonAttributes(polyAttrib);
		Material material = new Material();
		material.setAmbientColor(0f, 0f, 0f);
		// Changing the specular color reduces the shine
		// that occurs with the default setting.
		material.setSpecularColor(0.1f, 0.1f, 0.1f);
		appearance.setMaterial(material);
		return appearance;
	}

	public static void main(String[] args) {
		// Depending on the options used,
		// you may need to use the -Xms256M -Xmx1000M
		// options when running this example.
		// A roughness of 0.35 to 0.55 seems like
		// a good range.
		Frame frame = new MainFrame(new MandelWorldEroded(8, 0.45f), 1000, 500);
	}

}
