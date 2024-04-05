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

package metric.color.d3;

import java.util.ArrayList;

import datastructure.Node;
import metric.bricks.MetricInterface;
import metric.bricks.d3.Metric3D;
import utils.Log;
import utils.d3.RGBStruct;
import utils.d3.Voxel;

/**
 * 
 * Metric based on the radiometric intensity of each region (combination of pixels).
 * The computation of the distance between two regions requires the minimum and the maximum values of the whole pixels of the image and among the channels (~ bands).
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 * 
 */
public class Ominmax3D extends Metric3D implements MetricInterface {
	
	private int nbBands;
	
	public enum Context{
		
		OMIN_MAX_3D
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
	public Ominmax3D(RGBStruct cube) {
		
		this.type = TypeOfMetric.OMIN_MAX_3D;
		this.cube = cube;
		this.nbBands = 3;
		
		/**
		 * Allocate spaces for the 'minPos' and 'maxPos' array lists.
		 */
		this.minPos = new ArrayList<Integer>(this.nbBands);
		this.maxPos = new ArrayList<Integer>(this.nbBands);
		
		/* Define the minimum pixel value position in the list of MF. */
		for(int b = 0; b < this.nbBands; ++b){
			
			this.minPos.add(++Metric3D.currentFeaturePos);
			this.maxPos.add(++Metric3D.currentFeaturePos);
		
		}
		Log.println(String.valueOf(Context.OMIN_MAX_3D), "Metric prepared!");
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
	
		for(int b = 0; b < this.nbBands; ++b){
			
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
		for(int b = 0; b < this.nbBands; ++b){
					
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
				
				double voxelColorValue;
				for(Voxel voxel: n.getVoxels()){
					for(int b = 0; b < this.nbBands; ++b){
						
						minPosb = this.minPos.get(b);
						voxelColorValue = cube.getGrayValueOfBand(b, voxel.x, voxel.y, voxel.z);
						if(n.features.get(minPosb) > voxelColorValue){
							
							n.features.put(minPosb, voxelColorValue);

						}
						maxPosb = this.maxPos.get(b);
						if(n.features.get(maxPosb) < voxelColorValue){
							
							n.features.put(maxPosb, voxelColorValue);
							
						}
					}
				}
				break;
				
			default: /* GET THE MIN OF MIN AND THE MAX OF MAX OF THE VALUES BETWEEN THE TWO DIRECT SUB-REGIONS (CHILDREN) */
				for(int b = 0; b < this.nbBands; ++b){
					
					minPosb = this.minPos.get(b);
					n.features.put(minPosb, Math.min(n.leftNode.features.get(minPosb), n.rightNode.features.get(minPosb)));

					maxPosb = this.maxPos.get(b);
					n.features.put(maxPosb, Math.max(n.leftNode.features.get(maxPosb), n.rightNode.features.get(maxPosb)));
				}
		}
	}
}
