/****************************************************************************
* Copyright AGAT-Team (2014)						       
* 									    
* Contributors:								
* J.F. Randrianasoa							    
* K. Kurtz								    
* E. Desjardin								    
* N. Passat								    
* 									    
* This software is a computer program whose purpose is to [describe	    
* functionalities and technical features of your software].		    
* 									    
* This software is governed by the CeCILL-B license under French law and    
* abiding by the rules of distribution of free software.  You can  use,     
* modify and/ or redistribute the software under the terms of the CeCILL-B  
* license as circulated by CEA, CNRS and INRIA at the following URL	    
* "http://www.cecill.info". 						    
* 									    
* As a counterpart to the access to the source code and  rights to copy,    
* modify and redistribute granted by the license, users are provided only   
* with a limited warranty  and the software's author,  the holder of the    
* economic rights,  and the successive licensors  have only  limited	    
* liability. 								    
* 									    
* In this respect, the user's attention is drawn to the risks associated    
* with loading,  using,  modifying and/or developing or reproducing the     
* software by the user in light of its specific status of free software,    
* that may mean  that it is complicated to manipulate,  and  that  also	   
* therefore means  that it is reserved for developers  and  experienced     
* professionals having in-depth computer knowledge. Users are therefore     
* encouraged to load and test the software's suitability as regards their   
* requirements in conditions enabling the security of their systems and/or  
* data to be ensured and,  more generally, to use and operate it in the     
* same conditions as regards security. 					    
*								            
* The fact that you are presently reading this means that you have had	    
* knowledge of the CeCILL-B license and that you accept its terms.          
* 									   		
* The full license is in the file LICENSE, distributed with this software.  
*****************************************************************************/

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
