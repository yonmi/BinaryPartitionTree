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

import java.util.Comparator;

import datastructure.Adjacency;

/**
 * Comparator used to maintain a structure as the listW ordered.
 *
 * <p>
 * Two adjacency links a1 and a2 can be compared and the results can be:
 * 
 *  <li> 0: a1 and a2 are same
 *  <li> > 0: a1 > a2
 *  <li> < 0: a1 < a2
 */
public class AdjaComparator implements Comparator<Adjacency>{

	/**
	 * Identification of the listW or setW that has to be ordered according to this comparator
	 */
	public int listIndex;
	
	public boolean maintainChaining = false;
	
	/**
	 * Creates a comparator by specifying the index of the listW associated with it
	 * 
	 * @param listIndex defining the listW
	 * @param maintainChaining if true, the chaining links is maintained during comparisons
	 */
	public AdjaComparator(int listIndex, boolean maintainChaining){
		
		this.listIndex = listIndex;
		this.maintainChaining = maintainChaining;
	}
	
	@Override
	public int compare(Adjacency a1, Adjacency a2) {

		double distance1 = a1.scores[this.listIndex];
		double distance2 = a2.scores[this.listIndex];
		int res = 0;
		
		if(distance1 == distance2) {
	
			res = a1.compareTo(a2);
			
		}else if(distance1 < distance2) res = -1;
		else res = 1;
		
		if(this.maintainChaining && a1.updateChains[this.listIndex]) {
			
			if(res < 0) {
				
				Adjacency previous2 = a2.previous[this.listIndex];
				
				if(previous2 != null) {
					
					if(distance1 > previous2.scores[this.listIndex] || (distance1 == previous2.scores[this.listIndex] && a1.compareTo(previous2) == 1)) {

						previous2.next[this.listIndex] = a1;
						a1.previous[this.listIndex] = previous2;
						
						a1.next[this.listIndex] = a2;
						a2.previous[this.listIndex] = a1;
						
						a1.updateChains[this.listIndex] = false;
					}
					
				}else {
					
					a1.next[this.listIndex] = a2;
					a2.previous[this.listIndex] = a1;
					a1.updateChains[this.listIndex] = false;
				}
				
			}else if(res > 0){
				
				Adjacency next2 = a2.next[this.listIndex];
				
				if(next2 != null) {
					
					if(distance1 < next2.scores[this.listIndex] || (distance1 == next2.scores[this.listIndex] && a1.compareTo(next2) == -1)) {

						next2.previous[this.listIndex] = a1;
						a1.next[this.listIndex] = next2;
						
						a2.next[this.listIndex] = a1;
						a1.previous[this.listIndex] = a2;
						a1.updateChains[this.listIndex] = false;
					}					
				}else {
					
					a2.next[this.listIndex] = a1;
					a1.previous[this.listIndex] = a2;
					a1.updateChains[this.listIndex] = false;
				}
			}
		}
		
		return res;
	}
}
