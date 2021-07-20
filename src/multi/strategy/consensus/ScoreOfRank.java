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

import java.util.Iterator;
import java.util.List;
import datastructure.Adjacency;
import datastructure.ListW;
import multi.strategy.consensus.bricks.Consensus;
import multi.strategy.consensus.bricks.ConsensusInterface;

/**
 * 
 * The SCORE_OF_RANK is a consensus strategy of the relative local/global information.
 * 
 * <p>
 * It is a variant of MEAN_OF_RANK.
 * All adjacency are ranked differently in each list. The adjacency having the minimum mean of rank among all lists will be the chosen one. 
 * In order to reduce the computation cost, we only do the sum of the ranks and the choice will be determined by the minimum value of this result.
 * </br>
 * (!) In the case of a relative local information strategy, an interval has to be specified so only a specified range in the listW(s) will be considered.
 * In some cases where the studies adjacency is not present in all listW(s) between this interval, a penalty strategy (has to) may be applied. </br>
 *</br>
 * In the case of the relative global approach, all of the listW(s)' content have to be considered. It is obvious that this kind of strategy will show some performance cost problematic.
 *
 */
public class ScoreOfRank extends Consensus implements ConsensusInterface{
	
	/**
	 * Relative local/global information consensus strategy based on the mean of the ranks of the adjacency links stored in all listW(s) 
	 * 
	 * <p> The consensus strategy helps the choice of the adjacency that will determine the two nodes (~ regions) to merge.
	 * 
	 * @param consensusRange Interval of the 1st ranks of adjacency links to consider; should be > 0 and < the size of the listW (or setW)
	 * @param progressive the interval is defined proportionally to remaining number of adjacency links (0: false, 1: true)
	 * 
	 * @throws IndexOutOfBoundsException if consensusRange does not fit the requirements
	 */
	public ScoreOfRank(int consensusRange, int progressive) {

		this.type = ConsensusStrategy.SCORE_OF_RANK;
		this.consensusRange = consensusRange;
		this.needParam = true;
		this.progressive = progressive;
	}
	
	@Override
	public Adjacency apply(List<ListW> listOfLists) {
		
		Adjacency chosenAdjacency = null;
		double chosenScoreRank = -Double.MAX_VALUE;
		
		double nbAllElements = listOfLists.get(0).size();
		
		this.computeNbElementsToTreat(listOfLists.get(0).size());

		for(int il = 0; il < listOfLists.size(); ++il){
			
			Iterator<Adjacency> iterator = listOfLists.get(il).iterator();
			
			int rank = 1;
			while(rank <= this.nbElementsToTreat && rank <= nbAllElements){
				
				Adjacency adjacency = iterator.next();

				adjacency.consensusScore += (nbAllElements - rank);
				
				if(adjacency.consensusScore > chosenScoreRank) {
					
					chosenScoreRank = adjacency.consensusScore;
					chosenAdjacency = adjacency;
				}
				rank++;
			}
		}
		return chosenAdjacency;
	}
}
