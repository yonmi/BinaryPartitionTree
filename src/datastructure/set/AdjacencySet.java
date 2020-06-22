package datastructure.set;

import datastructure.Adjacency;
import metric.bricks.Metric;

public interface AdjacencySet {
	
	/**
	 * Adds the specific adjacency in the set.
	 * 
	 * @param adjacency element to add; should not be null
	 * 
	 * @throws NullPointerException if adjacency is null
	 * 
	 * @see AdjacencySet#add(Adjacency, Metric) specify the similarity metric to use
	 */
	public void add(Adjacency adjacency);

	/**
	 * Adds the specific adjacency in the set while computing the similarity distance score.
	 * 
	 * @param adjacency element to add; should not be null
	 * @param metric used to define the similarity (or not) of two regions
	 * 
	 * @throws NullPointerExcepetion if adjacency is null
	 * 
	 * @see AdjacencySet#add(Adjacency) DO NOT specify the similarity metric to use
	 */
	public void add(Adjacency adjacency, Metric metric);

	/**
	 * Checks if the element is already registered or not.
	 * 
	 * @param adjacency element to check; should not be null
	 * @return true if found, else false
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public boolean containsAdjacency(Adjacency adjacency);

	/**
	 * 
	 * @return true if the set is empty
	 */
	public boolean isEmpty();

	/**
	 * 
	 * @return the adjacency having the less similarity distance score
	 */
	public Adjacency optimalAdjacency();

	/**
	 * 
	 * @param adjacency element to remove from the set; should not be null
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public void remove(Adjacency adjacency);

	/**
	 * 
	 * @return the number of elements in the set
	 */
	public int size();

}
