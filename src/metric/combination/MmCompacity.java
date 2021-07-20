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

package metric.combination;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeSet;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import metric.shape.FastCompactness;
import utils.ImTool;
import utils.Log;

/**
 * Linear combination of two metrics : radiometric(mm) + compacity.
 *
 */
public class MmCompacity extends Metric implements MetricInterface{

	/**
	 * Indicates, for each band of the image, the location of the max feature for all nodes
	 */
	ArrayList<Integer> maxPos;

	/**
	 * Indicates, for each band of the image, the location of the min feature for all nodes
	 */
	ArrayList<Integer> minPos;

	
	/**
	 * Position of the compactness value in the metric features (MF) list.
	 */
	int compactPos;
	
	/**
	 * Registers an image within the metric and creates a similarity metric based on a linear combination of:
	 * 
	 * <li> RADIOMETRIC_MIN_MAX
	 * <li> COMPACITY
	 * 
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public MmCompacity(BufferedImage image){
		
		this.type = TypeOfMetric.CL_MM_COMPACTNESS;
		this.img = image;
				
		/* Allocate spaces for the 'minPos' and 'maxPos' array lists. */
		int nbBands = ImTool.getNbBandsOf(this.img);
		this.minPos = new ArrayList<Integer>(nbBands);
		this.maxPos = new ArrayList<Integer>(nbBands);
		
		/* Define the minimum pixel value position in the list of MF. */
		for(int b = 0; b < nbBands; ++b){
			
			this.minPos.add(++Metric.currentFeaturePos);
			this.maxPos.add(++Metric.currentFeaturePos);
		}

		/* define the position of the feature */
		this.compactPos = ++Metric.currentFeaturePos;
		
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
				
				/**
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
	
			/* compactness score */
			double compactness1 = n1.features.get(this.compactPos);
			double compactness2 = n2.features.get(this.compactPos);
		
			double averageChildren = (compactness1 + compactness2)/2.0;
				
			int sizeFakeFather = n1.getSize() + n2.getSize();
			
			// Border points fake father.
			TreeSet<Integer> borderPoints = new TreeSet<Integer>();
			borderPoints.addAll(n1.borderPoints);
			borderPoints.addAll(n2.borderPoints);
			for(Integer p:n1.borderPoints){
				if(n2.borderPoints.contains(p)){
					borderPoints.remove(p);
				}
			}
			
			double compactnessPotentialFather = FastCompactness.computeCompactness(sizeFakeFather, borderPoints.size());
			
			double compacityScore = Math.abs(compactnessPotentialFather - averageChildren);
	
			/* final score */
			score = normalizedRadiometricScore + compacityScore;
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
		 * Update / Compute region compactness.
		 */
		double compactness = FastCompactness.computeCompactness(n.getSize(), n.borderPoints.size());
		
		/*
		 * Set or Update the node metric feature (~ MF).
		 */
		n.features.put(this.compactPos, compactness);
	}	
}
