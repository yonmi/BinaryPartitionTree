package multi.strategy.consensus;

import java.util.Iterator;
import java.util.List;

import datastructure.Adjacency;
import datastructure.ListW;
import multi.strategy.consensus.bricks.Consensus;
import multi.strategy.consensus.bricks.ConsensusInterface;

/**
 * 
 * The MOST_FREQUENT is a consensus strategy of the relative local information.
 * This strategy focuses only on the first adjacency links included in a specified interval 'r'.
 * The choice of the adjacency will be determined by the adjacency having the most occurrence in the first 'r%' of the lists.
 *
 */
public class MostFrequent extends Consensus implements ConsensusInterface{
	
	/**
	 * Relative local information consensus strategy based on the most frequent adjacency links among a specific interval 'r' firsts adjacencies having low scores 
	 * 
	 * <p> The consensus strategy helps the choice of the adjacency that will determine the two nodes (~ regions) to merge.
	 * @param consensusRange Interval of the 1st ranks of adjacency links to consider; should be > 0 and < size of the listW (or setW)
	 * @param progressive the interval is defined proportionally to remaining number of adjacency links (0: false, 1: true)
	 * 
	 * @throws IndexOutOfBoundsException if consensusRange does not fit the requirements
	 */
	public MostFrequent(int consensusRange, int progressive) {

		this.type = ConsensusStrategy.MOST_FREQUENT;
		this.consensusRange = consensusRange;
		this.needParam = true;
		this.progressive = progressive;
	}	

	@Override
	public Adjacency apply(List<ListW> listOfLists) {

		
		Adjacency chosenAdjacency = null;
		double chosenOccurrency = 0;
		
		double nbAllElements = listOfLists.get(0).size();
		
		this.computeNbElementsToTreat(listOfLists.get(0).size());
		
		for(int il = 0; il < listOfLists.size(); ++il){
			
			Iterator<Adjacency> iterator = listOfLists.get(il).iterator();
			
			int nbElements = 1;
			while(nbElements <= this.nbElementsToTreat && nbElements <= nbAllElements){
				
				Adjacency adjacency = iterator.next();

				adjacency.consensusScore += 1;
				
				if(adjacency.consensusScore > chosenOccurrency) {
					
					chosenOccurrency = adjacency.consensusScore;
					chosenAdjacency = adjacency;
				}
				nbElements++;
			}
		}
		return chosenAdjacency;
	}
}
