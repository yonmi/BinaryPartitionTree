package metric.combination;

import java.awt.image.BufferedImage;

import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import metric.color.Ominmax;
import metric.color.Omse;
import metric.color.Omselab;
import metric.color.Owsdm;
import metric.shape.Ocontour_;
import utils.Log;

public class Ocolcont extends Metric implements MetricInterface{
	
	/* double alpha is the this.params.get(0) */
	
	Metric ocol;
	Metric ocont;
	
	public double ocolMinScore = Double.MAX_VALUE;
	public double ocolMaxScore = Double.MIN_VALUE;
	
	public double ocontMinScore = Double.MAX_VALUE;
	public double ocontMaxScore = Double.MIN_VALUE;
	
	public Ocolcont(BufferedImage image, TypeOfMetric typeOfOcol, double alpha) {
		
		this.params.add(alpha);

		this.img = image;
		this.ocont = new Ocontour_();
		
		switch(typeOfOcol) {
		
			case OMIN_MAX:

				this.type = TypeOfMetric.OCOL_CONT_MIN_MAX;
				this.ocol = new Ominmax(image);
				break;
				
			case OMSE:
				
				this.type = TypeOfMetric.OCOL_CONT_MSE;
				this.ocol = new Omse(image);
				break;
			
			case OMSE_LAB:
				
				this.type = TypeOfMetric.OCOL_CONT_MSE_LAB;
				this.ocol = new Omselab(image);
				break;
				
			case OWSDM:
				
				this.type = TypeOfMetric.OCOL_CONT_WSDM;
				this.ocol = new Owsdm(image);				
				break;
				
			default:
				
				this.type = TypeOfMetric.OCOL_CONT_MIN_MAX;
				this.ocol = new Ominmax(image);				
		}
		
		Log.println(String.valueOf(TypeOfMetric.OCOL_CONT), "Metric prepared!");
	}
	
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double ocolscore = this.ocol.computeDistances(n1, n2);
		double ocontscore =  this.ocont.computeDistances(n1, n2);
		
		// wsdm, mse, min max
/*		double colMin = 0.0; //0.3; //3; 
		double colMax = 2.7; //32928; //765;
		
		double contMin = 0.0;
		double contMax = 5032.0;
		
		ocolscore = (ocolscore - colMin) / (colMax - colMin);
		ocontscore = (ocontscore - contMin) / (contMax - contMin); 
		double score = ocolscore + 100000 * ocontscore;*/

		double alpha = this.params.get(0);

		/* balancing the weights between the two scores */
		double balance = 1; 
		if(this.ocol.type == TypeOfMetric.OWSDM) {
			
			balance = 2000;
			alpha = 2;
		}
		
		if(this.ocol.type == TypeOfMetric.OMSE) {
			
			alpha = 2;
			balance = 10000;
		}
		
		if(this.ocol.type == TypeOfMetric.OMIN_MAX) {
			
			alpha = 0.8;
		}
		
		//alpha = 0.5;
		//System.out.println("alpha: "+ alpha);
		double score = (alpha * ocolscore) + ((1 - alpha) * balance * ocontscore);
		//double score = (alpha * ocolscore) + ((1 - alpha) * 2000 * ocontscore);
		
		if(this.ocolMinScore > ocolscore) this.ocolMinScore = ocolscore;
		if(this.ocolMaxScore < ocolscore) this.ocolMaxScore = ocolscore;

		if(this.ocontMinScore > ocontscore) this.ocontMinScore = ocontscore;
		if(this.ocontMaxScore < ocontscore) this.ocontMaxScore = ocontscore;

		return score;
	}

	@Override
	public void initMF(Node n) {
		
		this.ocol.initMF(n);
	}

	@Override
	public void updateMF(Node n) {
		
		this.ocol.updateMF(n);
	}
}
