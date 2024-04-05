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

package metric.bricks.d3;

import java.util.ArrayList;

import datastructure.Node;
import metric.bricks.MetricInterface;
import utils.Log;
import utils.d3.RGBStruct;

/**
 * 
 * Parent of all metrics.
 * (!) All metric classes must implement the interface 'MetricInterface' and override all its methods.
 * 
 */
public class Metric3D implements MetricInterface{

	public enum Context{
		
		METRIC_3D
	}
	
	/**
	 * Current position of feature.
	 */
	public static int currentFeaturePos = -1;

	/**
	 * Image of interest
	 */
	public RGBStruct cube;
	
	/**
	 * Specific parameters of the metric.
	 */
	public ArrayList<Double> params = new ArrayList<Double>();

	/**
	 * Defines which similarity metric to consider
	 */
	public TypeOfMetric type;

	/**
	 *
	 * Each metric class must be associated to a precise type.
	 * The type will help the factory to build the right metric object.
	 *
	 */
	public static enum TypeOfMetric{
		
		OMIN_MAX_3D
	}
	
	/**
	 * Computes a distance between 'n1' and 'n2'.
	 * @param n1; should not be null
	 * @param n2; should not be null
	 * @return A score (~ distance) between 'n1' and 'n2'.
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
	
		System.err.println(Context.METRIC_3D +"[WARNING] the method 'agat.metric.bricks.MetricInterface.computeDistances(Node n1, Node n2)' is not implemented!");
		System.exit(0);
		
		return Double.MAX_VALUE;
	}

	/**
	 * Prepares all the Metric Features (MF) corresponding to the chosen metric.
	 * @param n; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void initMF(Node n) {

		System.err.println(String.valueOf(Context.METRIC_3D) +"[WARNING] the method 'agat.metric.bricks.MetricInterface.prepareMf(Node n)' is not implemented!");
		System.exit(0);
		
	}

	/**
	 * Initializes a single parameter value.
	 * @param metricParam value required for the score computations.
	 */
	public void setParam(double metricParam) {

		this.params.set(0, metricParam);
		StringBuilder paramsInfo = new StringBuilder("[Parameters] ");
		paramsInfo.append(metricParam +" ");
		
		Log.println("METRIC", paramsInfo.toString());
	}
	
	/**
	 * Initializes the values of the parameters.
	 * @param metricParams list of values required for the score computations.
	 */
	public void setParams(ArrayList<Double> metricParams) {

		this.params = metricParams;
		StringBuilder paramsInfo = new StringBuilder("[Parameters] ");
		
		for(Double param: metricParams) {
			
			paramsInfo.append(param +" ");
		}
		Log.println("METRIC", paramsInfo.toString());
	}
	
	/**
	 * Initiates or updates the values of the Metric Features (MF).
	 * @param n; should not be null
	 * 
	 * @throws NullPointerException if n is null
	 */
	@Override
	public void updateMF(Node n) {

		System.err.println(String.valueOf(Context.METRIC_3D) +"[WARNING] the method 'agat.metric.bricks.MetricInterface.updateMF(Node n)' is not implemented!");
		System.exit(0);
	}
}
