package metric.shape;

import java.awt.image.BufferedImage;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.Log;

public class Ocontour_ extends Metric implements MetricInterface{
	
	public Ocontour_() {
		
		Log.println(String.valueOf(TypeOfMetric.OCONTOUR), "Metric prepared!");
	}
	
	public Ocontour_(BufferedImage image) {

		this.img = image;
		this.type = TypeOfMetric.OCONTOUR;
	}

	@Override
	public double computeDistances(Node n1, Node n2) {
		
		return this.computeScore(n1, n2);
	}

	@Override
	public void initMF(Node n) {}

	@Override
	public void updateMF(Node n) {}
	
	private double computeScore(Node n1, Node n2) {
		
		return Math.max(0, deltaP(n1, n2));
	}

	private static double deltaP(Node n1, Node n2) {
		
		int frontier = n1.listOfNeighbors.get(n2).frontier;
		
		//System.out.println("frontier1: "+ frontier +" frontier2: "+ n2.listOfNeighbors.get(n1).frontier);
		
		double pi = n1.perimeter;
		double pj = n2.perimeter;
		double pij = frontier;
		
		//System.out.println("pi: "+ pi +", pj: "+ pj +", pij: "+ pij);
		
		return Math.min(pi, pj) * 2*pij;
		//return (pi + pj - 2*frontier) / Math.max(pi, pj);
	}
}
