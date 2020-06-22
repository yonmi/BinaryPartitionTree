package metric.bricks;

import datastructure.Node;

/**
 * 
 * Interface containing all methods that all metric classes should implement.
 *
 */
public interface MetricInterface {

	/**
	 * Prepare all the Metric Features (MF) corresponding to the chosen metric.
	 * @param n; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	public void initMF(Node n);
	
	/**
	 * Initiate or update the values of the Metric Features (MF).
	 * @param n; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	public void updateMF(Node n);
	
	/**
	 * Compute a distance between 'n1' and 'n2'.
	 * @param n1; should not be null
	 * @param n2; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'.
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	public double computeDistances(Node n1, Node n2);
}
