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
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import datastructure.Adjacency;
import datastructure.Node;
import datastructure.Node.TypeOfNode;
import datastructure.Tree.TypeOfConnectivity;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.Log;
import utils.d2.cooccurrence.CooccurrenceMatrix;
import utils.d2.cooccurrence.Couple;

/**
 * Metric based on the current value of the adjacency initially got from a cooccurrence matrix.
 * To update the new adjacency value, sum the two previous one.
 */
public class CooccurrenceMatrixMetric<T> extends Metric implements MetricInterface {

	/**
	 * Basing on the occurrence number of couple of values.
	 */
	private CooccurrenceMatrix<T> cooccMatrix;
	
	public enum Context{
		
		COOCC_METRIC
	}
	
	/**
	 * Position of the feature in the list of MF.
	 * Here, the attribute corresponds to the point value so double.
	 */
	public int attributePos;
	
	/**
	 * Registers an image within the metric and creates the random metric values.
	 * TODO - int connexity is not safe
	 */
	@SuppressWarnings("unchecked")
	public CooccurrenceMatrixMetric(ArrayList<T> valueSet, BufferedImage image, int connexity) {
		
		this.type = TypeOfMetric.COOCC_MATRIX;
		
		/* get a cooccurrence Matrix */
		this.cooccMatrix = new CooccurrenceMatrix<T>(image, valueSet, connexity);

		/* Print the coocc matrix - histogram here */
		this.cooccMatrix.print();
		System.out.println("coocc size: "+ this.cooccMatrix.size());
		
		this.attributePos = ++currentFeaturePos;
		Log.println(String.valueOf(Context.COOCC_METRIC), "Metric prepared!");
	}
	
	/** The distance corresponds to the number of cooccurrences for a specified couple of values.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		Couple<T> c = new Couple<T>((T) n1.features.get(this.attributePos), (T) n2.features.get(this.attributePos));
		return this.cooccMatrix.getOcc(c);
	}

	/**
	 */
	@Override
	public void initMF(Node n) {

		switch(n.type) {
		
		case LEAF:

			/* Get the first value and assign it as a feature.
			 * It will be used later as an id of the node in order to create the couples needed in the cooccurrency matrix */
			n.features.put(this.attributePos, n.getValues().get(0));
			break;
		default: /* Node or root */
			
			/* Use the name of the node as attribute */
			n.features.put(this.attributePos, (double) n.name);
		}
	}

	/**
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateMF(Node n) {
	
		/* Update only concern nodes and the root */
		if(n.type != TypeOfNode.LEAF) {
			
			/* For each neighbor, Create a new couple */
			for(Map.Entry<Node, Adjacency> entry: n.listOfNeighbors.entrySet()) {
				
				/* Create a new couple */
				Node neighbor = entry.getKey();
				Couple<T> newCouple = new Couple<T>((T) neighbor.features.get(this.attributePos),
													(T) n.features.get(this.attributePos));
				Couple<T> leftCouple = new Couple<T>((T) n.leftNode.features.get(this.attributePos),
					                                 (T) neighbor.features.get(this.attributePos));
				Couple<T> rightCouple = new Couple<T>((T) n.rightNode.features.get(this.attributePos),
                        							  (T) neighbor.features.get(this.attributePos));
				
				/* Sum up of the parallel links */
//				int newValue = this.cooccMatrix.getOcc(leftCouple) + this.cooccMatrix.getOcc(rightCouple);

				/* Get the max among the parallel links */
				int newValue = Math.max(this.cooccMatrix.getOcc(leftCouple), this.cooccMatrix.getOcc(rightCouple));

				/* Add it to the coocccurrence matrix ~ histogram */
				this.cooccMatrix.put(newCouple, newValue);
			}
		}
		this.cooccMatrix.print();
		System.out.println("coocc size: "+ this.cooccMatrix.size());
	}
}
