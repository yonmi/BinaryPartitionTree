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

import datastructure.Tree;
import standard.sequential.BPT;
import utils.ImTool;
import utils.SaveBPT;

/**
 * Example saving a created BPT in a file having .bpt format.
 *
 */
public class SavingBPTIntoFile {

	public static void main(String[] args) {

		boolean success = false;
		
		try {

			//String path = "xp//examples//six_regions_3_3.png";
			//String path = "xp//pleiade_stbg.tif";
			String path = "xp//examples//border_test.png";
			BufferedImage image = ImTool.read(path);
			
			String presegPath = "xp//examples//border_test.png";
			BufferedImage presegImage = ImTool.read(presegPath);
			
			Tree bpt = new BPT(image);
			bpt.setPreSegImage(presegImage);
			bpt.grow();
			
			if(bpt != null) {
				
				bpt.setName("tree.bpt");
				bpt.setDirectory("xp//examples");

				//bpt.setName("tree2.bpt");
				//bpt.setName("tree3.bpt");
				//bpt.setDirectory("xp");
				
				// Saving the tree structure in a .dot file and info in .css
				SaveBPT.toDOT(bpt);
				
				// Saving the tree structure in a .xml file and info in .css
				SaveBPT.toGRAPHML(bpt);
				
				// Saving the info and the tree structure in a .h5 (hdf5) file
				SaveBPT.toHDF5(bpt);

				success = true;
	
			}
						
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		if(success)	System.out.println("[Test] BPT saved successfully!");
	}
}
