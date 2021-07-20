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

package datastructure;

import java.util.Objects;
import datastructure.set.AdjacencySet;
import metric.bricks.Metric;

/**
 * An adjacency links two neighboring regions.
 * It is associated to a similarity distance between the corresponding regions.
 * The adjacency links are the edges of the Region Adjacency Graph while the regions (~nodes) are the summits.
 */
public class Adjacency implements Comparable<Adjacency>{
	
	/**
	 * <b> For multi-featuring purpose: </b>
	 * 
	 * Score evaluating the quality of the consensual choice.
	 */
	public double consensusScore;
	
	/**
	 * Similarity distance between the two neighboring regions.
	 */
	public double distance = -1;
	
	/**
	 * Length of the frontier between the two regions.
	 */
	public int frontier;
	
	/**
	 * Identification of the adjacency link.
	 * 
	 * <p>
	 * The inferior node index is always on the left while the other is in the right.
	 * 
	 * <p>
	 * Example: </br> 
	 * For a node1 named '10' and node2 named '5', the adjacency index is <b>5_10</b>.
	 */
	private String index;

	/**
	 * <b> For multi-featuring purpose: </b>
	 *  
	 * Next adjacency in each list of adjacency links.
	 */
	public Adjacency next[];

	/**
	 * First region having node2 as neighbor.
	 */
	public Node node1;

	/**
	 * Second region having node1 as neighbor.
	 */
	public Node node2;
	
	/**
	 * <b> For multi-featuring purpose: </b>
	 *  
	 * Previous adjacency in each list of adjacency links. 
	 */
	public Adjacency previous[];
	
	/**
	 * <b> For multi-featuring purpose: </b>
	 * 
	 * Position of the adjacency in each list / set of adjacency links.
	 */
	public int ranks[];
	
	/**
	 * States if the adjacency is already stored somewhere or not. 
	 */
	private boolean registered = false;

	/**
	 * <b> For multi-featuring purpose: </b> 
	 * 
	 * Similarity distance between the two neighboring regions for each metric, image association.
	 */
	public double scores[];

	/**
	 * List containing the registered adjacency link
	 */
	public AdjacencySet sideAdjaSet;
	
	/**
	 * <b> For multi-featuring purpose: </b>
	 * 
	 * Inits a Flag determining how the adjacency will be treated when inserting it in each list / set of adjacency links (e.g. ListW). 
	 * If <b> true </b>, the adjacency's treatment (~ comparing, adding, removing) in the listW will not update the chaining links.
	 */
	public boolean updateChains[];
	
	/**
	 * <b> For multi-featuring purpose: </b>
	 *  
	 * States needed when managing each list / set of adjacency links. 
	 */
	public enum State{
		
		NORMAL,
		ADDED,
		REMOVED,
		CHECKED
	}
	
	/**
	 * <b> For multi-featuring purpose: </b>
	 * 
	 * Current state of the adjacency helping for the list / set of adjacency links management.
	 */
	public State state[];

	/**
	 * Creates an adjacency using two node identifications
	 * 
	 * @param nodeIndex1
	 * @param nodeIndex2
	 * 
	 * @see Adjacency#Adjacency(Node, Node) create and adjacency from two nodes
	 */
	public Adjacency(int nodeIndex1, int nodeIndex2) {
		
		this.node1 = new Node(nodeIndex1);
		this.node2 = new Node(nodeIndex2);
		this.index = nodeIndex1 +"_"+ nodeIndex2;	
	}

	/**
	 * Creates an adjacency from two nodes. 
	 * 
	 * @param node1 should not be null
	 * @param node2 should not be null
	 * 
	 * @throws NullPointerException if node1 is null or node2 is null
	 * 
	 * @see Adjacency#Adjacency(int, int) create an adjacency link 
	 */
	public Adjacency(Node node1, Node node2) {
		
		/* generate index and set nodes */
		this.setIndex(node1, node2);
		
		/* Aknowledge neighbors */
		this.node1.addNeighbor(this.node2, this);
		this.node2.addNeighbor(this.node1, this);
	}
	
	@Override
	public int compareTo(Adjacency adjacency) {
		
		if(this.distance >=0) {
			
			double myDistance = this.distance;
			double hisDistance = adjacency.distance; 

			if(myDistance == hisDistance) {

				if(this.equals(adjacency)) return 0;
				else return -(this.index.compareTo(adjacency.index));

			}
			if(myDistance < hisDistance) return -1;
			else return 1;

		}else {
			
			return this.getIndex().compareTo(adjacency.getIndex());
		}
	}

	/**
	 * 
	 * @param metric used to define the similarity (or not) of two regions
	 * @return a similarity values between the two neighboring regions
	 * 
	 * @throws IndexOutOfBoundsException if nbBands is not in [0, number of bands of the image]
	 */
	public double computeDistance(Metric metric) {
		
		return this.distance = metric.computeDistances(this.node1, this.node2);
	}

	@Override
	public boolean equals(Object o) {
		
		if(o == null) return false;
		return this.index.equals(((Adjacency) o).index);
	}

    @Override
    public int hashCode() {
    	
        return Objects.hash(this.index);
    }
	
	/**
	 * 
	 * @return true if the adjacency is already stored somewhere, otherwise false
	 */
	public boolean isRegistered() { 
		
		return this.registered ; 
	}

	/**
	 * For a node1 named '10' and node2 named '5', the adjacency index is <b>5_10</b>.
	 * 
	 * @return the identification of the adjacency
	 */
	public String getIndex() {
		
		return this.index;
	}
	
	/**
	 * 
	 * @param nodeName index of the node having the neighbor to find
	 * @return the neighbor of the node having nodeName as index
	 */
	public Node getNeighbor(int nodeName) {
	
		if(this.node1.name == nodeName) {
			
			return this.node2;
			
		}else {
			
			return this.node1;
		}
	}

	/**
	 * Remembers that the adjacency, that has an unique index, is already stored somewhere
	 */
	public void register() { 
		
		this.registered = true; 
	}

	/**
	 * Generates and affect an unique index to the adjacency.
	 * 
	 * <p>
	 * The index of an adjacency is indeed unique.
	 * Example: </br> 
	 * For a node1 named '10' and node2 named '5', the adjacency index is <b>5_10</b>.
	 *
	 * @param node1 should not be null
	 * @param node2 should not be null
	 * 
	 * @throws NullPointerException if node1 is null or node2 is null
	 */
	public void setIndex(Node node1, Node node2) {

		int label1 = node1.name;
		int label2 = node2.name;
		if(label1 < label2) {
			
			this.index = label1 +"_"+ label2;
			this.node1 = node1;
			this.node2 = node2;
			
		}
		else {
			
			this.index = label2 +"_"+ label1;
			this.node1 = node2;
			this.node2 = node1;
		}
	}

	/**
	 * States that the adjacency, that has an unique index, is not stored anywhere
	 */
	public void unregister() { 
		
		this.registered = false; 
	}
}
