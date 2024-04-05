/****************************************************************************
* Copyright AGAT-Team (2023)						       
* 									    
* Contributors:								
* B. Naegel								    
* K. Kurtz								    
* N. Passat								    
* J.F. Randrianasoa							    
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

package examples.standard.sequential.d3;

import standard.sequential.BPTVS;
import ui.ImFrame;
import utils.ImTool;
import utils.ImTool.CubeFace;
import utils.Log;
import utils.TreeVisu;
import utils.d3.CutBPT3D;
import utils.d3.CutResult3D;
import utils.d3.RGBStruct;

import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import datastructure.d3.Tree3D;

/**
 * Example showing a standard BPT Value Set creation from one RGB Cube using RADIOMETRIC_AVERAGE metric.
 *
 */
public class CreateAndCutBPTVS {

	public static void main(String[] args) {
		
		Log.show = true;
		
		/* Creation */
		String dir = "";
		String name = "RGB Cube";
		String path = "";
		RGBStruct cube = new RGBStruct(10); // few levels only || out of memory issue 
		ImTool.register3D(cube, dir, name, path);
		
		Tree3D bptvs = new BPTVS(cube);
		bptvs.grow();
		
		if(bptvs.hasEnded()) {
			
			System.out.println("[Test] BPTVS Creation succeded with the "+ bptvs.getMetric3D().type +" metric!");
		}
		
		/* Cut */
		int starting = 10;
		int ending = 0;
		int step = 1;
		
		/* XY  */
		CubeFace cubeFace = CubeFace.XY;
		CutResult3D cutResult3D = CutBPT3D.execute(bptvs, starting, ending, step, cubeFace);
		System.out.println("[Test] BPTVS Cutting finished!");
				
		for(Entry<Integer, BufferedImage> entry: cutResult3D.partitions.entrySet()) {
			
			int numberOfRegions = entry.getKey();
			BufferedImage partition = entry.getValue();
			
			ImTool.show(partition, ImFrame.IMAGE_DEFAULT_SIZE, numberOfRegions +" regions");
			ImTool.save(partition, "xp//bptvs//bptvs_"+ numberOfRegions +"_"+ cubeFace.toString() +".png");
		}
		
		/* XZ  */
		cubeFace = CubeFace.XZ;
		cutResult3D = CutBPT3D.execute(bptvs, starting, ending, step, cubeFace);
		System.out.println("[Test] BPTVS Cutting finished!");
				
		for(Entry<Integer, BufferedImage> entry: cutResult3D.partitions.entrySet()) {
			
			int numberOfRegions = entry.getKey();
			BufferedImage partition = entry.getValue();
			
			ImTool.show(partition, ImFrame.IMAGE_DEFAULT_SIZE, numberOfRegions +" regions");
			ImTool.save(partition, "xp//bptvs//bptvs_"+ numberOfRegions +"_"+ cubeFace.toString() +".png");
		}
		
		/* YZ  */
		cubeFace = CubeFace.YZ;
		cutResult3D = CutBPT3D.execute(bptvs, starting, ending, step, cubeFace);
		System.out.println("[Test] BPTVS Cutting finished!");
				
		for(Entry<Integer, BufferedImage> entry: cutResult3D.partitions.entrySet()) {
			
			int numberOfRegions = entry.getKey();
			BufferedImage partition = entry.getValue();
			
			ImTool.show(partition, ImFrame.IMAGE_DEFAULT_SIZE, numberOfRegions +" regions");
			ImTool.save(partition, "xp//bptvs//bptvs_"+ numberOfRegions +"_"+ cubeFace.toString() +".png");
		}
		
		/* Basic tree visualization */
		TreeVisu.display(bptvs.getRoot(), null);
	}
}
