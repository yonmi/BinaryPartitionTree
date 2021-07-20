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

import java.util.List;

import datastructure.Adjacency;
import datastructure.ListW;

/**
 * 
 * Parent of all consensus strategies.
 * (!) All consensus strategies classes must implement the interface 'agat.consensus.support.ConsensusInterface' and override all its methods.
 *
 */
public class Consensus implements ConsensusInterface{

	/**
	 * Defines the interval to consider
	 * 
	 * <p>
	 * A percentage corresponding to the first ranked elements in the listW (or setW)
	 */
	public int consensusRange = 100;
	
	/**
	 * Number of elements included in the interval determined by the percentage {@linkplain Consensus#consensusRange consensusRange}
	 */
	public int nbElementsToTreat = -1;
	
	/**
	 * The parameter is the range r determining the interval of the top elements to consider
	 */
	public boolean needParam = false;
	
	/**
	 * Must set to true if the strategy requires the use of the precise rank of each adjacency link in the structure
	 */
	protected boolean needRanks = false; 
	
	/**
	 * The interval to consider is defined proportionally to remaining number of adjacency links.
	 * 
	 * <li> 0: false
	 * <li> 1: true
	 */
	public int progressive = 1;
	
	/**
	 * All different type of consensus strategies
	 */
	public enum ConsensusStrategy{
		
		MEAN_OF_RANK,
		MIN_OF_MIN,
		MOST_FREQUENT,
		SCORE_OF_RANK
	}

	/**
	 * The type of consensus strategy considered
	 */
	public ConsensusStrategy type;
	
	@Override
	public Adjacency apply(List<ListW> listOfLists) {

		System.err.println(String.valueOf(this.type) +"[WARNING] the method 'agat.consensus.support.ConsensusInterface.apply()' is not implemented!");
		System.exit(0);

		return null;
	}
	
	public void computeNbElementsToTreat(int nbTotalElements) {
		
		if(this.progressive == 1 || this.nbElementsToTreat <= 0) {
			
			if(this.consensusRange < 100) {
				
				this.nbElementsToTreat = (nbTotalElements * this.consensusRange) / 100;
				
			}else {
				
				this.nbElementsToTreat = nbTotalElements;
			}
			
			if(this.nbElementsToTreat < 1) {
				
				this.nbElementsToTreat = 1;
			}
		}
		
//		Log.print(this.type+"", "Elements to treat: "+ this.nbElementsToTreat);
	}

	@Override
	public boolean needRanks() {

		return this.needRanks;
	}
}
