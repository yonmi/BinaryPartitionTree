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

package standard.parallel.bricks;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import datastructure.Adjacency;
import datastructure.Node;
import datastructure.Node.TypeOfNode;
import datastructure.set.SetOfAdjacencies;
import lang.Strings;
import standard.parallel.BPTP;
import standard.parallel.BPTP.Side;
import standard.sequential.BPT.TypeOfConnectivity;
import utils.LabelMatrix;
import utils.Log;

public class IndividualTask extends Thread {
	
	private AtomicInteger waitingRequest = new AtomicInteger(0);

	private boolean inform = false;
	private boolean running;
	private boolean ended = false;

	BPTP tree;

	Object lock = new Object();

	SetOfAdjacencies adjaSet;
	
	public Side side;
	
	public IndividualTask(SetOfAdjacencies adjaSet, BPTP tree, Side side, boolean inform) {
		
		this.adjaSet = adjaSet;
		this.tree = tree;
		this.side = side;
		this.inform = inform;
	}
	
	public void dismissWaitingRequest() {
	
		waitingRequest.set(0);
	}

	@Override
	public void run() {

		running = true;
		
		/* Prepare the array of nodes */
		adjaSet.nbLeaves = (adjaSet.xMax - adjaSet.xMin) * (adjaSet.yMax - adjaSet.yMin);
		int nbNodes = (adjaSet.nbLeaves * 2) - 1;
		adjaSet.nodes = new Node[nbNodes];
		adjaSet.labelMatrix = new LabelMatrix();
		adjaSet.labelMatrix.setNbRegions(adjaSet.nbLeaves);
		adjaSet.labelMatrix.setLabels(new int[adjaSet.xMax - adjaSet.xMin][adjaSet.yMax - adjaSet.yMin]);
		
		/* Prepare the list of foreign nodes */
		adjaSet.foreignNodes = new ConcurrentHashMap<Integer, Node>();
	
		/* Regroup pixels in the leaves and detect the frontier nodes */
		Node leaf;
		adjaSet.currentNodeIndex = 0;
		for(int y = adjaSet.yMin; y < adjaSet.yMax; y++) {
			for(int x = adjaSet.xMin; x < adjaSet.xMax; x++) {
				
				/* Create the leaf from pixels */
				leaf = new Node(adjaSet.currentNodeIndex);
				tree.metric.initMF(leaf);
				adjaSet.nodes[adjaSet.currentNodeIndex] = leaf;
				leaf.setSide(side);
				
				/* Add the pixel within the leaf */
				leaf.addPixel(x, y);
				adjaSet.labelMatrix.setLabel(adjaSet.currentNodeIndex, x - adjaSet.xMin, y - adjaSet.yMin);
				
				/* Define the frontier */
				if(adjaSet.xMin == 0) { // columns
					
					if(adjaSet.xMax < tree.image.getWidth() && x == adjaSet.xMax - 1) {
						
						leaf.setFrontier(true);
					}
				}else {
					
					if(x == adjaSet.xMin) {

						leaf.setFrontier(true);
					}
				}
				
				if(adjaSet.yMin == 0) { // rows
					
					if(adjaSet.yMax < tree.image.getHeight() && y == adjaSet.yMax - 1) {
						
						leaf.setFrontier(true);
					}
				}else {
					
					if(y == adjaSet.yMin) {

						leaf.setFrontier(true);
					}
				}

				adjaSet.currentNodeIndex++;
			}
		}
		Log.println(side+"", Strings.NB_LEAVES_CREATED +": "+ adjaSet.currentNodeIndex);
		
		/*
		 * Update the values of the features within the leaves.
		 * (!) this can be done only after adding all points (~ pixels) in the leaf (~ region)
		 */
		for(int i=0; i < adjaSet.currentNodeIndex; i++) {
			
			leaf = adjaSet.nodes[i];
			tree.metric.updateMF(leaf);
		}
		
		/* Create adjacency edges between the leaves */
		Log.println(side+"", "RAG-Creating adjacencies ... ");
		adjaSet.currentNodeIndex = 0;
		while(adjaSet.currentNodeIndex < adjaSet.nbLeaves) {
			
			leaf = adjaSet.nodes[adjaSet.currentNodeIndex];
			ArrayList<Point> listOfPixels = leaf.getPixels();
			boolean frontier = leaf.isFrontier;
			for(int ip = 0; ip < listOfPixels.size(); ip++){
				
				Point pixel = listOfPixels.get(ip);
				
				int xPixel = (int) pixel.getX();
				int yPixel = (int) pixel.getY();
				int labelPixel = adjaSet.labelMatrix.getLabel(xPixel - adjaSet.xMin, yPixel - adjaSet.yMin);
				Node leafContainingPixel = adjaSet.nodes[labelPixel];
				
				if(tree.connectivity == TypeOfConnectivity.CN8) {
					
					/* 8 connectivities */
					for(int yNeighbor = yPixel-1; yNeighbor <= yPixel+1; yNeighbor++) {
						for(int xNeighbor = xPixel-1; xNeighbor <= xPixel+1; xNeighbor++) {
							if((xNeighbor != xPixel || yNeighbor != yPixel) && adjaSet.isInStudiedAread(xNeighbor, yNeighbor)) {

								int labelNeighbor = adjaSet.labelMatrix.getLabel(xNeighbor - adjaSet.xMin, yNeighbor - adjaSet.yMin);
								Node neighbor = adjaSet.nodes[labelNeighbor];

								if(labelNeighbor != labelPixel) {
									
									Adjacency newAdjacency = new Adjacency(neighbor, leafContainingPixel);
									adjaSet.add(newAdjacency, tree.metric);
								}
							}
						}
					}
					
					/* case of a frontier pixel */
					if(frontier) {
						
						if(adjaSet.xMin == 0) { // columns
							
							if(adjaSet.xMax < tree.image.getWidth() && pixel.getX() == adjaSet.xMax - 1) {

								int x = xPixel + 1;
								int y = yPixel - 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel + 1;
								y = yPixel;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel + 1;
								y = yPixel + 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

							}
						}else {
							
							if(pixel.getX() == adjaSet.xMin) {

								int x = xPixel - 1;
								int y = yPixel - 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel - 1;
								y = yPixel;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel - 1;
								y = yPixel + 1;
								manageAdjaFrontier(x, y, leafContainingPixel);
																		
							}
						}
						
						if(adjaSet.yMin == 0) { // rows
							
							if(adjaSet.yMax < tree.image.getHeight() && pixel.getY() == adjaSet.yMax - 1) {
							
								int x = xPixel - 1;
								int y = yPixel + 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel;
								y = yPixel + 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel + 1;
								y = yPixel + 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

							}
						}else {
							
							if(pixel.getY() == adjaSet.yMin) {

								int x = xPixel - 1;
								int y = yPixel - 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel;
								y = yPixel - 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

								x = xPixel + 1;
								y = yPixel - 1;
								manageAdjaFrontier(x, y, leafContainingPixel);

							}
						}
					}
					
				}else { // TODO - manage frontiers for 4-cn

					/* 4 connectivities */
					int[][] coords = new int[4][2];
					coords[0][0] = xPixel;
					coords[0][1] = yPixel - 1;
					coords[1][0] = xPixel - 1;
					coords[1][1] = yPixel;
					coords[2][0] = xPixel + 1;
					coords[2][1] = yPixel;
					coords[3][0] = xPixel;
					coords[3][1] = yPixel + 1;
					
					for(int i = 0; i < coords.length; i++) {

						int xNeighbor = coords[i][0];
						int yNeighbor = coords[i][1];
						
						if((xNeighbor != xPixel || yNeighbor != yPixel) && adjaSet.isInStudiedAread(xNeighbor, yNeighbor)) {

							int labelNeighbor = adjaSet.labelMatrix.getLabel(xNeighbor - adjaSet.xMin, yNeighbor - adjaSet.yMin);
							Node neighbor = adjaSet.nodes[labelNeighbor];

							if(labelNeighbor != labelPixel) {
								
								Adjacency newAdjacency = new Adjacency(neighbor, leafContainingPixel);
								adjaSet.add(newAdjacency, tree.metric);
							}
						}
					}
				}
			}
			
			adjaSet.currentNodeIndex++;
		}
		Log.println(side+"", "Nb Adjacencies generated: "+ adjaSet.size());
		
		/* Number of estimated fusions */
		int nbFusions = adjaSet.nbLeaves - 1;
		int numFusion = 1;
		
		/* Merge leaves and nodes until obtaining the root */
		int index = adjaSet.currentNodeIndex;
		Node newNode = null;
		while(!adjaSet.isEmpty()) {

			if(!adjaSet.isEmpty()) {

				Adjacency potentialAdjacency = adjaSet.optimalAdjacency(); 
				Node node1 = potentialAdjacency.node1;
				Node node2 = potentialAdjacency.node2;

				this.removeAdja(potentialAdjacency);

				if(node1.listOfNeighbors != null && node2.listOfNeighbors != null) {

					/* Create a new node */
					newNode = new Node(index, node1, node2);
					tree.metric.initMF(newNode);
					tree.metric.updateMF(newNode);
					newNode.rememberMerginScore(potentialAdjacency.distance);
					newNode.setSide(side);

					/* Let the two nodes forget each other and break the link */
					node1.removeNeighbor(node2);
					node2.removeNeighbor(node1);

					/* Remove the adjacencies of the neighbors of node1 */ 
					for(Entry <Node, Adjacency> entry: node1.listOfNeighbors.entrySet()) {

						Node neighborNode = entry.getKey();
						Adjacency neighborAdja = entry.getValue();
						neighborNode.removeNeighbor(node1);
						if(neighborAdja.isRegistered()) {

							if(neighborAdja.sideAdjaSet != null) {

								neighborAdja.sideAdjaSet.remove(neighborAdja);
							}
							neighborAdja.unregister();
						}

						/* Generate adjacencies between the new node and its neighbors */
						Adjacency newAdja = new Adjacency(neighborNode, newNode);
						adjaSet.add(newAdja, tree.metric);
						newAdja.sideAdjaSet = adjaSet;
					}
					node1.listOfNeighbors = null;

					/* Remove the adjacencies of the neighbors of node2 */ 
					for(Entry<Node, Adjacency> entry: node2.listOfNeighbors.entrySet()) {

						Node neighborNode = entry.getKey();
						Adjacency neighborAdja = entry.getValue();

						neighborNode.removeNeighbor(node2);

						if(!neighborNode.listOfNeighbors.containsKey(newNode)) {

							if(neighborAdja.isRegistered()) {

								if(neighborAdja.sideAdjaSet != null) {

									neighborAdja.sideAdjaSet.remove(neighborAdja);
								}
								neighborAdja.unregister();
							}

							/* Generate adjacencies between the new node and its neighbors */
							Adjacency newAdja = new Adjacency(neighborNode, newNode);
							adjaSet.add(newAdja, tree.metric);
							newAdja.sideAdjaSet = adjaSet;
						}
					}
					node2.listOfNeighbors = null;
					numFusion++;

					if(inform) {

						if(adjaSet.currentNodeIndex % tree.GC_ITERATION == 0) {

							Log.println("GC", "Cleaning!");
							System.gc();
							System.runFinalization();
						}

						long progress = (numFusion * 100) / nbFusions;
						tree.progress = (int) progress;
						Log.println("FUSION", progress +"%");
					}

					adjaSet.nodes[index] = newNode;
					index++;
				}
			}				
		}
		
		if(newNode != null) {
			
			newNode.type = TypeOfNode.ROOT;
		}
	}

	public boolean running() { return this.running && !this.ended ; }

	public void waitingRequest() { waitingRequest.set(1); }

	private SetOfAdjacencies getConcernedAdjaSet(int x, int y) {
	
		for(Entry<Side, SetOfAdjacencies> entry: tree.sideResource.entrySet()) {
			
			if(entry.getValue().isInStudiedAread(x, y)) {
				
				return entry.getValue();
			}
		}
		
		return null;
	}

	private void manageAdjaFrontier(int x, int y, Node node) {

		try {
			
			SetOfAdjacencies concernedAdjaSet = this.getConcernedAdjaSet(x, y);

			int labelNeighbor = concernedAdjaSet.labelMatrix.getLabel(x - concernedAdjaSet.xMin, y - concernedAdjaSet.yMin);
			Node neighbor = concernedAdjaSet.nodes[labelNeighbor];
			
			if(!node.listOfForeignNeighbors.containsKey(neighbor)) {

				Adjacency newAdja = new Adjacency(node.name, neighbor.name);
				newAdja.computeDistance(tree.metric);
				node.listOfForeignNeighbors.put(neighbor, newAdja);
				neighbor.listOfForeignNeighbors.put(node, newAdja);
			}
			
		}catch(Exception e) {
			
			System.err.println("Frontier managing: over flow \n");
		}
	}

	private void removeAdja(Adjacency adjacency) {

		adjacency.sideAdjaSet.remove(adjacency);
		adjacency.unregister();
	}
}