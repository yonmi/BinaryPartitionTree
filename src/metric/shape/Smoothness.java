package metric.shape;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.Log;
import utils.Morphological;

/**
 * 
 * Metric based on the shape of the regions. Precisely, this metric focuses on the smoothness of the regions.
 * The computation of the distance between two regions is determined by their respective smoothness value.
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 * 
 * @author C. Kurtz
 *
 */
public class Smoothness extends Metric implements MetricInterface{

	/**
	 * Position of the smoothness value in the metric features (MF) list.
	 */
	int smoothPos = -1; 
	
	/**
	 * Registers an image within the metric and create the metric object based on the elongation of a region (~ NODE).
	 * @param image should not be null
	 */
	public Smoothness(BufferedImage image) {
		
		this.type = TypeOfMetric.SMOOTHNESS;
		this.img = image;
		
		/* define the position of the feature */
		this.smoothPos = ++Metric.currentFeaturePos;
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	/**
	 * Compute a distance between 'n1' and 'n2' using the Metric Features (MF):
	 * - smoothness: value associated with the smoothness of the region (~ node)</br>
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'
	 * (!) TODO - think again about it
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double score = 0.0;
	
		double smoothness1 = n1.features.get(this.smoothPos);
		double smoothness2 = n2.features.get(this.smoothPos);
		
		score =  Math.abs(smoothness2 - smoothness1);
		
		return score;
		
	}

	/**
	 * Computes a smoothness score of a list of Points with Mathematical Morphology
	 * 
	 * @param listOfPoints List of points forming the region (~ node)
	 * @param imgWidth Width of the image.
	 * @param imgHeight Height of the image.
	 * @return value between 0 and 1 associated with the smoothness of the region
	 */
	public static double computeSmoothnessWithMorpho(ArrayList<Point> listOfPoints,int imgWidth,int imgHeight) {	
	
		//------------Morpho
		Morphological morpho = new Morphological(imgWidth, imgHeight,5);
		double smoothness_morpho = morpho.morphologicalSmoothness(listOfPoints);
		return smoothness_morpho;
	}

	/**
	 * Prepares all the Metric Features (MF) corresponding to the elongation value of the specified region (~ node):</br>
	 * - elongation: value associated with the elongation shape of the region (~ node)</br>
	 * @param n Concerned node
	 */
	@Override
	public void initMF(Node n) {
		
		/* Nothing else to initiate */
	}

	/**
	 * Initiates or updates the values of the Metric Features (MF):</br>
	 *- smoothness: value associated with the smoothness of the region (~ node)</br>
	 * @param n concerned node; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void updateMF(Node n) {
		
		/*
		 * Compute region smoothness.
		 */
		double smoothness = Smoothness.computeSmoothnessWithMorpho(n.getPixels(), this.img.getWidth(), this.img.getHeight());
		
		/*
		 * Set or Update the node metric feature (~ MF).
		 */
		n.features.put(this.smoothPos, smoothness);
	}
}
