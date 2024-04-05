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

import datastructure.Adjacency;
import metric.bricks.Metric;

public interface AdjacencySet {
	
	public enum OptimalOption {
		
		MINIMUM,
		MAXIMUM;
	}
	
	/**
	 * Adds the specific adjacency in the set.
	 * 
	 * @param adjacency element to add; should not be null
	 * 
	 * @throws NullPointerException if adjacency is null
	 * 
	 * @see AdjacencySet#add(Adjacency, Metric) specify the similarity metric to use
	 */
	public void add(Adjacency adjacency);

	/**
	 * Adds the specific adjacency in the set while computing the similarity distance score.
	 * 
	 * @param adjacency element to add; should not be null
	 * @param metric used to define the similarity (or not) of two regions
	 * 
	 * @throws NullPointerExcepetion if adjacency is null
	 * 
	 * @see AdjacencySet#add(Adjacency) DO NOT specify the similarity metric to use
	 */
	public void add(Adjacency adjacency, Metric metric);

	/**
	 * Checks if the element is already registered or not.
	 * 
	 * @param adjacency element to check; should not be null
	 * @return true if found, else false
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public boolean containsAdjacency(Adjacency adjacency);

	/**
	 * 
	 * @return true if the set is empty
	 */
	public boolean isEmpty();

	/**
	 * 
	 * @return the adjacency having the less similarity distance score
	 */
	public Adjacency optimalAdjacency();

	/**
	 * 
	 * @param adjacency element to remove from the set; should not be null
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public void remove(Adjacency adjacency);

	/**
	 * 
	 * @return the number of elements in the set
	 */
	public int size();
	
	/**
 	 * Allowing to know if the optimal value is the MINIMAL or the MAXIMAL.
	 * @param optimalOption
	 */
	public void setOptimalOption(OptimalOption optimalOption);

}
