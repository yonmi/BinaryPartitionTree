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

package multi.strategy.consensus;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import datastructure.Adjacency;
import datastructure.ListW;
import multi.strategy.consensus.bricks.Consensus;
import multi.strategy.consensus.bricks.ConsensusInterface;

/**
 * 
 * The MIN_OF_MIN is a consensus strategy of the absolute information.
 * This strategy focuses only on the adjacency links having the smallest score in each ordered list.
 * The choice of the adjacency will be determined by the one that have the minimum score value among this focused set of adjacency links.
 *
 */
public class MinOfMin extends Consensus implements ConsensusInterface{

	/**
	 * Absolute information consensus strategy based on the minimum score of the mimimum scores of of the adjacencies stored in all listW(s) 
	 * 
	 * <p> The consensus strategy helps the choice of the adjacency that will determine the two nodes (~ regions) to merge.
	 */
	public MinOfMin() {

		this.type = ConsensusStrategy.MIN_OF_MIN;
		this.needParam = false;
	}

	@Override
	public Adjacency apply(List<ListW> listOfLists) {

		Adjacency chosenAdjacency = null;
		
		/* Used for storing the adjacency links having the minimum value of score. There is no redundant element. */
		Set<Adjacency> minAdjacencySet = new TreeSet<Adjacency>();
		
		/* For each listW, take the adjacency having the minimum value and consider it as the chosen one if the its score is lower than the others. 
		 * (!) As the order of our list is descendant, we will pick up the last adjacency.
		 */
		for(int il = 0; il < listOfLists.size(); ++il){
			
			ListW listw = listOfLists.get(il);
			Adjacency adjacency = listw.optimalElement();
			minAdjacencySet.add(adjacency);
			
			if(chosenAdjacency == null || adjacency.scores[il] < chosenAdjacency.scores[il]){
				
				chosenAdjacency = adjacency;
			}
		}
		
		/*
		 * For tracking the conflict, lets consider the number of adjacencies having the same score.
		 * TODO - think about it again deeply.
		 */
		chosenAdjacency.consensusScore = listOfLists.get(0).size() - minAdjacencySet.size();

		return chosenAdjacency;
	}
}
