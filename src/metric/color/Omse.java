package metric.color;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.ImTool;
import utils.Log;

public class Omse extends Metric implements MetricInterface{

	ArrayList<Integer> pos = new ArrayList<Integer>();
	
	int nbBands;
	
	public Omse(BufferedImage image) {
		
		this.type = TypeOfMetric.OMSE;
		this.img = image;
		this.nbBands = ImTool.getNbBandsOf(this.img);
		this.pos = new ArrayList<Integer>(this.nbBands);
		for(int b = 0; b < this.nbBands; ++b){
			
			this.pos.add(++Metric.currentFeaturePos);
		}

		ImTool.initMinMaxValues(this.img); // Needed for the later normalization
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	private double combineMean(double m1, double m2, int s1, int s2) {
		
		return ((m1 * s1) + (m2 * s2)) / (s1 + s2);
	}

	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double score = 0.0;
		
		int s1 = n1.getSize();
		int s2 = n2.getSize();
		double totalSize = s1 + s2;
		
		ArrayList<Point> points = new ArrayList<Point>();
		points.addAll(n1.getPixels());
		points.addAll(n2.getPixels());

		for(int band = 0; band < this.nbBands; ++band) {
			
			int posBand = this.pos.get(band);
			double scoreB = 0.0;

			double mr1ur2 = combineMean(n1.features.get(posBand), n2.features.get(posBand), s1, s2);
			
			for(Point p: points) {
				
				double pixVal = ImTool.getPixelValue(p.x, p.y, band, this.img);
				double diff = pixVal - mr1ur2;
				scoreB += Math.pow(diff, 2.);
			}
			
			score += scoreB;
		}

		score /= totalSize;
		
		return score;
	}

	@Override
	public void initMF(Node n) {
		
		/* Nothing to initiate */
	}

	@Override
	public void updateMF(Node n) {

		switch(n.type){
		
			case LEAF: 

				for(int band = 0; band < this.nbBands; ++band){
					
					double mean = 0.0;
					for(Point p: n.getPixels()){

						mean += ImTool.getPixelValue(p.x, p.y, band, this.img);
					}
					mean /= n.getSize();			
					int posB = this.pos.get(band);
					n.features.put(posB, mean);
				}
				break;
				
			default:
				
				for(int band = 0; band < this.nbBands; ++band){
					
					int posBand = this.pos.get(band);
					Node n1 = n.leftNode;
					Node n2 = n.rightNode;
					int s1 = n1.getSize();
					int s2 = n2.getSize();
					
					double mean = combineMean(n1.features.get(posBand), n2.features.get(posBand), s1, s2);
					n.features.put(posBand, mean);
				}
		}
	}
}
