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

package multi.sequential;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import datastructure.Adjacency;
import datastructure.Adjacency.State;
import datastructure.ListW;
import datastructure.Node;
import datastructure.Node.TypeOfNode;
import datastructure.set.SetOfAdjacencies;
import datastructure.set.SetW;
import lang.Strings;
import metric.bricks.Metric.TypeOfMetric;
import multi.strategy.consensus.bricks.Consensus;
import multi.strategy.consensus.bricks.Consensus.ConsensusStrategy;
import multi.strategy.consensus.bricks.ConsensusFactory;
import standard.sequential.BPT;
import standard.sequential.BPT.TypeOfConnectivity;
import utils.ImTool;
import utils.Log;

/**
 * MULTI-FEATURES BINARY PARTITION TREE known as MBPT.
 * 
 * <p> The MBPT is a hierarchical representation of an image as the BPT (Binary Partition Tree).
 * The MBPT is built in a multi-features and / or multi-images fashion although the BPT is based on a mono-feature and mono image startegy.
 * The MBPT aims to built a single unique binary hierarchical tree taking advantages to all the features and the images used while avoiding to merge them.
 * The construction of the MBPT relies on:
 * 
 * <li> the metrics defined by the user according to the chosen features
 * <li> a consensus strategies in order to achieve the choice of the neighboring regions to merge
 *
 */
public class MBPT extends BPT{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Strategy to use.
	 * 
	 * <p> For a common MBPT, only one strategy is used.
	 */
	public ArrayList<Consensus> consensusStrategies = new ArrayList<Consensus>();
	
	/**
	 * Regrouping all images.
	 */
	public ArrayList<BufferedImage> listOfImages;

	/**
	 * Regrouping all listW associating the images to all metrics.
	 */
	public ArrayList<ListW> listOfLists;
	
	/**
	 * Creates an empty multi-featuring binary partition tree.
	 */
	public MBPT() {
		
		super();
		
		this.listOfImages = new ArrayList<BufferedImage>();
		this.listOfLists = new ArrayList<ListW>();
		this.context = "MBPT";
	}
	
	/**
	 * Accepts and records a link between two neighboring regions in the Region Adjacency Graph (RAG).
	 * 
	 * <p>
	 * The RAG models the links (adjacency) between the regions (summit) and their neighbors.
	 * A distance is computed to determine how similar are the neighboring regions. 
	 *  
	 * @param adjacency
	 */
	@Override
	public void add(Adjacency adjacency) {
		
		adjacency.scores = new double[this.listOfLists.size()];
		adjacency.updateChains = new boolean[this.listOfLists.size()];
		adjacency.state = new State[this.listOfLists.size()];
		adjacency.ranks = new int[this.listOfLists.size()];
		adjacency.previous = new Adjacency[this.listOfLists.size()];
		adjacency.next = new Adjacency[this.listOfLists.size()];
		
		Arrays.fill(adjacency.updateChains, true);

		/* Add the adjacency in all listW(s). */
		for(int i = 0; i < this.listOfLists.size(); i++) {

			ListW listW = this.listOfLists.get(i);
			adjacency.scores[listW.getIndex()] = listW.getMetric().computeDistances(adjacency.node1, adjacency.node2);
			listW.add(adjacency);
		}
	}
	
	/**
	 * The Region Adjacency Graph (RAG) defines the links between the regions and their neighbors.
	 * 
	 * <p>
	 * The initial summits of the RAG are  {@link BPT#defineLeaves() the leaves that must be defined earlier}. 
	 * The neighboring is defined by the {@link TypeOfConnectivity type of connectivity} (8-CN or 4-CN) chosen by the user.
	 * The distance associated with each adjacency defines how similar the regions are.
	 * <li> Summit: node
	 * <li> Neighbor link = adjacency
	 */
	@Override
	public void createRAG() {
		
		super.createRAG();
		this.nbInitialAdjacencies = this.listOfLists.get(0).size();
		
		/*
		 * Updating ranks of the adjacency links in each list if the number of lists is more than one. 
		 */
		for(int i = 0; i < this.listOfLists.size(); i++) {

			this.listOfLists.get(i).initRanks();
		}
	}
	
	/**
	 * Links a metric to an image and create the corresponding list / set of adjacency links (e.g. ListW).
	 * 
	 * @param image Identification of the image in the list of source images of the tree.
	 * @param metricType Type of metric to use.
	 */
	public void linkMetricToAnImage(BufferedImage image, TypeOfMetric metricType) {
		
		ListW list = new SetW(this.listOfLists.size(), image, metricType, this.consensusStrategies.get(0));
		this.listOfLists.add(list);
	}
	
	/**
	 * Leaves are the unitary regions initially defined in the image. 
	 * 
	 * <p>
	 * Leaves are the pillars of the BPT creation.
	 * Indeed, the creation is done in a bottom-up fashion starting from the leaves.
	 * They are defined by the labels contained in  {@link BPT#prepareLabelMatrix() the matrix of labels that must be defined earlier}.
	 * 
	 * <p>
	 * Note that the set of leaves can be define from the pixels of an image or a set of regions previously obtained (e.g. presegmentation).
	 * This choice is done when defining the matrix of labels.
	 */
	@Override
	public void defineLeaves() {
		
		/* Prepare the list of leaves */
		this.nodes = new Node[labelMatrix.getNbRegions() + labelMatrix.getNbRegions() - 1];
		Log.println(context, Strings.NB_NODES_TO_CREATE +": "+ this.nodes.length +" (including leaves)");
		Log.println(context, Strings.NB_LEAVES_TO_CREATE +": "+ labelMatrix.getNbRegions());
		
		/* Regroup pixels in the leaves ( */
		for(int y = 0; y < this.labelMatrix.getHeight(); y++) {
			for(int x = 0; x < this.labelMatrix.getWidth(); x++) {
				
				int nodeName = this.labelMatrix.getLabel(x, y);
				Node leaf = this.nodes[nodeName];
				
				/* Create the leaf if it does not exist */
				if(leaf == null) {
					
					Node newLeaf = new Node(nodeName);
					
					/*
					 * Prepare the metric features.
					 * (!) For now, this cannot be done in a parallel fashion.
					 */
					for(int i = 0; i < this.listOfLists.size(); i++) {
						
						this.listOfLists.get(i).getMetric().initMF(newLeaf);
					}
					
					this.nodes[nodeName] = newLeaf;
					this.nbLeaves++;
					
					leaf = newLeaf;
				}
				
				/* Associate the pixels to the leaf */
				leaf.addPixel(x, y);
			}
		}
		
		/*
		 * Update the values of the features within the leaves.
		 * (!) this can be done only after adding all points (~ pixels) in the leaf (~ region)
		 */
		for(int n=0; n < this.nbLeaves; n++) {
			
			Node leaf = this.nodes[n];
			for(int i = 0; i < this.listOfLists.size(); i++) {
				
				this.listOfLists.get(i).getMetric().updateMF(leaf);
			}
		}
	}
	
	/**
	 * The core process of the BPT creation relies on a binary node merging.
	 * 
	 * <p>
	 * For each iteration of the process, the most similar regions (~nodes) are merged.
	 * Such similarity is determined by the distance associated to the adjacency linking two neighboring regions.
	 * The node merging leads to a new node creation and a big update of the RAG (i.e. removing all invalid links, update or remove some).
	 * The node merging process ends when there is only one node (the root) and no link (~adjacency) is left. 
	 * The end of the node merging process means the end of the BPT creation and confirms the obtaining of the root.
	 * 
	 * <p> Note that the node merging process requires {@link BPT#createRAG() the RAG to be already created}.
	 */
	@Override
	public void nodeMergings() {
		
		/* Number of estimated fusions */
		int nbFusions = this.nbLeaves - 1;
		int numFusion = 1;
		
		long mergingStartingTime = System.nanoTime();
		
		/* Merge leaves and nodes until obtaining the root */
		Node root = null;
		while(!this.listOfLists.get(0).isEmpty()) {
			
			this.progress = (numFusion * 100) / nbFusions;
			Log.println(context+"_FUSION", this.progress +"%");
			
			Adjacency potentialAdjacency = this.consensusStrategies.get(0).apply(this.listOfLists);
	
			/* Create a new node */
			Node leftNode = potentialAdjacency.node1;
			Node rightNode = potentialAdjacency.node2;
			Node newNode = new Node(this.nbNodes, leftNode, rightNode);
			newNode.rememberMerginScore(potentialAdjacency.consensusScore);
			this.nodes[this.nbNodes] = newNode;
			this.nbNodes++;
			
			/*
			 * Prepare and initiate features.
			 */
			for(int i = 0; i < this.listOfLists.size(); i++) {
				
				this.listOfLists.get(i).getMetric().initMF(newNode);
				this.listOfLists.get(i).getMetric().updateMF(newNode);
			}
			
			/* Let the two nodes to forget each other and break the link */
			leftNode.removeNeighbor(rightNode);
			rightNode.removeNeighbor(leftNode);
			this.remove(potentialAdjacency);
			
			/* Remove the adjacency links corresponding to the left node*/
			ConcurrentHashMap<Node, Adjacency> neighbors = leftNode.listOfNeighbors;
			for(Entry<Node, Adjacency> entry: neighbors.entrySet()) {
				
				Node neighbor = entry.getKey();
				Adjacency adjacencyNeighbor = entry.getValue();
				this.remove(adjacencyNeighbor);
				neighbor.removeNeighbor(leftNode);
			}
			leftNode.listOfNeighbors = null;
	
			/* Remove the adjacency links corresponding to the right node*/
			neighbors = rightNode.listOfNeighbors;
			for(Entry<Node, Adjacency> entry: neighbors.entrySet()) {
				
				Node neighbor = entry.getKey();
				Adjacency adjacencyNeighbor = entry.getValue();
				this.remove(adjacencyNeighbor);
				neighbor.removeNeighbor(rightNode);
			}
			rightNode.listOfNeighbors = null;			

			/* Generate adjacency links between the new node and its neighbors */
			neighbors = newNode.listOfNeighbors;
			for(Entry<Node, Adjacency> entry: neighbors.entrySet()) {
				
				Node neighbor = entry.getKey();
				this.add(new Adjacency(neighbor, newNode));
			}
			
			numFusion++;
	
			if(this.nbNodes % this.GC_ITERATION == 0) {
				
				Log.println("GC", Strings.CLEANING +"!");
				System.gc();
				System.runFinalization();
			}
			
			/*
			 * Update ranks if the strategy requires it.
			 */
			if(this.consensusStrategies.get(0).needRanks()) {
				
				for(int i = 0; i < this.listOfLists.size(); i++) {
					
					this.listOfLists.get(i).updateRanks();
				}
			}
			
			root = newNode;
			
			/* One iteration test */
//			long oneEndingTime= System.nanoTime();
//			long oneTimeNs = oneEndingTime - mergingStartingTime;
//			long oneTimeMs = oneTimeNs/1000000;
//			long oneTimeS = oneTimeMs / 1000;
//			Log.print("1 iteration", "time: "+ oneTimeNs +" ns | "+ oneTimeMs +" ms | "+ oneTimeS +" s");
//			System.exit(0);
		}
		
		if(root != null) {
			
			root.type = TypeOfNode.ROOT;
		}
		
		long mergingEndingTime= System.nanoTime();
		long mergingTimeMs = (mergingEndingTime - mergingStartingTime)/1000000;
		long mergingTimeS = mergingTimeMs / 1000;
		Log.println(context+"_FUSION", Strings.TIME_OF_CREATION +": "+ mergingTimeMs +" ms | "+ mergingTimeS +" s");
	}
	
	/**
	 * Register an image as one of the source image.
	 * @param image
	 */
	public void registerImage(BufferedImage image) {

		this.listOfImages.add(image);
		
		if(this.image == null) {
			
			this.image = image;
		}
	}
	
	/**
	 * Delete an adjacency from the RAG (Region Adjacency Graph).
	 * 
	 * <p>
	 * Only unused adjacency links have to be stored in the RAG. The remaining has to be definitely removed. 
	 * 
	 * @param adjacency
	 */
	@Override
	public void remove(Adjacency adjacency) {
		
		for(int i = 0; i < this.listOfLists.size(); i++) {
			
			this.listOfLists.get(i).remove(adjacency);
		}
	}

	/**
	 * Set the consensus strategy to use and precise if only a specified first ranks in the lists are considered.
	 * @param consensusStrategy Consensus strategy to use.
	 * @param consensusParams Range of the 1sts adjacency links to consider in all listW(s). (!) If this argument is not set, all of the list content (i.e. 100%) will be considered.
	 */
	public void setConsensusStrategy(ConsensusStrategy consensusStrategy, int... consensusParams) {
		
		int range;
		if(consensusParams.length > 0) range = consensusParams[0];
		else range = 100; // BY DEFAULT, CONSIDER 100% OF THE LIST CONTENT. 

		int progressive = 1;
		if(consensusParams.length > 1) progressive = consensusParams[1];
		
		this.consensusStrategies.add(ConsensusFactory.buildConsensusStrategy(consensusStrategy, range, progressive));
	}
	
	@Override
	public void setParams(String name, BufferedImage image, BufferedImage preSegImage, TypeOfConnectivity connectivity) {

		this.name = name;
		this.image = image;
		this.preSegImage = preSegImage;
		this.connectivity = connectivity;
		
		this.setOfAdjacencies = new SetOfAdjacencies();
		
		Log.show = true;
		Log.println(context, Strings.IMAGE +"=: "+ ImTool.getNameOf(image) +" ("+ image.getWidth() +"x"+ image.getHeight() +")");
		Log.println(context, Strings.DIRECTORY +": "+ directory);
		Log.println(context, Strings.NAME +": "+ name);
		Log.println(context, Strings.CONNEXITY +": "+ connectivity);
	}
}
