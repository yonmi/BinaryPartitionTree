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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeMap;

import datastructure.Node;
import datastructure.Tree;

/**
 * A cut performed on a BPT gives a partition regrouping regions at a precised hierarchy level.
 * 
 * <p>
 * Important attributes are:
 * 
 * <li> a set of images that are partitions at each level
 * <li> a set of regions at each level
 *
 */
public class CutResult {

	/**
	 * Hierarchical structure owning the cutting results.
	 */
	public Tree tree;
	
	/**
	 * Set of partitions for each level. 
	 */
	public TreeMap<Integer, BufferedImage> regionImages;
	
	/**
	 * Set of regions contained in a partition for each level.
	 */
	public TreeMap<Integer, ArrayList<Node>> relatedNodes;
	
	/**
	 * Creates and prepares the place where to store the different cutting results.  
	 * 
	 * @param tree on which the cut will be performed; should not be null
	 * 
	 * @throws NullPointerException if tree is null
	 */
	public CutResult(Tree tree) {

		this.tree = tree;
		regionImages = new TreeMap<Integer, BufferedImage>();
		relatedNodes = new TreeMap<Integer, ArrayList<Node>>();
	}
	
	/**
	 * Saving the partition and its regions (~nodes).
	 * 
	 * @param nbRegions index determining the current level; should be > 0
	 * @param regionImage image partition of the current level; should not be null
	 * @param activeNodesList regions of the current level; should not be null
	 * 
	 * @throws NullPointerException if regionImage is null nor activeNodesList is null
	 */
	public void add(int nbRegions, BufferedImage regionImage, ArrayList<Node> activeNodesList) {
		
		ArrayList<Node> list = new ArrayList<Node>();
		list.addAll(activeNodesList);
		regionImages.put(nbRegions, regionImage);
		relatedNodes.put(nbRegions, list);
	}

	/**
	 * 
	 * @param nbRegions used as index determining a specific level
	 * @return the partition having the precised number of regions
	 */
	public BufferedImage get(int nbRegions) { 
		
		return regionImages.get(nbRegions); 
	}

	/**
	 * 
	 * @param nbRegions used as index determining a specific level
	 * @return the set of nodes representing the regions forming the partition at a precised level
	 */
	public ArrayList<Node> getRelatedNodes(int nbRegions){ 
		
		return relatedNodes.get(nbRegions); 
	}

	/**
	 * Delete the partition and the set of regions saved at a precised level
	 * 
	 * @param nbRegions index determining a specific level
	 */
	public void remove(int nbRegions) {
		
		regionImages.remove(nbRegions);
		relatedNodes.remove(nbRegions);
	}
}
