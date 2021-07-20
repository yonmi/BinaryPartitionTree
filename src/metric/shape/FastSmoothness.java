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
