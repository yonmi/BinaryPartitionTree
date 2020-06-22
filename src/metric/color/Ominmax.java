package metric.color;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.ImTool;
import utils.Log;

/**
 * 
 * Metric based on the radiometric intensity of each region (combination of pixels).
 * The computation of the distance between two regions requires the minimum and the maximum values of the whole pixels of the image and among the channels (~ bands).
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 * 
 */
public class Ominmax extends Metric implements MetricInterface {
	
	public enum Context{
		
		RADIOMETRIC_MIN_MAX
	}
	
	/**
	 * Indicates, for each band of the image, the location of the max feature for all nodes
	 */
	ArrayList<Integer> maxPos;

	/**
	 * Indicates, for each band of the image, the location of the min feature for all nodes
	 */
	ArrayList<Integer> minPos;

	/**
	 * Registers an image within the metric and creates the radiometric object based on the min and the max values of the pixels.
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public Ominmax(BufferedImage image) {
		
		this.type = TypeOfMetric.OMIN_MAX;
		this.img = image;
		
		/**
		 * Allocate spaces for the 'minPos' and 'maxPos' array lists.
		 */
		this.minPos = new ArrayList<Integer>(ImTool.getNbBandsOf(this.img));
		this.maxPos = new ArrayList<Integer>(ImTool.getNbBandsOf(this.img));
		
		/* Define the minimum pixel value position in the list of MF. */
		for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
			
			this.minPos.add(++Metric.currentFeaturePos);
			this.maxPos.add(++Metric.currentFeaturePos);
		
		}
		Log.println(String.valueOf(Context.RADIOMETRIC_MIN_MAX), "Metric prepared!");
	}
	
	/**
	 * Compute a distance between 'n1' and 'n2' using the Metric Features (MF) such as:
	 * - min: minimum value of the pixels of the region among the channels (~ bands).
	 * - max: maximum value of the pixels of the among the channels (~ bands).
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double score = 0;
		double miniMini, maxiMaxi;
	
		int nbBands = ImTool.getNbBandsOf(this.img);
		for(int b = 0; b < nbBands; ++b){
			
			/*
			 * Sum the differences between the max of max and the min of min of each channel (~ band).
			 */
			miniMini = Math.min(n1.features.get(this.minPos.get(b)), n2.features.get(this.minPos.get(b)));
			maxiMaxi = Math.max(n1.features.get(this.maxPos.get(b)), n2.features.get(this.maxPos.get(b)));
			score += Math.abs(maxiMaxi - miniMini);
		}
		
		return score;
	}

	/**
	 * Prepares all the Metric Features (MF) corresponding to the radiometric intensity of the specified regions (~ node) such as:</br>
	 * - min: minimum value of the pixels of the region among the channels (~ bands).</br>
	 * - max: maximum value of the pixels of the among the channels (~ bands).</br>
	 * @param n Concerned node, should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void initMF(Node n) {

		/* - Initialize the minimum value with the possible maximum value of double. */
		int nbBands = ImTool.getNbBandsOf(this.img);
		for(int b = 0; b < nbBands; ++b){
					
			n.features.put(this.minPos.get(b), Double.MAX_VALUE);
			n.features.put(this.maxPos.get(b), Double.MIN_VALUE);
		}
	}

	/**
	 * Initiates or updates the values of the Metric Features (MF) such as:</br>
	 * - min: minimum value of the pixels of the region among the channels (~ bands).</br>
	 * - max: maximum value of the pixels of the among the channels (~ bands).</br>
	 * @param n Concerned node; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void updateMF(Node n) {
		
		int minPosb;
		int maxPosb;
		switch(n.type){
		
			case LEAF: /* GET THE MIN AND MAX FOR EACH CHANNEL (~ BAND). */
				
				double pixelValue;
				for(Point point: n.getPixels()){
					for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
						
						minPosb = this.minPos.get(b);
						pixelValue = ImTool.getPixelValue(point.x, point.y, b, this.img);
						if(n.features.get(minPosb) > pixelValue){
							
							n.features.put(minPosb, pixelValue);

						}
						maxPosb = this.maxPos.get(b);
						if(n.features.get(maxPosb) < pixelValue){
							
							n.features.put(maxPosb, pixelValue);
							
						}
					}
				}
				break;
				
			default: /* GET THE MIN OF MIN AND THE MAX OF MAX OF THE VALUES BETWEEN THE TWO DIRECT SUB-REGIONS (CHILDREN) */
				for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
					
					minPosb = this.minPos.get(b);
					n.features.put(minPosb, Math.min(n.leftNode.features.get(minPosb), n.rightNode.features.get(minPosb)));

					maxPosb = this.maxPos.get(b);
					n.features.put(maxPosb, Math.max(n.leftNode.features.get(maxPosb), n.rightNode.features.get(maxPosb)));
				}
		}
	}
}
