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

package datastructure.set;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import datastructure.Adjacency;
import datastructure.Node;
import metric.bricks.Metric;
import utils.LabelMatrix;
import utils.Log;

public class SetOfAdjacencies implements AdjacencySet, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * <b> For parallel fashion purpose: </b>
	 * used to follow the created nodes during the process. 
	 */
	public int currentNodeIndex = 0;
	
	/**
	 * <b> For parallel fashion purpose: </b>
	 * nodes belonging to other sides having neighbors in the current side.
	 */
	public ConcurrentHashMap<Integer, Node> foreignNodes;
	
	/**
	 * <b> For parallel fashion purpose: </b>
	 * each side of the construction has an unique matrix of labels. 
	 */
	public LabelMatrix labelMatrix;
	
	/**
	 * <b> For parallel fashion purpose: </b>
	 * number of smallest node units.
	 */
	public int nbLeaves;

	/**
	 * <b> For parallel fashion purpose: </b> 
	 */
	public Node[] nodes;

	/**
	 * Main ordered structure.
	 */
	public TreeSet<Adjacency> set;

	/**
	 * Max column
	 * 
	 * <p>
	 * N.B: used when only a crop of the image is considered. 
	 */
	public int xMax;
	
	/**
	 * Max row
	 * 
	 * <p>
	 * N.B: used when only a crop of the image is considered. 
	 */
	public int yMax;
	
	/**
	 * Min column
	 * 
	 * <p>
	 * N.B: used when only a crop of the image is considered. 
	 */
	public int xMin;
	
	/**
	 * Min row
	 * 
	 * <p>
	 * N.B: used when only a crop of the image is considered. 
	 */
	public int yMin;

	/**
	 * Prepares and creates an empty structure. 
	 */
	public SetOfAdjacencies() { 
		
		this.set = new TreeSet<Adjacency>(); 
	}

	@Override
	public synchronized void add(Adjacency adjacency) {
	
		boolean added = this.set.add(adjacency);
	
		if(added) {
	
			adjacency.register();
		}
	}

	@Override
	public void add(Adjacency adjacency, Metric metric) {
	
		adjacency.computeDistance(metric);
		this.add(adjacency);
		adjacency.sideAdjaSet = this;
	}

	@Override
	public boolean containsAdjacency(Adjacency adjacency) { 
		
		return this.set.contains(adjacency); 
	}

	@Override
	public boolean isEmpty() { 
		
		return this.set.isEmpty(); 
	}

	/**
	 * Check if a point has to be considered or not.
	 * 
	 * <p>
	 * N.B: used when only a crop of the image is considered. 
	 * 
	 * @param x column coordinate
	 * @param y row coordinate
	 * @return true if the coordinates of the point is inside the studies area
	 */
	public boolean isInStudiedAread(int x, int y) { 
		
		return (x >= xMin && y >= yMin && x < xMax  && y < yMax); 
	}

	@Override
	public Adjacency optimalAdjacency() { 
		
		return this.set.first(); 
	}

	@Override
	public synchronized void remove(Adjacency adjacency) {
		
		boolean deleted = this.set.remove(adjacency);
		adjacency.unregister();

		if(!deleted) {
			
			Log.println("SetOfAdjacencies", "Could not delete the adjacency: "+ adjacency.getIndex());
		}
	}

	@Override
	public int size() { 
		
		return this.set.size(); 
	}
}
