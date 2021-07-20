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

package metric.bricks;

import java.awt.image.BufferedImage;

import metric.bricks.Metric.TypeOfMetric;
import metric.color.NdviMetric;
import metric.color.NdwiMetric;
import metric.color.RadiometricAverage;
import metric.color.Ominmax;
import metric.color.Omse;
import metric.color.Owsdm;
import metric.combination.MmCompacity;
import metric.combination.MmFelNdvi;
import metric.combination.MmFelNdviNdwi;
import metric.combination.MmNdvi;
import metric.combination.MmNdviNdwi;
import metric.combination.MmNdwi;
import metric.combination.Ocolcont;
import metric.others.Orandom;
import metric.shape.Elongation;
import metric.shape.FastCompactness;
import metric.shape.FastSmoothness;
import metric.shape.Ocontour_;
import metric.shape.Smoothness;
import metric.vector.VectorialDistance;

/**
 * 
 * Factory building the right metric objects.
 * Each coded metric class should figure in this class as a choice.
 *
 */
public class MetricFactory {

	/**
	 * Chooses and builds the right metric and associates it to an image
	 * @param metricType; should not be null
	 * @param image; should not be null
	 * @return the right metric object
	 * 
	 * @throws NullPointerException if metricType or image is null
	 */
	public static Metric initMetric(TypeOfMetric metricType, BufferedImage image) {

		switch(metricType){
		
			case RADIOMETRIC_MIN_MAX: return new Ominmax(image);
			case RADIOMETRIC_AVERAGE: return new RadiometricAverage(image);
			case PRECISED_ELONGATION: return new Elongation(TypeOfMetric.PRECISED_ELONGATION, image);
			case SIMPLE_ELONGATION: return new Elongation(TypeOfMetric.SIMPLE_ELONGATION, image);
			case FAST_ELONGATION: return new Elongation(TypeOfMetric.FAST_ELONGATION, image);
			case SMOOTHNESS: return new Smoothness(image);
			case FAST_SMOOTHNESS: return new FastSmoothness(image);	
			case FAST_COMPACTNESS: return new FastCompactness(image);	
			case NDVI: return new NdviMetric(image);
			case NDWI: return new NdwiMetric(image);
			case CL_MM_NDVI: return new MmNdvi(image);
			case CL_MM_NDWI: return new MmNdwi(image);
			case CL_MM_COMPACTNESS: return new MmCompacity(image);
			case CL_MM_NDVI_NDWI:return new MmNdviNdwi(image);
			case CL_MM_FEL_NDVI: return new MmFelNdvi(image);
			case CL_MM_FEL_NDVI_NDWI: return new MmFelNdviNdwi(image);
			case VECTORIAL_DISTANCE: return new VectorialDistance(image);
			case ORANDOM: return new Orandom(image);
			case OMIN_MAX: return new Ominmax(image);
			case OMSE: return new Omse(image);
			case OWSDM: return new Owsdm(image);
			case OCONTOUR: return new Ocontour_(image);
			case OCOL_CONT_MIN_MAX: return new Ocolcont(image, TypeOfMetric.OMIN_MAX, 0.5);
			case OCOL_CONT_MSE: return new Ocolcont(image, TypeOfMetric.OMSE, 0.5);
			case OCOL_CONT_MSE_LAB: return new Ocolcont(image, TypeOfMetric.OMSE_LAB, 0.5);
			case OCOL_CONT_WSDM: return new Ocolcont(image, TypeOfMetric.OWSDM, 0.5);
			default: return new Ominmax(image);
		}
	}
}
