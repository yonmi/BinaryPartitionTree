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

import datastructure.Node;
import datastructure.Tree;
import standard.sequential.BPT;
import utils.TreeVisu;
import utils.d2.Formula;

/**
 * Example loading a BPT from a file.
 *
 */
public class LoadingBPTFromFile {

	public static void main(String[] args) {
		
		String filePath = "xp//examples//tree.bpt.h5";
		//String filePath = "xp//tree3.bpt.h5";
		
		Tree bpt = new BPT(filePath); // re-growing is included.
		
		if(bpt != null) {
			
			System.out.println("[Test] BPT regrow succeded!");
			
			/* Get the pixels of a leaf from the hdf5 file */
			Node leaf = bpt.getNodes()[3];
			int[] points = bpt.getPixels(leaf);
			
			for(int i = 0; i < points.length; ++i){
				
				int p = points[i];
				int x = Formula.toX(p, bpt.getMaxLonger());
				int y = Formula.toY(p, bpt.getMaxLonger());
				System.out.println("point: "+ p +" x: " + x +" y: "+ y);
			}
		}
		
		// Drawing the tree in a file
		Node root = bpt.getRoot();
		String savePath = "xp//tree3-visu.txt";
		TreeVisu.display(root, savePath);
	}
}
