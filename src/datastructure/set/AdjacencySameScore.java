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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import datastructure.Adjacency;
import utils.Log;

public class AdjacencySameScore implements Comparable<AdjacencySameScore>{

	public double score;
	
	public Set<Adjacency> bucket;
	
	public AdjacencySameScore(double distance) {

		this.score = distance;
		this.bucket = new HashSet<Adjacency>();
	}

	public boolean add(Adjacency adjacency) {

		return this.bucket.add(adjacency);
	}

	public boolean contains(Adjacency adjacency) {

		return this.bucket.contains(adjacency);
	}

	@Override
	public int compareTo(AdjacencySameScore adjacencySameScore) {

		if(this.score == adjacencySameScore.score) {
			
			return 0;
			
		}else if(this.score < adjacencySameScore.score) {
			
			return -1;
			
		}else return 1;
	}
	
	@Override
	public boolean equals(Object o) {
	
		if (o == this) return true;
        
		if (!(o instanceof AdjacencySameScore)) {
			
            return false;
        }
		
		return this.score == ((AdjacencySameScore) o).score;
	}
	
	@Override
    public int hashCode() {
    	
        return Objects.hash(this.score);
    }
	
	public boolean isEmpty() {

		return this.bucket.isEmpty();
	}

	public Adjacency optimal() {
	
		return this.bucket.iterator().next();
	}

	public boolean remove(Adjacency adjacency) {

		boolean removed = this.bucket.remove(adjacency);
		
		if(!removed) {
			
			Log.println("SetOfAdjacencies", adjacency.getIndex() +"Not found");
		}
		return removed;
	}

	public int size() {

		return this.bucket.size();
	}	
}
