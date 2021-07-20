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

package multi.strategy.consensus.bricks;

import multi.strategy.consensus.MeanOfRank;
import multi.strategy.consensus.MinOfMin;
import multi.strategy.consensus.MostFrequent;
import multi.strategy.consensus.ScoreOfRank;
import multi.strategy.consensus.bricks.Consensus.ConsensusStrategy;

/**
 * 
 * Factory building consensus strategies objects.
 * Each coded strategy class should figure in this class as a choice.
 *
 */
public class ConsensusFactory {

	/**
	 * Build the right consensus strategy and consider its parameters.
	 * @param consensusStrategy the consensus strategy to build; should not be null
	 * @param consensusRange the range to consider in the listW(s) should be > 0 and < size of the listW (or setW)
	 * @param progressive the interval is defined proportionally to remaining number of adjacency links (0: false, 1: true)
	 * @return a consensus strategy object to use during the creation of the tree
	 * 
	 * @throws NullPointerException if consensusStrategy is null
	 * @throws IndexOutOfBoundsException if consensusRange does not fit the size of the list
	 */
	public static Consensus buildConsensusStrategy(ConsensusStrategy consensusStrategy, int consensusRange, int progressive) {

		switch(consensusStrategy){
		
			case MEAN_OF_RANK:
				return new MeanOfRank(consensusRange, progressive);
			case MIN_OF_MIN:
				return new MinOfMin();
			case MOST_FREQUENT:
				return new MostFrequent(consensusRange, progressive);
			case SCORE_OF_RANK:
				return new ScoreOfRank(consensusRange, progressive);
			default:
				return new MeanOfRank(consensusRange, progressive);
		}
	}
}
