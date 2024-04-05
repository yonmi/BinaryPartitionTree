package examples.standard.sequential.xp_rfiap24;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utils.d2.cooccurrence.CooccurrenceMatrix;

/**
 * XP on cooccurrence matrix for the paper - MCT - RFIAP24 - ICPR24
 * 1- Prepare values
 * 2- Generate image
 * 3- build the cooccurrence matrix as an histogram
 */
public class XpCooccurrenceMatrix {

	public static void main(String[] args) {
		
		/* 1. Prepare values 
		 * ************************************************************************************/		
		
		System.out.println("## Step-1: Prepare the set of values ##");
		int width = 5;
		int height = 5;
		int nbValues = 9; // number of possible values of each point
		ArrayList<Double> valueSet = new ArrayList<Double>(nbValues);
		for(double i = 0.0; i < nbValues; i++) {
			valueSet.add(i); /* Transform each value to an RGB color with R = i, G = i, B = i */
		}
		
		/* 2. Generate the image of the example
		 * ************************************************************************************/
		
		System.out.println("## Step-1: Cooccurrence Matrix ##");
		BufferedImage img = XpCooccurrenceMatrix.generateImgExample(width, height, valueSet);
		
		/* 3. Build the cooccurrence matrix ~ histogram
		 * ************************************************************************************/
		int connexity = 4; // neighbors to consider
		CooccurrenceMatrix<Double> cooccMat = new CooccurrenceMatrix<Double>(img, valueSet, connexity);

		/* *. Other examples */
//		int bandOfInterest = 0; // band of interest, 0 is the first band.
//		CooccurrenceMatrix<Double> cooccMat = new CooccurrenceMatrix<Double>(img, bandOfInterest, connexity, null);
//		The valueGetter parameter should to null if we just want to get pixel double values from each pixels
//		CooccurrenceMatrix<Double> cooccMat = new CooccurrenceMatrix<Double>(img, bandOfInterest, valueSet, connexity, null);
//		CooccurrenceMatrix<Double> cooccMat = new CooccurrenceMatrix<Double>(img, connexity);
//		CooccurrenceMatrix<Double> cooccMat = new CooccurrenceMatrix<Double>(img, bandOfInterest, valueSet, connexity);
		
		/* Print the coocc matrix as histogram here */
		cooccMat.print();
	}

	/**
	 * Just generate an image for the test.
	 * @return a specific generated image.
	 */
	private static BufferedImage generateImgExample(int width, int height, ArrayList<Double> values) {

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);		
		
		/* 1st column */
		img.setRGB(0, 0, genColor(7).getRGB());
		img.setRGB(0, 1, genColor(7).getRGB());
		img.setRGB(0, 2, genColor(8).getRGB());
		img.setRGB(0, 3, genColor(4).getRGB());
		img.setRGB(0, 4, genColor(4).getRGB());
		
		/* 2nd column */
		img.setRGB(1, 0, genColor(6).getRGB());
		img.setRGB(1, 1, genColor(0).getRGB());
		img.setRGB(1, 2, genColor(0).getRGB());
		img.setRGB(1, 3, genColor(0).getRGB());
		img.setRGB(1, 4, genColor(4).getRGB());
		
		/* 3rd column */
		img.setRGB(2, 0, genColor(5).getRGB());
		img.setRGB(2, 1, genColor(5).getRGB());
		img.setRGB(2, 2, genColor(8).getRGB());
		img.setRGB(2, 3, genColor(8).getRGB());
		img.setRGB(2, 4, genColor(6).getRGB());

		/* 4th column */
		img.setRGB(3, 0, genColor(3).getRGB());
		img.setRGB(3, 1, genColor(3).getRGB());
		img.setRGB(3, 2, genColor(4).getRGB());
		img.setRGB(3, 3, genColor(4).getRGB());
		img.setRGB(3, 4, genColor(8).getRGB());

		/* 5th column */
		img.setRGB(4, 0, genColor(0).getRGB());
		img.setRGB(4, 1, genColor(0).getRGB());
		img.setRGB(4, 2, genColor(3).getRGB());
		img.setRGB(4, 3, genColor(3).getRGB());
		img.setRGB(4, 4, genColor(1).getRGB());
		
		return img;
	}
	
	/**
	 * Just transform a gray scale value v into an RGB Color object.
	 * @param v gray scale value.
	 * @return a Color object.
	 */
	private static Color genColor(int v) {
		return  new Color(v, v, v);
	}
}
