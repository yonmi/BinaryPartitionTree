package multi.strategy.consensus;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import datastructure.Adjacency;
import datastructure.ListW;
import multi.strategy.consensus.bricks.Consensus;
import multi.strategy.consensus.bricks.ConsensusInterface;

/**
 * 
 * The MEAN_OF_RANK is a consensus strategy of the relative local/global information.
 * All adjacency link are ranked differently in each list. The adjacency having the minimum mean of rank among all lists will be the chosen one. 
 * In order to reduce the computation cost, we only do the sum of the ranks and the choice will be determined by the minimum value of this result.
 * </br>
 * (!) In the case of a relative local information strategy, an interval has to be specified so only a specified range in the listW(s) will be considered.
 * In some cases where the studies adjacency is not present in all listW(s) between this interval, a penalty strategy (has to) may be applied. </br>
 *</br>
 * In the case of the relative global approach, all of the listW(s)' content have to be considered. It is obvious that this kind of strategy will show some performance cost problematic.
 *
 */
public class MeanOfRank extends Consensus implements ConsensusInterface{
	
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
	public MeanOfRank(int consensusRange, int progressive) {

		this.type = ConsensusStrategy.MEAN_OF_RANK;
		this.consensusRange = consensusRange;
		this.needRanks = true;
		this.needParam = true;
		this.progressive = progressive;
	}
	
	@Override
	public Adjacency apply(List<ListW> listOfLists) {
		
		Adjacency chosenAdjacency = null;
		double chosenMeanRank = Double.MAX_VALUE;
		
		this.computeNbElementsToTreat(listOfLists.get(0).size());
		
		HashSet<Adjacency> treatedElements = new HashSet<Adjacency>();
		
		for(int il = 0; il < listOfLists.size(); ++il){
			
			Iterator<Adjacency> iterator = listOfLists.get(il).iterator();
			
			int elementPos = 0;
			while(elementPos < this.nbElementsToTreat && elementPos < listOfLists.get(il).size()){
				
				Adjacency adjacency = iterator.next();
				
				if(!treatedElements.contains(adjacency)) {
					
					double meanRank = 0;
					for(int iil = 0; iil < listOfLists.size(); ++iil){
						
						meanRank += listOfLists.get(iil).getRankOf(adjacency);
					}
					
					if(meanRank < chosenMeanRank || (meanRank == chosenMeanRank && adjacency.compareTo(chosenAdjacency)==-1)){
						
						chosenAdjacency = adjacency;
						chosenAdjacency.consensusScore = chosenMeanRank = meanRank;
					}
					
					treatedElements.add(adjacency);
				}
				
				elementPos++;
			}
		}
		return chosenAdjacency;
	}
}
