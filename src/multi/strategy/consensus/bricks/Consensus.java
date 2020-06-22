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
