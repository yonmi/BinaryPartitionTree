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

package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import datastructure.Node;

public class TreeVisu {

    /**
     * Shows the structure of tree and store it into a file
     * @param root of the tree 
     * @param savePath file to create to store the structure  of the tree; if null the structure is not saved
     */
	public static void display(Node root, String savePath) {
		
		if(savePath != null){ /* saving */
        	/* Create the file and start to write in it. */
			try{

				PrintWriter writer = new PrintWriter(savePath, "UTF-8");
				show(root, 0, new ArrayList<Integer>(), writer);
				writer.close();

			}catch(IOException e){
				e.printStackTrace();
			}			
        }else /* not saving */
        	show(root, 0, new ArrayList<Integer>(), null);
		
	}
    
	/**
	 * Core method drawing the tree structure
	 * @param n node from which the drawing starts
	 * @param lvl from which the drawing starts
	 * @param bracket list remembering each block
	 * @param writer needed if the structure has to be stored in a file
	 */
	private static void show(Node n, int lvl, ArrayList<Integer> bracket, PrintWriter writer){
		
		String line = "`--";
		String space = "";
		String indent = "  ";
		String bar = " |";
		ArrayList<Integer> bracketRight = new ArrayList<Integer>();
		bracketRight.addAll(bracket);
		
		if(n != null){
			
			StringBuilder s = new StringBuilder();
			for(int i=0; i<lvl; ++i){

				if(bracket.contains(i))
					space = space + bar;
				else space = space + indent;

			}
			
			s.append(space).append(line).append(n.name+":"+n.lvl);
			System.out.println(s);
			if(writer != null)
				writer.println(s);
			
			bracket.add(lvl);
			lvl += 1;
			show(n.leftNode, lvl, bracket, writer);
			show(n.rightNode, lvl, bracketRight, writer);
			
		}	
	}
}
