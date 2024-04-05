/****************************************************************************
* Copyright (2023)						       
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

package standard.sequential;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ch.systemsx.cisd.hdf5.IHDF5Reader;
import datastructure.Adjacency;
import datastructure.Node;
import datastructure.Node.TypeOfNode;
import datastructure.d3.Tree3D;
import datastructure.set.AdjacencySet;
import datastructure.set.AdjacencySet.OptimalOption;
import datastructure.set.SetOfAdjacencyBuckets;
import lang.Strings;
import metric.bricks.d3.Metric3D;
import metric.color.d3.Ominmax3D;
import utils.ImTool;
import utils.Log;
import utils.d3.LabelMatrix3D;
import utils.d3.RGBStruct;
import utils.d3.Voxel;

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
public class BPTVS implements Tree3D, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The active nodes represents the regions forming a partition of an image.
	 * 
	 * <p>
	 * At a specific level of the hierarchy, the partition corresponds to a coarse or a detailed segmentation of an image.
	 * A parent and its children could not be contained in the same partition.
	 */
	public ArrayList<Node> activeNodes = new ArrayList<Node>();
	
	/**
	 * List of adjacencies to treat later.
	 */
	private Set<Adjacency> adjacenciesBuffer = new HashSet<Adjacency>();

	/**
	 * Defines the size of the biggest leaf.
	 */
	public int biggestLeafSize;
	
	/**
	 * For a logging purpose, a context is useful to determine the current process.
	 */
	public String context = "BPTV";

	/**
	 * Determines the method of neighbors detection.
	 */
	public TypeOfConnectivity connectivity;

	/**
	 * States if the BPT creation process is finished or not.
	 */
	public boolean ended;
	
	/**
	 * The garbage collector (GC) is invoked manually after some numbers of iterations. 
	 */
	public final int GC_ITERATION = 100000;
	
	/**
	 * Image to represent with a hierarchical structure.
	 */
	public RGBStruct cube;

	/**
	 * A matrix of labels needed for generating the images containing the depicted regions.
	 */
	public LabelMatrix3D labelMatrix3D;
	
	/**
	 * Maximum depth value
	 */
	public int maxDepth;
	
	/**
	 * Maximum between the width and the height of the image.
	 */
	public int maxLonger;

	/**
	 * Metric computing the similarity distance between two regions.
	 * By default, the RADIOMETRIC_MIN_MAX is used.
	 */
	public Metric3D metric3D;

	/**
	 * Used essentially for BPT file saving.
	 */
	public String name;

	/**
	 * According to the type of neighbor connectivity chosen, the number of the initial adjacencies can vary.  
	 */
	public int nbInitialAdjacencies;

	/**
	 * The number of leaves depends on the set of leaves used to start the BPT creation.
	 * 
	 * <p>
	 * Set of leaves:
	 * <li> from pixels
	 * <li> from a set of regions (e.g. pre-segmented image)
	 */
	public int nbLeaves;

	/**
	 * The number of regions (leaves, nodes and root) currently created.
	 */
	public int nbNodes;

	/**
	 * The set of all nodes of the tree (leaves, nodes, root).
	 * 
	 * <p> A leaf/node corresponds to a region in the hierarchy.
	 */
	public Node[] nodes;
	
	/**
	 * Name of the current building or cutting process
	 */
	public String processName;

	/**
	 * BPT process progression
	 */
	public int progress;
	
	/**
	 * Accessing the HDF5 file.
	 */
	public IHDF5Reader reader;

	/**
	 * A specific data structure used to store the links between neighbors.
	 * Known as adjacency links that are crucial to the BPT creation.
	 * 
	 * <p>
	 * This set must be maintained and ordered.
	 * Its capacity is not limited but a big size of it could terribly affect the performance of the BPT creation process.
	 */
	protected AdjacencySet setOfAdjacencies;

	/**
	 * The amount of time, in ms, needed for the BPT creation. 
	 */
	public long timeMs;

	/**
	 * The amount of time, in s, needed for the BPT creation. 
	 */
	public long timeS;

	/**
	 * Precise if the optimal distance value to consider for mergin nodes is the MAXIMUM or the MINIMUM.
	 */
	private OptimalOption optimalOption = OptimalOption.MINIMUM; // by default

	
	/**
	 * Prepares an empty tree.
	 * 
	 * @see BPT#BPT(BufferedImage) prepares a BPT creation from one image
	 * @see BPT#BPT(BufferedImage, TypeOfConnectivity) prepares a BPT creation from one image while precising the neighbor type of connectivity
	 */
	public BPTVS() {
		
		this.processName = Strings.PLANTING_A_SEED;
		this.connectivity = TypeOfConnectivity.CN6;
	}
	
	/**
	 * Prepares a BPT from one image.
	 * 
	 * <p>
	 * The default region similarity metric used is the "radiometric".
	 * The default region connection is 8 neighbors (8-CN).
	 * 
	 * <p>
	 * Example:
	 * <pre>
	 * String path = "images/myImage.png";
	 * BufferedImage image = ImTool.read(path);
	 * BPT bpt = new BPT(image);
	 * bpt.grow();
	 * </pre>
	 * 
	 * @param image must not be null
	 * @throws NullPointerException if image is null
	 * 
	 * @see BPT#BPT() prepares an empty BPT creationnbGrayLevel][nbGray][256];
	 * @see BPT#BPT(String) prepares a BPT to be re-grown from a HDF5 file.
	 * @see BPT#BPT(BufferedImage, TypeOfConnectivity) prepares a BPT creation from one image while precising the neighbor type of connectivity
	 */
	public BPTVS(RGBStruct cube) {
		
		this.processName = Strings.PLANTING_A_SEED;
		
		this.cube = cube;
		this.connectivity = TypeOfConnectivity.CN6;
		
		Log.println(context, Strings.RGB_CUBE);
		Log.println(context, Strings.CONNEXITY +": "+ this.connectivity);
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
		
		adjacency.computeDistance(this.metric3D);
		this.setOfAdjacencies.add(adjacency);
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
		
		long ragStartingTime = System.nanoTime();
		
		/* Create adjacency edges between the leaves */
		Log.println(Strings.RAG, Strings.CREATING_ADJACENCIES);
		this.setOfAdjacencies = new SetOfAdjacencyBuckets(this.optimalOption);
		
		this.nbNodes = 0;
		while(this.nbNodes < this.nbLeaves) {
			
			Node leaf = this.nodes[this.nbNodes];
			
			ArrayList<Voxel> listOfVoxels = leaf.getVoxels();
			for(Voxel voxel: listOfVoxels){
				
				int xVoxel = voxel.x;
				int yVoxel = voxel.y;
				int zVoxel = voxel.z;
				int labelVoxel = this.labelMatrix3D.getLabel(xVoxel, yVoxel, zVoxel);
				Node leafContainingVoxel = this.nodes[labelVoxel];
				
				int[][] coords = ImTool.getConnectedNeibhorgs(6, voxel); // CN6
				
				for(int i = 0; i < coords.length; i++) {
					
					int xNeighbor = coords[i][0];
					int yNeighbor = coords[i][1];
					int zNeighbor = coords[i][2];
//					System.out.println("voxel pos x: "+ xNeighbor +" y: "+ yNeighbor +" z: "+ zNeighbor);
					this.treat(leafContainingVoxel, voxel, labelVoxel, xNeighbor, yNeighbor, zNeighbor);
				}
			}
			
			this.nbNodes++;
		}
		this.nbInitialAdjacencies = this.getNbAdjacencies();

		/* Add all new adjacencies in the RAG */
		for(Adjacency adja: this.adjacenciesBuffer) {
			
			this.add(adja);
		}
		this.adjacenciesBuffer.clear();
		
		long ragEndingTime = System.nanoTime();
		long ragTimeMs = (ragEndingTime - ragStartingTime)/1000000;
		long ragTimeS = ragTimeMs / 1000;
		Log.println(Strings.RAG, Strings.TIME_OF_CREATION +": "+ ragTimeMs +" ms | "+ ragTimeS +" s");
	}

	/**
	 * Leaves are the smallest regions initially defined in the image. 
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
		
		int estimatedNbLeaves = labelMatrix3D.getNbRegions() * 2 - 1;

		
		/* Prepare the list of leaves */
		this.nodes = new Node[estimatedNbLeaves];
		Log.println(context, Strings.NB_NODES_TO_CREATE +": "+ this.nodes.length +" (including leaves)");
		Log.println(context, Strings.NB_LEAVES_TO_CREATE +": "+ this.labelMatrix3D.getNbRegions());
		
		/* Regroup voxels in the leaves */
		for(int y = 0; y < this.labelMatrix3D.getHeight(); y++) {
			for(int x = 0; x < this.labelMatrix3D.getWidth(); x++) {
				for(int z = 0; z < this.labelMatrix3D.getDepth(); z++) {
				
					int nodeName = this.labelMatrix3D.getLabel(x, y, z);
					Node leaf = this.nodes[nodeName];
					
					/* Create the leaf if it does not exist */
					if(leaf == null) {
						
						leaf = new Node(nodeName);
						leaf.label = nodeName; 
						this.metric3D.initMF(leaf);
						this.nodes[nodeName] = leaf;
						this.nbLeaves++;
					}				
					leaf.type = TypeOfNode.LEAF;
					
					/* Associate the pixels to the leaf */
					leaf.addVoxel(x, y, z);
					int lsize = leaf.getNbVoxels();
					if(this.biggestLeafSize < lsize) {
						
						this.biggestLeafSize = lsize;
					}
				}
			}
		}
		
		/*
		 * Update the values of the features within the leaves.
		 * (!) this can be done only after adding all points (~ pixels) in the leaf (~ region)
		 */
		for(int i=0; i < this.nbLeaves; i++) {
			
			Node leaf = this.nodes[i];
			this.metric3D.updateMF(leaf);
		}
	}

	@Override
	public void endingState() {

		this.ended = true;
	}
	
	@Override
	public int getBiggestLeafSize() {

		return this.biggestLeafSize;
	}

	@Override
	public TypeOfConnectivity getConnectivity() {

		return this.connectivity;
	}

	public LabelMatrix3D getLabelMatrix3D() {

		return this.labelMatrix3D;
	}

	/**
	 * 
	 * @return the set of leaves used to start the BPT creation process.
	 */
	@Override
	public Node[] getLeaves() {
		
		if(this.labelMatrix3D == null) {
			
			this.labelMatrix3D = ImTool.getLabelMatrix3DOf(this.cube); /* initiated from voxels */
		}
		
		Node[] leaves = new Node[this.labelMatrix3D.getNbInitialRegions()];
		int leafLabel = 0;
		while(leafLabel < leaves.length) {
			leaves[leafLabel] = this.nodes[leafLabel];
			leafLabel++;
		}
		return leaves;
	}

	@Override
	public Metric3D getMetric3D() {
	
		return this.metric3D;
	}

	@Override
	public String getName() {

		return this.name;
	}

	/**
	 * 
	 * @return The number of actual existing adjacency links between neighbors.
	 */
	@Override
	public int getNbAdjacencies() {
		
		return this.setOfAdjacencies.size();
	}

	@Override
	public Integer getNbInitialAdjacencies() {

		return this.nbInitialAdjacencies;
	}

	@Override
	public int getNbLeaves() {

		return this.nbLeaves;
	}

	/**
	 * 
	 * @return the number of regions (leaves, nodes and root) of the BPT that are currently created.
	 */
	@Override
	public int getNbNodes() { 
		
		return this.nbNodes; 
	}

	@Override
	public Node getNode(int index) {

		return this.nodes[index];
	}

	@Override
	public Node[] getNodes() {

		return this.nodes;
	}
	
	@Override
	public String getProcessName() {
		
		return this.processName;
	}

	@Override
	public int getProgress() {

		return this.progress;
	}
	
	@Override
	public IHDF5Reader getReader() {

		return this.reader;
	}
	
	@Override
	public RGBStruct getCube() {

		return this.cube;
	}
	
	@Override
	public Node getRoot() {

		return this.nodes[this.getNbNodes()-1];
	}

	@Override
	public Long getTimeMs() {

		return this.timeMs;
	}

	@Override
	public Long getTimeS() {

		return this.timeS;
	}

	/**
	 * Creates a tree in a bottom-up fashion.
	 * 
	 * <p>
	 * <li> Defines the leaves
	 * <li> Builds the RAG
	 * <li> Merge regions (~nodes)
	 * <li> Defines the root
	 */
	@Override
	public void grow() {
		
		/* Reset the static position of metric features,
		 *  useful when you want to create more than one BPT*/
		Metric3D.currentFeaturePos = -1;
		
		if(this.metric3D == null) { // Default metric
			
			this.metric3D = new Ominmax3D(this.cube);
		}
		
		this.processName = Strings.STARTING_TO_GROW;
		Log.println(context, Strings.STARTING_TREE_CREATION);	
		long startingTime = System.nanoTime();
		
		this.processName = Strings.PREPARING_LABEL_MATRIX;
		this.prepareLabelMatrix3D();
		
		this.processName = Strings.PREPARING_LEAVES;
		this.defineLeaves();
		Log.println(context, Strings.NB_LEAVES_CREATED +": "+ this.nbLeaves);
		
		this.processName = Strings.CREATING_ADJACENCIES;
		this.createRAG();
		//System.exit(0);
		Log.println(context, Strings.NB_ADJACENCIES_GENERATED +": "+ this.nbInitialAdjacencies);
		
		this.processName = Strings.MERGING_NODES;
		this.nodeMergings();
		
		long endingTime = System.nanoTime();
		this.timeMs = (endingTime - startingTime)/1000000;
		this.timeS = this.timeMs / 1000;
		Log.println(context, Strings.NB_REMAINING_ADJACENCIES +": "+ this.getNbAdjacencies());
		Log.println(context, Strings.NB_NODES_CREATED +": "+ this.nbNodes);		
		Log.println(context, Strings.TREE_CREATION_IN +" "+ this.timeMs +" ms ("+ this.timeS +" s)/n");
		
/*		System.out.println("colour: ["+ ((Ocolcont) this.metric).ocolMinScore +", "+ ((Ocolcont) this.metric).ocolMaxScore +"]");
		System.out.println("contour: ["+ ((Ocolcont) this.metric).ocontMinScore +", "+ ((Ocolcont) this.metric).ocontMaxScore +"]");
		System.exit(0); */
		
		this.processName = Strings.FINALIZING;
		this.ended = true;
	}

	@Override
	public boolean hasEnded() {

		return this.ended;
	}

	/**
	 * Determines whether a pixel defined with a coordinates x,y is contained in the cube or not.
	 * 
	 * @param x index of the column
	 * @param y index of the row
	 * @param z index of the depth
	 * @return true if inside and false if outside
	 * 
	 */
	@Override
	public boolean isInStudiedAread(int x, int y, int z) {
	
		return (x >= 0 && y >=0 && z >= 0 && x < this.cube.getxLevels() && y < this.cube.getyLevels() && z < this.cube.getzLevels());
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
		
		long mergingStartingTime = System.nanoTime();
		
		/* Number of estimated fusions */
		int nbFusions = this.nbLeaves - 1;
		int numFusion = 1;
		
		/* Merge leaves and nodes until obtaining the root */
		Node newNode = null;
		while(!this.setOfAdjacencies.isEmpty()) {
			
			this.progress = (numFusion * 100) / nbFusions;
			Log.println(context +"_FUSION", this.progress +"%");
			
			Adjacency potentialAdjacency = this.setOfAdjacencies.optimalAdjacency();
	
			/* Create a new node */
			Node leftNode = potentialAdjacency.node1;
			Node rightNode = potentialAdjacency.node2;
			newNode = new Node(this.nbNodes, leftNode, rightNode);
			
			/* TODO for now it not optimized */
			newNode.listOfVoxels.addAll(leftNode.getVoxels());
			newNode.listOfVoxels.addAll(rightNode.getVoxels());
			newNode.nbVoxels += leftNode.nbVoxels + rightNode.nbVoxels;
			
			newNode.type = TypeOfNode.NODE;
			newNode.rememberMerginScore(potentialAdjacency.distance);
			this.metric3D.initMF(newNode);
			this.metric3D.updateMF(newNode);
			this.nodes[this.nbNodes] = newNode;
			this.nbNodes++;
			
			/* set the node level and the tree max depth */
			newNode.updateLvl(Math.max(newNode.leftNode.lvl, newNode.rightNode.lvl) + 1);
			if(this.maxDepth < newNode.lvl) this.maxDepth = newNode.lvl;
			
			/* Let the two nodes to forget each other and break the link */
			leftNode.removeNeighbor(rightNode);
			rightNode.removeNeighbor(leftNode);
			this.remove(potentialAdjacency);
			
			/* Generate adjacencies between the new node and its neighbors */
			ConcurrentHashMap<Node, Adjacency> neighbors = newNode.listOfNeighbors;
			for(Entry<Node, Adjacency> entry: neighbors.entrySet()) {
				
				Node neighbor = entry.getKey();
				Adjacency adjacency = new Adjacency(neighbor, newNode);
				this.add(adjacency);
			}	
	
			/* Remove the adjacencies corresponding to the left node*/
			neighbors = leftNode.listOfNeighbors;
			for(Entry<Node, Adjacency> entry: neighbors.entrySet()) {
				
				Node neighbor = entry.getKey();
				Adjacency adjacencyNeighbor = entry.getValue();
				this.remove(adjacencyNeighbor);
				neighbor.removeNeighbor(leftNode);
				
				Adjacency createdAdjacency = neighbor.listOfNeighbors.get(newNode);
				createdAdjacency.frontier += adjacencyNeighbor.frontier;
			}
			leftNode.listOfNeighbors = null;
	
			/* Remove the adjacencies corresponding to the right node*/
			neighbors = rightNode.listOfNeighbors;
			for(Entry<Node, Adjacency> entry: neighbors.entrySet()) {
				
				Node neighbor = entry.getKey();
				Adjacency adjacencyNeighbor = entry.getValue();
				this.remove(adjacencyNeighbor);
				neighbor.removeNeighbor(rightNode);

				Adjacency createdAdjacency = neighbor.listOfNeighbors.get(newNode);
				createdAdjacency.frontier += adjacencyNeighbor.frontier;
			}
			rightNode.listOfNeighbors = null;
			
			numFusion++;
	
			if(this.nbNodes % this.GC_ITERATION == 0) {
				
				Log.println("GC", Strings.CLEANING +"!");
				System.gc();
				System.runFinalization();
			}
		}
		
		if(newNode != null) {
			
			newNode.type = TypeOfNode.ROOT;
		}
		
		long mergingEndingTime= System.nanoTime();
		long mergingTimeMs = (mergingEndingTime - mergingStartingTime)/1000000;
		long mergingTimeS = mergingTimeMs / 1000;
		Log.println(context+"_FUSION", Strings.TIME_OF_MERGINGS +": "+ mergingTimeMs +" ms | "+ mergingTimeS +" s");
	}

	/**
	 * Use the matrix of labels of the image.
	 * 
	 * <p>
	 * The matrix is initially filled from the pixels (i.e. one pixel, one specific label).
	 */
	@Override
	public void prepareLabelMatrix3D() {
		
		this.labelMatrix3D = ImTool.getLabelMatrix3DOf(this.cube);
	}
	
	/**
	 * Delete an adjacency from the RAG (Region Adjacency Graph).
	 * 
	 * <p>
	 * Only unused adjacencies have to be stored in the RAG. The remaining has to be definitely removed. 
	 * 
	 * @param adjacency
	 */
	@Override
	public void remove(Adjacency adjacency) {
		
		this.setOfAdjacencies.remove(adjacency);
	}

	@Override
	public void setCube(RGBStruct cube) {

		this.cube = cube;
	}

	@Override
	public void setMetric3D(Metric3D metric3D) {

		this.metric3D = metric3D;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public void setParams(String name, RGBStruct cube, TypeOfConnectivity connectivity) {

		this.name = name;
		this.cube = cube;
		this.connectivity = connectivity;
		
		Log.show = true;
		Log.println(context, Strings.NAME +": "+ name);
		Log.println(context, Strings.CONNEXITY +": "+ connectivity);
	}

	@Override
	public void setParams(String name, RGBStruct cube, Metric3D metric3D, TypeOfConnectivity connectivity) {

		this.name = name;
		this.cube = cube;
		this.metric3D = metric3D;
		this.connectivity = connectivity;
		
		Log.show = true;
		Log.println(context, Strings.NAME +": "+ name);
		Log.println(context, Strings.CONNEXITY +": "+ connectivity);
	}

	@Override
	public void setProgress(int p) {
	
		this.progress = p;
	}

	@Override
	public void startingState() {
	
		this.ended = false;
	}

	
	/**
	 * Creates an adjacency if the required conditions are gathered.
	 * 
	 * @param leafContainingVoxel
	 * @param voxel
	 * @param labelVoxel
	 * @param xNeighbor
	 * @param yNeighbor
	 * @param zNeighbor
	 */
	private void treat(Node leafContainingVoxel, Voxel voxel, int labelVoxel, int xNeighbor, int yNeighbor, int zNeighbor) {
	
		int xVoxel = voxel.x;
		int yVoxel = voxel.y;
		int zVoxel = voxel.z;
		
		/* Do only something for a Neighboring Voxel in the studied area ant if it is not the Voxel itself */
		if(this.isInStudiedAread(xNeighbor, yNeighbor, zNeighbor) && (xNeighbor != xVoxel || yNeighbor != yVoxel || zNeighbor != zVoxel)) {
			
			int labelNeighbor = this.labelMatrix3D.getLabel(xNeighbor, yNeighbor, zNeighbor);
			Node neighbor = this.nodes[labelNeighbor];
			
			if(labelNeighbor != labelVoxel) {
				
				Adjacency adjacency = leafContainingVoxel.listOfNeighbors.get(neighbor);
				
				if(adjacency == null) { // Create the new adjacency
				
					adjacency = new Adjacency(neighbor, leafContainingVoxel); // will be added later
					this.adjacenciesBuffer.add(adjacency);
				}
			}	
		}
	}
}
