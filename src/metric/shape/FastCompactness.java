package metric.shape;

import java.awt.image.BufferedImage;
import java.util.TreeSet;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import metric.bricks.ToolsMetric;
import utils.Log;

/**
 * This class compute rapidly the compactness values of each region (~ node). </br>
 *
 */
public class FastCompactness extends Metric implements MetricInterface{

	/**
	 * Position of the compactness value in the metric features (MF) list
	 */
	int compactPos;

	/**
	 * Registers an image within the metric and create the compactness metric
	 * @param image should not be null
	 */
	public FastCompactness(BufferedImage image) {
		
		this.type = TypeOfMetric.FAST_COMPACTNESS;
		this.img = image;
		
		/* define the position of the feature */
		this.compactPos = ++Metric.currentFeaturePos;
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	/**
	 * Computes a fast compactness
	 * @param size Size of the region in terms of pixel.
	 * @param borderLength Length of the border. 
	 * @return a compactness 
	 */
	public static double computeCompactness(int size, int borderLength) {
		
		return borderLength / Math.sqrt(size);
	}
	
	/**
	 * Computes a distance between 'n1' and 'n2' using the Metric Features (MF)
	 * 
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'.
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
	
		double score = 0.0;
	
		double compactness1 = n1.features.get(this.compactPos);
		double compactness2 = n2.features.get(this.compactPos);
	
		double averageChildren = (compactness1 + compactness2)/2.0;//((compactness1 * n1.points.size()) + (compactness2 * n2.points.size())) / (n1.points.size() + n2.points.size());
			
		int sizeFakeFather = n1.getSize() + n2.getSize();
		
		// Border points fake father.
		TreeSet<Integer> borderPoints = new TreeSet<Integer>();
		borderPoints.addAll(n1.borderPoints);
		borderPoints.addAll(n2.borderPoints);
		for(Integer p:n1.borderPoints){
			if(n2.borderPoints.contains(p)){
				borderPoints.remove(p);
			}
		}
		
		double compactnessPotentialFather = FastCompactness.computeCompactness(sizeFakeFather, borderPoints.size());
		
		score = Math.abs(compactnessPotentialFather - averageChildren);
		
		return score;
	}

	/**
	 * Prepares all the Metric Features (MF) corresponding to the compactness value of the specified region (~ node).</br>
	 * @param n Concerned node.
	 */
	@Override
	public void initMF(Node n) {

		/* Nothing else to intiate */
	}

	/**
	 * Initiates or updates the compactness values in the Metric Features list (MF). </br>
	 * @param n Concerned node.
	 */
	@Override
	public void updateMF(Node n) {
		try {
		ToolsMetric.computeRegionBoundingBox(n, this.img.getWidth(), this.img.getHeight());
		ToolsMetric.computeBorderPixels(n, this.img.getWidth(), this.img.getHeight());
		}catch(Exception e) {e.printStackTrace();}
		/*
		 * Compute region compactness.
		 */
		double compactness = FastCompactness.computeCompactness(n.getSize(), n.borderPoints.size());
		
		/*
		 * Set or Update the node metric feature (~ MF).
		 */
		n.features.put(this.compactPos, compactness);
	}
}
