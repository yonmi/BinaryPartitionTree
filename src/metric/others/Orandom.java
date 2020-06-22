package metric.others;

import java.awt.image.BufferedImage;
import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.Log;

/**
 * 
 * Metric based on random values.
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 * 
 */
public class Orandom extends Metric implements MetricInterface {
	
	public enum Context{
		
		RANDOM_M
	}
	
	/**
	 * Position of the random feature in the list of MF.
	 */
	public int randPos;
	
	/**
	 * Registers an image within the metric and creates the random metric values.
	 * @param image; should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public Orandom(BufferedImage image) {
		
		this.type = TypeOfMetric.ORANDOM;
		this.img = image;
		
		randPos = ++currentFeaturePos;
		Log.println(String.valueOf(Context.RANDOM_M), "Metric prepared!");
	}
	
	/**
	 * Generates a random metric value.
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return random score
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double res = Math.abs(n1.features.get(this.randPos) - n2.features.get(this.randPos)); 
//		System.out.println("random value: "+ res +"\n");
		return res;
	}

	/**
	 * Generates a random feature value and initializes the feature value.
	 * @param n Concerned node, should not be null
	 * 
	 */
	@Override
	public void initMF(Node n) {

		double randVal = Math.random();
		n.features.put(this.randPos, randVal);
	}

	/**
	 * Generates a random feature value and updates the previous value with it.
	 * @param n Concerned node; should not be null
	 * 
	 */
	@Override
	public void updateMF(Node n) {
	
		double randVal = Math.random();
		n.features.put(this.randPos, randVal);
	}
}
