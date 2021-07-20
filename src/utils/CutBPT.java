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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import datastructure.CutResult;
import datastructure.Node;
import datastructure.Tree;

/**
 * As a BPT is a hierarchical data structure representation of an image, the objects of interest can be represented in each hierarchy level.
 * In order to depict them, a strategy partitioning each level is required.
 * Such strategy could be a cut performed on the tree on each level.
 * The BPT cut can be considered as a segmentation method as it provide as a result a partitioned image that could be a segmentation result.
 *
 */
public class CutBPT {
	
	private static final String CONTEXT = "Tree-CUT";

	/**
	 * Performs a simple (horizontal) cut on the tree.
	 * 
	 * @param tree hierarchical structure representation of an image; should not be null
	 * @param starting number of regions of the first partition to generate and to store in the cut result; should be > 0 and > ending
	 * @param ending number of regions of the last partition to generate and to store in the cut result; should be > 0 and < starting
	 * @param step number of regions between two partitions to generate; if step is 0, only one partitioned image corresponding to the starting parameter is generated
	 * @return a {@link CutResult cut result} containing:
	 * 
	 * <li> a set of partitions corresponding to each defined hierarchy level
	 * <li> a set of nodes corresponding to all regions of each hierarchy level
	 * 
	 * @throws NullPointerException if tree is null
	 */
	public static CutResult execute(Tree tree, int starting, int ending, int step) {
		
		CutResult res = new CutResult(tree);
		
		BufferedImage regions;
		
		tree.prepareLabelMatrix();
		LabelMatrix labelMatrix = tree.getLabelMatrix();
		
		/* list of active nodes */
		ArrayList<Node> activeNodesList = new ArrayList<Node>();
		
		int nbLeaves = tree.getNbLeaves();

		/* preparing the leaves and the colors */
		Node[] nodes = tree.getNodes();
		HashMap<Integer, Color> randomColors = new HashMap<Integer, Color>();
		Color color;
		Random rand = new Random();
		for(int i = 0; i < nbLeaves; i++) {
			
			Node leaf = nodes[i];
			if(leaf.getSize() > 0) {
			
				activeNodesList.add(leaf);
				labelMatrix.fill(leaf.getPixels(), leaf.label);

				if(!randomColors.containsKey(leaf.label)) {

					float r = rand.nextFloat();
					float g = rand.nextFloat();
					float b = rand.nextFloat();

					color = new Color(r, g, b);
					randomColors.put(leaf.label, color);
				}
			}
		}
		
		if(starting > nbLeaves) {
			
			starting = nbLeaves;
		}
		
		/* if the number of regions matches the starting */
		if(starting == nbLeaves || ending == nbLeaves) {
			
			regions = ImTool.generateRegions(labelMatrix, randomColors);
			res.add(nbLeaves, regions, activeNodesList);
			
			if(step == 0) {
				
				return res;
			}
		}
		
		/* Number of estimated fusions */
		int nbFusions = tree.getNbLeaves() - 1;
		int numFusion = 1;
		
		/* node merging simulation */
		int numberOfRegions = nbLeaves;
		for(int n = nbLeaves; n < tree.getNbNodes(); n++) {
			
			tree.setProgress((numFusion * 100) / nbFusions);
			Log.println(CONTEXT, tree.getProgress() +"%");

			Node node = nodes[n];

			if(node.getSize() > 0) {

				activeNodesList.remove(node.rightNode);
				activeNodesList.remove(node.leftNode);
				activeNodesList.add(node);

				numberOfRegions--;

				if(starting >= numberOfRegions) {

					for(int i = 0; i < activeNodesList.size(); i++) {
						
						Node activeNode = activeNodesList.get(i);
						labelMatrix.fill(activeNode.getPixels(), activeNode.label);
					}

					if(starting == numberOfRegions) {

						regions = ImTool.generateRegions(labelMatrix, randomColors); 
						res.add(numberOfRegions, regions, activeNodesList);

					}

					if(step != 0) {

						if(numberOfRegions % starting % step == 0){

							regions = ImTool.generateRegions(labelMatrix, randomColors); 
							res.add(numberOfRegions, regions, activeNodesList);

						}

					}

					if(numberOfRegions == ending) {

						regions = ImTool.generateRegions(labelMatrix, randomColors); 
						res.add(numberOfRegions, regions, activeNodesList);
						return res;
					}
				}

				numFusion++;
			}
		}
		tree.endingState();
		return res;
	}
}
