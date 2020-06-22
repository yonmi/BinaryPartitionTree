package metric.color;

import java.awt.Point;
import java.awt.image.BufferedImage;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.ImTool;
import utils.Log;

/**
 * This class computes NDWI values of each region (~ node). </br>
 * The NDWI is a normalized water index for remote sensing.
 *
 */
public class NdwiMetric extends Metric implements MetricInterface{

	/**
	 * Identification of the Green band
	 */
	int gindex;

	/**
	 * Position of the ndwi in the metric features (MF) list.
	 */
	int ndwiPos = -1;
	
	/**
	 * Identification of the NIR band
	 */
	int nirindex;
	
	/**
	 * Register an image within the metric and create the ndwi metric based on the difference-sum ratio between the Near Infrared Red and the Green bands.
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public NdwiMetric(BufferedImage image) {
		
		this.type = TypeOfMetric.NDWI;
		this.img = image;
		
		/*
		 * (!) For this prototype, the G and NIR bands are not accurate.
		 */
		this.gindex = 0;
		this.nirindex = 0;
		if(ImTool.getNbBandsOf(this.img) > 1){
			this.gindex = 1;
			this.nirindex = ImTool.getNbBandsOf(this.img) -1;
		}
		
		/* define the position of the feature */
		this.ndwiPos = ++Metric.currentFeaturePos;
		
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
	
		return Math.abs(n1.features.get(this.ndwiPos) - n2.features.get(this.ndwiPos));
	}

	/**
	 * Prepare all the Metric Features (MF) corresponding to the NDWI value of the specified region (~ node).</br>
	 * @param n Concerned node
	 */
	@Override
	public void initMF(Node n) {
		
		/* Nothing else to initiate */
	}

	/**
	 * Initiate or update the NDWI values in the Metric Features list (MF). </br>
	 * (!) For this prototype, let's assume that the G band index and the NIR band index are respectively the second and the last bands.
	 * @param n Concerned node; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void updateMF(Node n) {

		double ndwi; /* NDWI = (G - NIR) / (G + NIR) */
		double g; // value on the Green band.
		double nir; // value on the NIR band.
		
		switch(n.type){

		/* Compute the NDVI value corresponding to the region (~ node). */
		case LEAF: 

			double meanG = 0.0; // mean of the Red pixels values of the region.
			double meanNIR = 0.0; // mean of the NIR pixels values of the region.
			for(Point point: n.getPixels()){

				g = ImTool.getPixelValue(point.x, point.y, this.gindex, this.img);
				nir =  ImTool.getPixelValue(point.x, point.y, this.nirindex, this.img);
				meanG += g;
				meanNIR += nir;
				
			}
			meanG /= n.getSize();
			meanNIR /= n.getSize();
			
			ndwi = (meanG - meanNIR) / (meanG + meanNIR);
			n.features.put(this.ndwiPos, ndwi);

			break;
		default: // node case.
			ndwi = (n.leftNode.features.get(this.ndwiPos) * n.leftNode.getSize() + n.rightNode.features.get(this.ndwiPos) * n.rightNode.getSize()) / (n.leftNode.getSize() +n.rightNode.getSize());
			n.features.put(this.ndwiPos, ndwi);
		}
	}
}
