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

package examples.standard.sequential;

import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import datastructure.CutResult;
import datastructure.Tree;
import metric.color.Omselab;
import metric.color.Owsdm;
import metric.bricks.Metric;
import metric.bricks.Metric.TypeOfMetric;
import metric.bricks.MetricFactory;
import metric.color.Ominmax;
import metric.color.Omse;
import metric.color.RadiometricAverage;
import metric.combination.Ocolcont;
import standard.sequential.BPT;
import ui.ImFrame;
import utils.CutBPT;
import utils.ImTool;
import utils.Log;

/**
 * Example of a simple cutting on the BPT built using the RADIOMETRIC_AVERAGE metric.
 *
 */
public class CuttingBPT_RadiometricAverage {

	public static void main(String[] args) {

		//String path = "xp//examples//six_regions_3_3.png";
		//String path = "xp//examples//42049.png";
		//String path = "xp//examples//weizmann1obj/100_0109.png";
		String path = "xp//examples/pigeon.png";
		BufferedImage image = ImTool.read(path);
		
		String presegPath = "xp//examples/pigeon_preseg.tif";
		BufferedImage presegImage = ImTool.read(presegPath);
		
		Log.show = true;
		Tree bpt = new BPT(image);
		bpt.setPreSegImage(presegImage);
		//bpt.setMetric(new RadiometricAverage(image));
		
		//Metric metric = MetricFactory.initMetric(TypeOfMetric.OCOL_CONT_MIN_MAX, image);
		//Metric metric = MetricFactory.initMetric(TypeOfMetric.OCOL_CONT_MSE, image);
		//Metric metric = MetricFactory.initMetric(TypeOfMetric.OCOL_CONT_MSE_LAB, image);
		Metric metric = MetricFactory.initMetric(TypeOfMetric.OCOL_CONT_WSDM, image);
		bpt.setMetric(metric);

		//bpt.setMetric(new Ominmax(image));
		//bpt.setMetric(new Omse(image));
		//bpt.setMetric(new Owsdm(image));
		
		//double alpha = 0.5; 
		//bpt.setMetric(new Ocolcont(image, TypeOfMetric.OMIN_MAX, alpha));
		//bpt.setMetric(new Ocolcont(image, TypeOfMetric.OMSE, alpha));
		//bpt.setMetric(new Ocolcont(image, TypeOfMetric.OWSDM, alpha));
		
		//bpt.setMetric(new Omselab(image));
		//bpt.setMetric(new Ocolcont(image, TypeOfMetric.OMSE_LAB, alpha));
		
		bpt.grow();
		
		if(bpt.hasEnded()) {
			
			System.out.println("[Test] BPT Creation succeded!");
		}
		
		int starting = 25;
		int ending = 0;
		int step = 1;
		CutResult cutResult = CutBPT.execute(bpt, starting, ending, step);
		System.out.println("[Test] BPT Cutting finished!");
		
		for(Entry<Integer, BufferedImage> entry: cutResult.regionImages.entrySet()) {
			
			int numberOfRegions = entry.getKey();
			BufferedImage partition = entry.getValue();
			
			ImTool.show(partition, ImFrame.IMAGE_DEFAULT_SIZE, numberOfRegions +" regions");			
		}
	}
}
