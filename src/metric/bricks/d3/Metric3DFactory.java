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

import metric.bricks.d3.Metric3D.TypeOfMetric;
import metric.color.d3.Ominmax3D;
import utils.d3.RGBStruct;

/**
 * 
 * Factory building the right metric objects.
 * Each coded metric class should figure in this class as a choice.
 *
 */
public class Metric3DFactory {

	/**
	 * Chooses and builds the right metric and associates it to an image
	 * @param metricType; should not be null
	 * @param image; should not be null
	 * @return the right metric object
	 * 
	 * @throws NullPointerException if metricType or image is null
	 */
	public static Metric3D initMetric(TypeOfMetric metricType, RGBStruct cube) {

		switch(metricType){
		
			case OMIN_MAX_3D: return new Ominmax3D(cube);
			default: return new Ominmax3D(cube);
		}
	}
}
