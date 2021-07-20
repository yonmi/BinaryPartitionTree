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
