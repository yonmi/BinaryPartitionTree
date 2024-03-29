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
import java.util.ArrayList;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.ImTool;
import utils.Log;

/**
 * 
 * Metric based on the radiometric intensity of each region (combination of pixels) of each region.
 * The computation of the distance between two regions requires the average of the intensity values of the whole pixels of the image and among the channels (~ bands).
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 *
 */
public class RadiometricAverage extends Metric implements MetricInterface{

	/**
	 * Location of the average color feature in each node for each band.
	 */
	ArrayList<Integer> avgPos = new ArrayList<Integer>();
	
	/**
	 * Register an image within the metric and create the radiometric object based on the average values of the pixels.
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public RadiometricAverage(BufferedImage image) {
		
		this.type = TypeOfMetric.RADIOMETRIC_AVERAGE;
		this.img = image;

		/* - Define the minimum pixel value position in the list of MF.
		 * - Initialize the minimum value with the possible maximum value of double.
		 */
		this.avgPos = new ArrayList<Integer>(ImTool.getNbBandsOf(this.img));
		for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
			
			this.avgPos.add(++Metric.currentFeaturePos);
		
		}
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	/**
	 * Compute a distance between 'n1' and 'n2' using the Metric Features (MF):
	 * - average: average value of the pixels of the region among the channels (~ bands).
	 * @param n1 First Node, should not be null
	 * @param n2 Second Node, should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'.
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double score = 0;
	
		for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
			
			/*
			 * Compute the difference between the two nodes (~ regions) and to the sum of all values of each channel (~ band).
			 */
			int avgPosb = this.avgPos.get(b);
			score += Math.abs(n2.features.get(avgPosb) - n1.features.get(avgPosb));
		}
		return score;
	}

	/**
	 * Prepare all the Metric Features (MF) corresponding to the radiometric intensity of the specified region (~ node):</br>
	 * - average: average of the pixels of the region among the channels (~ bands).</br>
	 * @param n concerned node
	 */
	@Override
	public void initMF(Node n) {

	}

	/**
	 * Initiate or update the values of the Metric Features (MF):</br>
	 * - average: average value of the pixels of the region among the channels (~ bands).</br>
	 * @param n Concerned node; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void updateMF(Node n) {
		
		switch(n.type){
		
			case LEAF: /* GET THE AVERAGE PIXEL VALUE FOR EACH CHANNEL (~ BAND). */
				for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
					
					int avgPosb = this.avgPos.get(b);
					double sumPixelValues = 0.0;
					for(Point point: n.getPixels()){
						
						sumPixelValues += ImTool.getPixelValue(point.x, point.y, b, this.img);
						
					}
					n.features.put(avgPosb, sumPixelValues);
					
				}
				break;
				
			default: /* COMPUTE THE AVERAGE PIXEL VALUES BETWEEN THE TWO DIRECT SUB-REGIONS (CHILDREN) */
				for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
					
					int avgPosb = this.avgPos.get(b);
					Node leftChild = n.leftNode;
					Node rightChild = n.rightNode;
					double radiometricAvg = ((leftChild.features.get(avgPosb) * leftChild.getSize())
							+ (rightChild.features.get(avgPosb) * rightChild.getSize()))
							/ (leftChild.getSize() + rightChild.getSize());
					
					n.features.put(avgPosb, radiometricAvg);
				}
		}
	}
}
