package metric.shape;

import java.awt.image.BufferedImage;
import java.util.TreeSet;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.Log;

/**
 * This class compute rapidly the smoothness values of each region (~ node). </br>
 *
 */
public class FastSmoothness extends Metric implements MetricInterface{

	/**
	 * Position of the smoothness value in the metric features (MF) list.
	 */
	int smoothPos;

	/**
	 * Registers an image within the metric and create the smoothness metric
	 * @param image should not be null
	 */
	public FastSmoothness(BufferedImage image) {
		
		this.type = TypeOfMetric.FAST_SMOOTHNESS;
		this.img = image;
		
		/* define the position of the feature */
		this.smoothPos = ++Metric.currentFeaturePos;
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	/**
	 * Computes a fast smoothness
	 * 
	 * @param borderLength Length of the border 
	 * @param boundingBoxEdgeLength Edge length the bounding box of the region
	 * @return
	 */
	public static double computeSmoothness(int borderLength, int boundingBoxEdgeLength) {
		
		return 1 - (((double) borderLength / boundingBoxEdgeLength) - 1);
	}
	
	/**
	 * Computes a distance between 'n1' and 'n2' using the Metric Features (MF)
	 * 
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
	
		double score = 0.0;
	
		double smoothness1 = n1.features.get(this.smoothPos);
		double smoothness2 = n2.features.get(this.smoothPos);
	
		double averageChildren =  (smoothness1 + smoothness2)/2.0;
		
		// Border points fake father.
		TreeSet<Integer> borderPoints = new TreeSet<Integer>();
		borderPoints.addAll(n1.borderPoints);
		borderPoints.addAll(n2.borderPoints);
		for(Integer p:n1.borderPoints){
			if(n2.borderPoints.contains(p)){
				borderPoints.remove(p);
			}
		}
		
		//Bounding box fake father
		// Boundig box
		int minX = Math.min(n1.boundingBox[0],n2.boundingBox[0]);
		int maxX = Math.max(n1.boundingBox[1], n2.boundingBox[1]);
		int minY = Math.min(n1.boundingBox[2], n2.boundingBox[2]);
		int maxY = Math.max(n1.boundingBox[3], n2.boundingBox[3]);
		int boundingBoxEdgeLength = 2*((maxX - minX)+(maxY - minY));
		if (boundingBoxEdgeLength == 0)
			boundingBoxEdgeLength=1;
		
		double smoothnessPotentialFather = FastSmoothness.computeSmoothness(borderPoints.size(), boundingBoxEdgeLength);
		
		score = Math.abs(smoothnessPotentialFather - averageChildren);
		
		return score;
	}

	/**
	 * Prepares all the Metric Features (MF) corresponding to the smoothness value of the specified region (~ node)</br>
	 * 
	 * @param n concerned node
	 */
	@Override
	public void initMF(Node n) {

		/* Nothing else to initiate */
	}

	/**
	 * Initiates or updates the smoothness values in the Metric Features list (MF)</br>
	 * @param n concerned node
	 */
	@Override
	public void updateMF(Node n) {

		// Bounding box
		int minX = n.boundingBox[0];
		int maxX = n.boundingBox[1];
		int minY = n.boundingBox[2];
		int maxY = n.boundingBox[3];
		
		/*
		 * Compute region smoothness.
		 */
		int boundingBoxEdgeLength = 2*((maxX - minX)+(maxY - minY));
		if (boundingBoxEdgeLength == 0)
			boundingBoxEdgeLength=1;
		
		double smoothness = FastSmoothness.computeSmoothness(n.borderPoints.size(), boundingBoxEdgeLength);
		
		/*
		 * Set or Update the node metric feature (~ MF).
		 */
		n.features.put(this.smoothPos, smoothness);
	}
}
