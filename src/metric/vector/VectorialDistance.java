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

package metric.vector;

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
 * Metric based on vectors containing locations (e.g.: x, y, z).
 * The computation of the distance between two regions requires each corresponding vector.
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 * 
 * <p>
 * <li> x: 1st band
 * <li> y: 2nd band
 * <li> z: 3rd band
 * <li> ...
 */
public class VectorialDistance extends Metric implements MetricInterface {
	
	public enum Context{
		
		VECTORIAL_DISTANCE
	}
	
	/**
	 * Indicates the location of the feature composed by the x, y, z positions
	 */
	ArrayList<Integer> pos;

	/**
	 * Registers an image within the metric and creates it
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public VectorialDistance(BufferedImage image) {
		
		this.type = TypeOfMetric.VECTORIAL_DISTANCE;
		this.img = image;
		
		/*
		 * Allocate spaces for the positions of the values in the feature list
		 */
		this.pos = new ArrayList<Integer>(ImTool.getNbBandsOf(this.img));
		
		/* Define the position of x, y and z. */
		for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
			
			this.pos.add(++Metric.currentFeaturePos);
		
		}
		
		Log.println(String.valueOf(Context.VECTORIAL_DISTANCE), "Metric prepared!");
	}
	
	/**
	 * Compute a distance between 'n1' and 'n2' using the Metric Features (MF) based on positions:
	 * 
	 * <li> x
	 * <li> y
	 * <li> z
	 * 
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double score = 0;

		/* pseudo euclidian distance */
		int nbBands = ImTool.getNbBandsOf(this.img);
		for(int b = 0; b < nbBands; ++b){
			
			double coord1 = n1.features.get(this.pos.get(b));
			double coord2 = n2.features.get(this.pos.get(b));
			score += Math.abs(coord1 - coord2);
		}
		return score;
	}

	/**
	 * Prepares all the features corresponding to the metric
	 * <li> x
	 * <li> y
	 * <li> z
	 * @param n Concerned node, should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void initMF(Node n) {

		for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b) {
			
			n.features.put(this.pos.get(b), 0.0);
		}
	}

	/**
	 * Initiates or updates the values of the Metric Features (MF) such as:</br>
	 * <li> x
	 * <li> y
	 * <li> z
	 * @param n Concerned node; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void updateMF(Node n) {

		int posb;
		switch(n.type){

			case LEAF:
				
				/* Define the position */
				for(Point point: n.getPixels()){

					for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
						
						posb = this.pos.get(b);

						double val = Math.max(n.features.get(posb), ImTool.getPixelValue(point.x, point.y, b, this.img));
//						double val = n.features.get(posb) + ImTool.getPixelValue(point.x, point.y, b, this.img);
						System.out.println(b +") "+ ImTool.getPixelValue(point.x, point.y, b, this.img));
						n.features.put(posb, val);
					}
				}
				break;
				
			default: 

				for(int b = 0; b < ImTool.getNbBandsOf(this.img); ++b){
					
					posb = this.pos.get(b);
//					n.features.put(posb, n.leftNode.features.get(posb) + n.rightNode.features.get(posb));
					n.features.put(posb, Math.max(n.leftNode.features.get(posb), n.rightNode.features.get(posb)));
				}
		}
	}
}
