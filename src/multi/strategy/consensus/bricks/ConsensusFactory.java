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
