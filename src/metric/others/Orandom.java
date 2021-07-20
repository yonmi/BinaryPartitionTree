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

package metric.others;

import java.awt.image.BufferedImage;
import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.Log;

/**
 * 
 * Metric based on random values.
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 * 
 */
public class Orandom extends Metric implements MetricInterface {
	
	public enum Context{
		
		RANDOM_M
	}
	
	/**
	 * Position of the random feature in the list of MF.
	 */
	public int randPos;
	
	/**
	 * Registers an image within the metric and creates the random metric values.
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public Orandom(BufferedImage image) {
		
		this.type = TypeOfMetric.ORANDOM;
		this.img = image;
		
		randPos = ++currentFeaturePos;
		Log.println(String.valueOf(Context.RANDOM_M), "Metric prepared!");
	}
	
	/**
	 * Generates a random metric value.
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return random score
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double res = Math.abs(n1.features.get(this.randPos) - n2.features.get(this.randPos)); 
//		System.out.println("random value: "+ res +"\n");
		return res;
	}

	/**
	 * Generates a random feature value and initializes the feature value.
	 * @param n Concerned node, should not be null
	 * 
	 */
	@Override
	public void initMF(Node n) {

		double randVal = Math.random();
		n.features.put(this.randPos, randVal);
	}

	/**
	 * Generates a random feature value and updates the previous value with it.
	 * @param n Concerned node; should not be null
	 * 
	 */
	@Override
	public void updateMF(Node n) {
	
		double randVal = Math.random();
		n.features.put(this.randPos, randVal);
	}
}
