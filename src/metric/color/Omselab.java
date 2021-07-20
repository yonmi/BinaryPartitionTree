/****************************************************************************
* Copyright AGAT-Team (2014)						       
* 									    
* Contributors:								
* J.F. Randrianasoa							    
* K. Kurtz								    
* E. Desjardin								    
* N. Passat								    
* 									    
* This software is a computer program whose purpose is to [describe	    
* functionalities and technical features of your software].		    
* 									    
* This software is governed by the CeCILL-B license under French law and    
* abiding by the rules of distribution of free software.  You can  use,     
* modify and/ or redistribute the software under the terms of the CeCILL-B  
* license as circulated by CEA, CNRS and INRIA at the following URL	    
* "http://www.cecill.info". 						    
* 									    
* As a counterpart to the access to the source code and  rights to copy,    
* modify and redistribute granted by the license, users are provided only   
* with a limited warranty  and the software's author,  the holder of the    
* economic rights,  and the successive licensors  have only  limited	    
* liability. 								    
* 									    
* In this respect, the user's attention is drawn to the risks associated    
* with loading,  using,  modifying and/or developing or reproducing the     
* software by the user in light of its specific status of free software,    
* that may mean  that it is complicated to manipulate,  and  that  also	   
* therefore means  that it is reserved for developers  and  experienced     
* professionals having in-depth computer knowledge. Users are therefore     
* encouraged to load and test the software's suitability as regards their   
* requirements in conditions enabling the security of their systems and/or  
* data to be ensured and,  more generally, to use and operate it in the     
* same conditions as regards security. 					    
*								            
* The fact that you are presently reading this means that you have had	    
* knowledge of the CeCILL-B license and that you accept its terms.          
* 									   		
* The full license is in the file LICENSE, distributed with this software.  
*****************************************************************************/

package metric.color;

import java.awt.Point;
import java.awt.image.BufferedImage;
import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import utils.ImTool;
import utils.Log;

public class Omselab extends Metric implements MetricInterface{

	int pos[];
	
	public Omselab(BufferedImage image) {
		
		this.type = TypeOfMetric.OMSE_LAB;
		this.img = image;

		this.pos = new int[3];
		this.pos[0] = ++Metric.currentFeaturePos; // L position
		this.pos[1] = ++Metric.currentFeaturePos; // A position
		this.pos[2] = ++Metric.currentFeaturePos; // B position

		ImTool.initMinMaxValues(this.img); // Needed for the later normalization
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	private double combineMean(double m1, double m2, int s1, int s2) {
		
		return ((m1 * s1) + (m2 * s2)) / (s1 + s2);
	}

	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double score1 = 0.0;
		double score2 = 0.0;
		double score3 = 0.0;
		
		int s1 = n1.getSize();
		int s2 = n2.getSize();
		double totalSize = s1 + s2;
		
		double mr1ur2_1 = combineMean(n1.features.get(this.pos[0]), n2.features.get(this.pos[0]), s1, s2);
		double mr1ur2_2 = combineMean(n1.features.get(this.pos[1]), n2.features.get(this.pos[1]), s1, s2);
		double mr1ur2_3 = combineMean(n1.features.get(this.pos[2]), n2.features.get(this.pos[2]), s1, s2);
		
		for(Point p: n1.getPixels()) {
			
			int r = ImTool.getNormPixelValues(p.x, p.y, 0, this.img);
			int g = ImTool.getNormPixelValues(p.x, p.y, 1, this.img);
			int b = ImTool.getNormPixelValues(p.x, p.y, 2, this.img);
			double[] lab = ImTool.rgb2lab(r, g, b);
			
			score1 += Math.pow((lab[0] - mr1ur2_1), 2.0);
			score2 += Math.pow((lab[1] - mr1ur2_2), 2.0);
			score3 += Math.pow((lab[2] - mr1ur2_3), 2.0);
		}
		
		for(Point p: n2.getPixels()) {
			
			int r = ImTool.getNormPixelValues(p.x, p.y, 0, this.img);
			int g = ImTool.getNormPixelValues(p.x, p.y, 1, this.img);
			int b = ImTool.getNormPixelValues(p.x, p.y, 2, this.img);
			double[] lab = ImTool.rgb2lab(r, g, b);
			
			score1 += Math.pow((lab[0] - mr1ur2_1), 2.0);
			score2 += Math.pow((lab[1] - mr1ur2_2), 2.0);
			score3 += Math.pow((lab[2] - mr1ur2_3), 2.0);
		}
		
		score1 /= totalSize;
		score2 /= totalSize;
		score3 /= totalSize;
		
		return score1 + score2 + score3;
	}

	@Override
	public void initMF(Node n) {
		
		/* Nothing to initiate */
	}

	@Override
	public void updateMF(Node n) {

		double meanL = 0.0;
		double meanA = 0.0;
		double meanB = 0.0;
		
		switch(n.type){
		
			case LEAF: 

				for(Point p: n.getPixels()){

					int r = ImTool.getNormPixelValues(p.x, p.y, 0, this.img);
					int g = ImTool.getNormPixelValues(p.x, p.y, 1, this.img);
					int b = ImTool.getNormPixelValues(p.x, p.y, 2, this.img);
					double[] lab = ImTool.rgb2lab(r, g, b);
					
					meanL += lab[0];
					meanA += lab[1];
					meanB += lab[2];

				}
				
				meanL /= n.getSize();
				meanA /= n.getSize();
				meanB /= n.getSize();
				
				n.features.put(this.pos[0], meanL);
				n.features.put(this.pos[1], meanA);
				n.features.put(this.pos[2], meanB);
				
				break;
				
			default:

				meanL = combineMean(n.leftNode.features.get(this.pos[0]), n.rightNode.features.get(this.pos[0]),
									n.leftNode.getSize(), n.rightNode.getSize());
				meanA = combineMean(n.leftNode.features.get(this.pos[1]), n.rightNode.features.get(this.pos[1]),
									n.leftNode.getSize(), n.rightNode.getSize());
				meanB = combineMean(n.leftNode.features.get(this.pos[2]), n.rightNode.features.get(this.pos[2]),
									n.leftNode.getSize(), n.rightNode.getSize());
				
				n.features.put(this.pos[0], meanL);
				n.features.put(this.pos[1], meanA);
				n.features.put(this.pos[2], meanB);		
		}
	}
}
