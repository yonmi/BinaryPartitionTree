package metric.combination;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import metric.shape.Elongation;
import utils.ImTool;
import utils.Log;

/**
 * Linear combination of two metrics : radiometric(mm) + elongation + ndvi + ndwi.
 *
 */
public class MmFelNdviNdwi extends Metric implements MetricInterface{

	/**
	 * Position of the elongation feature for all nodes
	 */	
	int elongPos = -1;

	/**
	 * Identification of the Green band
	 */
	int gindex;

	/**
	 * Indicates, for each band of the image, the location of the max feature for all nodes
	 */
	ArrayList<Integer> maxPos;

	/**
	 * Indicates, for each band of the image, the location of the min feature for all nodes
	 */
	ArrayList<Integer> minPos;

	/**
	 * Position of the ndvi in the metric features (MF) list.
	 */
	int ndviPos = -1;
	
	/**
	 * Position of the ndwi in the metric features (MF) list.
	 */
	int ndwiPos = -1;

	/**
	 * Identification of the NIR band
	 */
	int nirindex;

	/**
	 * Identification of the Red band
	 */
	int rindex;
	
	/**
	 * Registers an image within the metric and creates a similarity metric based on a linear combination of:
	 * 
	 * <li> RADIOMETRIC_MIN_MAX
	 * <li> ELONGATION
	 * <li> NDVI
	 * <li> NDWI
	 * 
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public MmFelNdviNdwi(BufferedImage image){
		
		this.type = TypeOfMetric.CL_MM_FEL_NDVI_NDWI;
		this.img = image;
		
		/*
		 * RADIOMETRIC
		 */
		
		/* Allocate spaces for the 'minPos' and 'maxPos' array lists. */
		int nbBands = ImTool.getNbBandsOf(this.img);
		this.minPos = new ArrayList<Integer>(nbBands);
		this.maxPos = new ArrayList<Integer>(nbBands);
		
		/* Define the minimum pixel value position in the list of MF. */
		for(int b = 0; b < nbBands; ++b){
			
			this.minPos.add(++Metric.currentFeaturePos);
			this.maxPos.add(++Metric.currentFeaturePos);
		}
		
		/*
		 * ELONGATION
		 */
		
		/* define the position of the feature */
		this.elongPos = ++Metric.currentFeaturePos;
		
		/*
		 * NDVI
		 */
		
		/* (!) For this prototype, the R and NIR bands are not accurate. */
		this.rindex = 0;
		this.nirindex = 0;
		if(nbBands > 1){
			
			this.rindex = 0;
			this.nirindex = nbBands -1;
		}
		this.ndviPos = ++Metric.currentFeaturePos;
		
		/*
		 * NDWI
		 */
		
		/* (!) For this prototype, the G and NIR bands are not accurate. */
		this.gindex = 0;
		this.nirindex = 0;
		if(nbBands > 1){
			
			this.gindex = 1;
			this.nirindex = nbBands -1;
		}
		
		/* define the position of the feature */
		this.ndwiPos = ++Metric.currentFeaturePos;
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}

	@Override
	public double computeDistances(Node n1, Node n2) {

		double score = 0;
		double radiometricScore = 0;
		double miniMini, maxiMaxi;

		/* normalize radiometric score */ 
		double normalizedRadiometricScore = 0;
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		double bscore = 0;
		for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){

			/*
			 * Sum the differences between the max of max and the min of min of each channel (~ band).
			 */
			miniMini = Math.min(n1.features.get(this.minPos.get(b)), n2.features.get(this.minPos.get(b)));
			maxiMaxi = Math.max(n1.features.get(this.maxPos.get(b)), n2.features.get(this.maxPos.get(b)));
			bscore = Math.abs(maxiMaxi - miniMini);			
			radiometricScore += bscore;

			if(min > bscore)
				min = bscore;
			if(max < bscore)
				max = bscore;

		}		
		if(radiometricScore > min)
			normalizedRadiometricScore = (radiometricScore - min) / (max - min);
		else normalizedRadiometricScore = radiometricScore;
		normalizedRadiometricScore /= ImTool.getNbBandsOf(this.img);

		/*
		 *  elongation Score
		 *  
		 */
		double elongation1 = n1.features.get(this.elongPos);
		double elongation2 = n2.features.get(this.elongPos);

		double averageChildren =  (elongation1 + elongation2)/2.0;
		//double averageChildren =  (n1.getSize() * elongation1 + n2.getSize() * elongation2)/(n1.getSize() + n2.getSize() );

		ArrayList<Point> pointsFakeFather = new ArrayList<Point>();
		pointsFakeFather.addAll(n1.getPixels());
		pointsFakeFather.addAll(n2.getPixels());

		//Bounding box fake father
		int [] boundingBox = new int[4];
		boundingBox[0] = Math.min(n1.boundingBox[0],n2.boundingBox[0]);
		boundingBox[1] = Math.max(n1.boundingBox[1], n2.boundingBox[1]);
		boundingBox[2] = Math.min(n1.boundingBox[2], n2.boundingBox[2]);
		boundingBox[3] = Math.max(n1.boundingBox[3], n2.boundingBox[3]);

		double elongationpotentialFather = Elongation.computeElongation(this.type, pointsFakeFather,boundingBox,this.img.getWidth(), this.img.getHeight());

		double elongScore = Math.abs(elongationpotentialFather - averageChildren) / 2;

		/* ndvi score */
		double ndviScore = Math.abs(n1.features.get(this.ndviPos) - n2.features.get(this.ndviPos)) / 2;

		/* ndwi score */
		double ndwiScore = Math.abs(n1.features.get(this.ndwiPos) - n2.features.get(this.ndwiPos)) / 2;

		/* final score */
		score = (normalizedRadiometricScore + ndviScore + ndwiScore) * 95 / 100 + elongScore * 5 / 100;			
		return score;
	}

	@Override
	public void initMF(Node n) {
		
		/* - Initialize the minimum value with the possible maximum value of double. */ 
		for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
					
			n.features.put(this.minPos.get(b), Double.MAX_VALUE); // ADDING FICTIONNAL INITIAL MINIMUM VALUE.
			n.features.put(this.maxPos.get(b), Double.MIN_VALUE); // ADDING FICTIONNAL INITIAL MAXIMUM VALUE.
		
		}
	}

	@Override
	public void updateMF(Node n) {
		
		/*
		 * Update the radiometric features.
		 */
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
		
		/*
		 * Update the elongation features.
		 */
		double elongation = Elongation.computeElongation(this.type, n.getPixels(), n.boundingBox, this.img.getWidth(), this.img.getHeight()); /* Compute region elongation.*/
		n.features.put(this.elongPos, elongation); /* Set or Update the node metric feature (~ MF) */
		
		/*
		 * Update the ndvi features.
		 */
		double ndvi; /* NDVI = (NIR - R) / (NIR + R) */
		double r; // value on the Red band.
		double nir; // value on the NIR band.
		
		switch(n.type){

		/* Compute the NDVI value corresponding to the region (~ node). */
		case LEAF: 

			double meanR = 0.0; // mean of the Red pixels values of the region.
			double meanNIR = 0.0; // mean of the NIR pixels values of the region.
			for(Point point: n.getPixels()){

				int x = point.x;
				int y = point.y;
				r = ImTool.getPixelValue(x, y, this.rindex, this.img);
				nir = ImTool.getPixelValue(x, y, this.nirindex, this.img);
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
		
		/*
		 * Update the ndwi features.
		 */
		double ndwi; /* NDWI = (G - NIR) / (G + NIR) */
		double g; // value on the Green band.
		
		switch(n.type){

		/* Compute the NDVI value corresponding to the region (~ node). */
		case LEAF: 

			double meanG = 0.0; // mean of the Red pixels values of the region.
			double meanNIR = 0.0; // mean of the NIR pixels values of the region.
			for(Point point: n.getPixels()){

				int x = point.x;
				int y = point.y;
				g = ImTool.getPixelValue(x, y, this.gindex, this.img);
				nir = ImTool.getPixelValue(x, y, this.nirindex, this.img);
				meanG += g;
				meanNIR += nir;
				
			}
			meanG /= n.getSize();
			meanNIR /= n.getSize();
			
			ndwi = (meanG - meanNIR) / (meanG + meanNIR);
			n.features.put(this.ndwiPos, ndwi);

			break;
		default: // node case.
			ndwi = (n.leftNode.features.get(this.ndwiPos) * n.leftNode.getSize() + n.rightNode.features.get(this.ndwiPos) * n.rightNode.getSize()) / (n.leftNode.getSize() +n.rightNode.getSize()) ;
			n.features.put(this.ndwiPos, ndwi);
		}
	}
}
