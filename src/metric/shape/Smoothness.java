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
