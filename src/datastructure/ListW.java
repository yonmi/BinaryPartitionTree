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

import java.util.Iterator;
import java.util.Set;

import metric.bricks.Metric;

/**
 * The listW (or setW) is a data-structure containing adjacency links ordered by their associated distance score.
 * 
 *  <p>
 *  The listW (or setW) associates a couple image / metric where:
 *  
 *  <li> image: the matrix on which the study is done
 *  <li> metric: methods computing the similarity distances between neighboring regions
 *  
 *  <p>
 *  The listW should be maintained ordered and (if possible) should let each adjacency link to know its rank in the structure. 
 *
 */
public interface ListW {

	/**
	 * 
	 * @param adjacency to add in the ordered structure; should not be null
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public void add(Adjacency adjacency);

	/**
	 * The context of the process
	 */
	public final String CONTEXT = "ListW";
	
	/**
	 * 
	 * @return the set of all contained adjacency links
	 */
	public Set<Adjacency> elements();
	
	/**
	 * 
	 * @return the index associated to the listW (very useful when several listWs (or setWs) are used).
	 */
	public int getIndex();

	/**
	 * 
	 * @return the similarity metric method associated
	 */
	public Metric getMetric();

	/**
	 * 
	 * @param adjacency the element of interest; should not be null
	 * @return the rank of the adjacency in the ordered structure
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public int getRankOf(Adjacency adjacency);

	/**
	 * Affects, for the first time, the rank of each adjacency link in the ordered structure
	 * 
	 * @see ListW#updateRanks() update the ranks after some modifications in the structure
	 */
	public void initRanks();

	/**
	 * 
	 * @return true if no element is recorded, otherwise false
	 */
	public boolean isEmpty();
	
	/**
	 * 
	 * @return an iterator allowing to sequentially browse the structure
	 */
	public Iterator<Adjacency> iterator();

	/**
	 * 
	 * @return the adjacency link associated with the smallest distance score
	 */
	public Adjacency optimalElement();

	/**
	 * Shows the content of the structure, the ranks, the chaining structure
	 */
	public void print();

	/**
	 * 
	 * @param adjacency to remove from the structure; should not be null
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public void remove(Adjacency adjacency);
	
	/**
	 * 
	 * @return the number of elements in the structure
	 */
	public int size();
	
	/**
	 * Once the ranks have been {@link ListW#initRanks() initiated}, they can be updated as soon as a modification (e.g.: adding, removal) of the structure occurs.
	 * 
	 * @see ListW#initRanks() initiate the ranks and the chaining structure for the first time 
	 */
	public void updateRanks();
}
