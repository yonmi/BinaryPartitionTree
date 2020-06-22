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
