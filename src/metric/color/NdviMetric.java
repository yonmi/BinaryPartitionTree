package metric.color;

import java.awt.Point;
import java.awt.image.BufferedImage;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.ImTool;
import utils.Log;

/**
 * This class computes NDVI values of each region (~ node). </br>
 * The NDVI value is between -1 (no vegetation) and 1 (full of vegetation).
 *
 */
public class NdviMetric extends Metric implements MetricInterface{

	/**
	 * Position of the ndvi in the metric features (MF) list.
	 */
	int ndviPos = -1;
	
	/**
	 * Identification of the NIR band
	 */
	int nirindex;
	
	/**
	 * Identification of the Red band
	 */
	int rindex;

	/**
	 * Register an image within the metric and create the ndvi metric based on the difference-sum ratio between the Red and the Near Infrared Red bands.
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	public NdviMetric(BufferedImage image) {
		
		this.type = TypeOfMetric.NDVI;
		this.img = image;
		
		/*
		 * (!) For this prototype, the R and NIR bands are not accurate.
		 */
		this.rindex = 0;
		this.nirindex = 0;
		if(ImTool.getNbBandsOf(this.img) > 1){
			this.rindex = 0;
			this.nirindex = ImTool.getNbBandsOf(this.img) -1;
		}
		
		/* define the position of the feature */
		this.ndviPos = ++Metric.currentFeaturePos;
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	/**
	 * Compute a distance between 'n1' and 'n2' using the Metric Features (MF).
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'.
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
	
		return Math.abs(n1.features.get(this.ndviPos) - n2.features.get(this.ndviPos));
	
	}

	/**
	 * Prepare all the Metric Features (MF) corresponding to the NDVI value of the specified region (~ node).</br>
	 * @param n concerned node
	 */
	@Override
	public void initMF(Node n) {

		/* Nothing else to initiate */
	}

	/**
	 * Initiate or update the NDVI values in the Metric Features list (MF). </br>
	 * (!) For this prototype, let's assume that the R band index and the NIR band index are respectively the first and the last bands.
	 * @param n Concerned node; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void updateMF(Node n) {

		double ndvi; /* NDVI = (NIR - R) / (NIR + R) */
		double r; // value on the Red band.
		double nir; // value on the NIR band.
		
		switch(n.type){

		/* Compute the NDVI value corresponding to the region (~ node). */
		case LEAF: 

			double meanR = 0.0; // mean of the Red pixels values of the region.
			double meanNIR = 0.0; // mean of the NIR pixels values of the region.
			for(Point point: n.getPixels()){

				r = ImTool.getPixelValue(point.x, point.y, rindex, this.img);
				nir = ImTool.getPixelValue(point.x, point.y, this.nirindex, this.img);
				meanR += r;
				meanNIR += nir;
				
			}
			meanR /= n.getSize();
			meanNIR /= n.getSize();
			
			ndvi = (meanNIR - meanR) / (meanNIR + meanR);
			n.features.put(this.ndviPos, ndvi);

			break;
			
		default: // node case.
			
			ndvi = (n.leftNode.features.get(this.ndviPos) * n.leftNode.getSize() + n.rightNode.features.get(this.ndviPos) * n.rightNode.getSize()) / (n.leftNode.getSize() +n.rightNode.getSize()) ;
			n.features.put(this.ndviPos, ndvi);
		}
	}
}
