package multi.strategy.consensus.bricks;

import java.util.List;

import datastructure.Adjacency;
import datastructure.ListW;

/**
 * 
 * Interface containing all methods that all consensus strategy class should implement.
 * 
 */
public interface ConsensusInterface {
	
	/**
	 * Application of the consensus strategy.
	 * @param listOfLists List of listW(s) containing various information that will be used by the consensus strategy.
	 * @return The chosen adjacency determining the two nodes (~ regions) to merge.
	 */
	public Adjacency apply(List<ListW> listOfLists);
	
	/**
	 * Asserts whether the strategy used need the specific ranks of the the adjacency links in each listW (or setW)
	 * @return true if the precise ranks of the adjacency links are important and have to be updated as many times as possible 
	 */
	public boolean needRanks();
}
