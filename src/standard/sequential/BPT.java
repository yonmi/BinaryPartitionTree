package standard.sequential;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FilenameUtils;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import datastructure.Adjacency;
import datastructure.Node;
import datastructure.Tree;
import datastructure.Node.TypeOfNode;
import datastructure.set.AdjacencySet;
import datastructure.set.SetOfAdjacencyBuckets;
import lang.Strings;
import metric.bricks.Metric;
import metric.bricks.MetricFactory;
import metric.bricks.Metric.TypeOfMetric;
import metric.color.Ominmax;
import utils.Formula;
import utils.ImTool;
import utils.LabelMatrix;
import utils.Log;
import utils.SegmentByConnexityRaw;


/**
 * Binary tree representing an image as a hierarchy of regions.
 * Such hierarchy can be used for image segmentation purposes.
 * 
 * <p>
 * One tree is built from one image and one user defined region similarity metric.
 * The tree is in a bottom up fashion from a set of leaves to the root through binary region mergings.
 * The set of leaves can be a set of pixels or a set of regions obtained from a pre-segmentation.
 * Two similar regions are merged according to a user defined metric.
 * 
 */
public class BPT implements Tree, Serializable{

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
	public String context = "BPT";

	/**
	 * Determines the method of neighbors detection.
	 */
	public TypeOfConnectivity connectivity;

	/**
	 * To save the BPT in a file, a directory is needed.
	 */
	public String directory;

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
	public transient BufferedImage image;
	
	/**
	 * Location of the image
	 */
	public String imgPath;

	/**
	 * A matrix of labels needed for generating the images containing the depicted regions.
	 */
	public LabelMatrix labelMatrix;
	
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
	public Metric metric;

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
	 * Leaves can be defined as the set of pixels but also a set of pre-segmented regions previously obtained.
	 */
	public transient BufferedImage preSegImage;

	/**
	 * Location of the pre-segmented image
	 */
	public String preSegPath;
	
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
	 * To determine the neighbors of a region, the type of connectivity has to be known.
	 * 
	 * <p>
	 * <li> CN8 or 8-CN considers all 8 neighbors that a middle pixel can have (*).
	 * <li> CN4 or 4-CN considers only 4 neighbors corresponding to vertical and horizontal ones (+). </br></br>
	 * 
	 * <p>
	 * Example:
	 * <pre>
	 * 1 2 3  |  Neighbors of 5 considering 8-CN: 1, 2, 3, 4, 6, 7, 8, 9 </br>
	 * 4 5 6  |  Neighbors of 5 considering 4-CN: 2, 4, 6, 8 </br>
	 * 7 8 9  |
	 * </pre>
	 *
	 */
	public enum TypeOfConnectivity{
		
		CN8,
		CN4;

		/**
		 * 
		 * @param text referencing the type
		 * @return the type corresponding to the text
		 */
		public static TypeOfConnectivity valueFrom(String text) {

			if (text.equals(Strings.FOUR_CN)) {

				return TypeOfConnectivity.CN4;
			}		

			return TypeOfConnectivity.CN8;
		}
	}

	/**
	 * Prepares an empty tree.
	 * 
	 * @see BPT#BPT(BufferedImage) prepares a BPT creation from one image
	 * @see BPT#BPT(BufferedImage, TypeOfConnectivity) prepares a BPT creation from one image while precising the neighbor type of connectivity
	 */
	public BPT() {
		
		this.processName = Strings.PLANTING_A_SEED;
		this.connectivity = TypeOfConnectivity.CN8;
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
	 * @see BPT#BPT() prepares an empty BPT creation
	 * @see BPT#BPT(String) prepares a BPT to be re-grown from a HDF5 file.
	 * @see BPT#BPT(BufferedImage, TypeOfConnectivity) prepares a BPT creation from one image while precising the neighbor type of connectivity
	 */
	public BPT(BufferedImage image) {
		
		this.processName = Strings.PLANTING_A_SEED;
		
		this.image = image;
		this.imgPath = ImTool.getPathOf(image);
		this.directory = ImTool.getDirOf(image);
		this.connectivity = TypeOfConnectivity.CN8;
		
		int imgW = image.getWidth();
		int imgH = image.getHeight();
		this.maxLonger = imgW;
		if(this.maxLonger < imgH) this.maxLonger = imgH;
		
		Log.println(context, Strings.IMAGE+": "+ ImTool.getNameOf(this.image) +" ("+ this.image.getWidth() +"x"+ this.image.getHeight() +")");
		Log.println(context, Strings.DIRECTORY +": "+ this.directory);
		Log.println(context, Strings.CONNEXITY +": "+ this.connectivity);
	}
	
	/**
	 * Prepares a BPT to be re-grown from a HDF5 file. 
	 * 
	 * <p>
	 * Example:
	 * <pre>
	 * String filePath = "saves/bpt_file.h5";
	 * BPT bpt = new BPT(Path);
	 * </pre>	 
	 * No need to regrow.
	 * 
	 * @param filePath leading to the saved BPT; should not be null
	 * 
	 * @throws NullPointerException if filePath is null
	 * 
	 * @see BPT#BPT() prepares an empty BPT creation
	 * @see BPT#BPT(BufferedImage) prepares a BPT creation from one image
	 * @see BPT#BPT(BufferedImage, TypeOfConnectivity) prepares a BPT creation from one image while precising the neighbor type of connectivity
	 */
	public BPT(String filePath) {
		
		this.processName = Strings.PLANTING_A_SEED;
		
		this.reader = HDF5Factory.openForReading(filePath);
		this.name = this.reader.readString(Strings.VAR_INFO +"/"+ Strings.VAR_NAME);
		this.directory = this.reader.readString(Strings.VAR_INFO +"/"+ Strings.VAR_DIRECTORY);
		this.imgPath = this.reader.readString(Strings.VAR_INFO +"/"+ Strings.VAR_IMAGE_PATH);
		this.imgPath = FilenameUtils.separatorsToSystem(this.imgPath);
		this.image = ImTool.read(this.imgPath);
		try{
			this.preSegPath = this.reader.readString(Strings.VAR_INFO +"/"+ Strings.VAR_PRESEG_PATH);
//			this.preSegPath = "xp/PR2020/DATA/weizmann1obj/slic/img_3083_modif.tif";
			this.preSegPath = FilenameUtils.separatorsToSystem(this.preSegPath);
		}catch(Exception e) {/* no preseg */};
//		System.out.println("presegpath: "+ this.preSegPath);
		try{this.preSegImage = ImTool.read(this.preSegPath);}catch(Exception e) {/* no preseg */}
//		System.exit(0);
		this.connectivity = TypeOfConnectivity.valueOf(this.reader.readString(Strings.VAR_INFO +"/"+ Strings.VAR_CONNEXITY));
		this.nbInitialAdjacencies = this.reader.readInt(Strings.VAR_INFO +"/"+ Strings.VAR_NB_INITIAL_ADJACENCIES);
		this.nbNodes = this.reader.readInt(Strings.VAR_INFO +"/"+ Strings.VAR_NB_NODES);
		this.nbLeaves = this.reader.readInt(Strings.VAR_INFO +"/"+ Strings.VAR_NB_LEAVES);
		this.biggestLeafSize = this.reader.readInt(Strings.VAR_INFO +"/"+ Strings.VAR_BIGGEST_LEAF_SIZE);
		this.timeMs = this.reader.readLong(Strings.VAR_INFO +"/"+ Strings.VAR_TIME_OF_CREATION_MS);
		this.timeS = this.reader.readLong(Strings.VAR_INFO +"/"+ Strings.VAR_TIME_OF_CREATION_S);
		this.maxLonger = this.reader.readInt(Strings.VAR_INFO +"/"+ Strings.VAR_MAX_LONGER);
		
		String metricInfo = this.reader.readString(Strings.VAR_INFO +"/"+ Strings.VAR_METRIC);
		String splitMetricInfo[] = metricInfo.split(";");
		String metricName = splitMetricInfo[0];
		ArrayList<Double> metricParams = new ArrayList<Double>();
		for(int i = 1; i < splitMetricInfo.length; ++i) {
			
			metricParams.add(Double.valueOf(splitMetricInfo[i]));
		}
		TypeOfMetric metricType = TypeOfMetric.valueOf(metricName);
		this.metric = MetricFactory.initMetric(metricType, this.image);
		this.metric.setParams(metricParams);
		
		Log.println(context, Strings.IMAGE+": "+ ImTool.getNameOf(this.image) +" ("+ this.image.getWidth() +"x"+ this.image.getHeight() +")");
		Log.println(context, Strings.DIRECTORY +": "+ this.directory);
		Log.println(context, Strings.CONNEXITY +": "+ this.connectivity);
		
		this.regrow();
	}
	
	/**
	 * Prepares a BPT from one image while precising the neighbor type of connectivity.
	 * 
	 * <p>
	 * The default region similarity metric used is the "radiometric".
	 * 
	 * <p>
	 * Example:
	 * <pre>
	 * String path = "images/myImage.png";
	 * BufferedImage image = ImTool.read(path);
	 * BPT bpt = new BPT(image, TypeOfConnectivity.CN4);
	 * bpt.grow();
	 * </pre>
	 * 
	 * @param image must not be null
	 * @param connectivity neighboring connection
	 * 
	 * @throws NullPointerException if image is null or connectivity is null
	 * 
	 * @see BPT#BPT() prepares an empty BPT creation
 	 * @see BPT#BPT(String) prepares a BPT to be re-grown from a HDF5 file 
	 * @see BPT#BPT(BufferedImage) prepares a BPT creation from one image
	 */
	public BPT(BufferedImage image, TypeOfConnectivity connectivity) {
		
		this(image);
		this.connectivity = connectivity;
		Log.println(context, Strings.CONNEXITY +" "+ Strings.CHANGED +" : "+ this.connectivity);
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
		
		adjacency.computeDistance(this.metric);
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
		this.setOfAdjacencies = new SetOfAdjacencyBuckets();
		
		this.nbNodes = 0;
		while(this.nbNodes < this.nbLeaves) {
			
			Node leaf = this.nodes[this.nbNodes];
			
			ArrayList<Point> listOfPixels = leaf.getPixels();
			for(int ip = 0; ip < listOfPixels.size(); ip++){
				
				Point pixel = listOfPixels.get(ip);
			
				int xPixel = pixel.x;
				int yPixel = pixel.y;
				int labelPixel = this.labelMatrix.getLabel(xPixel, yPixel);
				Node leafContainingPixel = this.nodes[labelPixel];
				
				if(this.connectivity == TypeOfConnectivity.CN8) {
					
					/* 8 connectivities */
					for(int yNeighbor = yPixel-1; yNeighbor <= yPixel+1; yNeighbor++) {
						for(int xNeighbor = xPixel-1; xNeighbor <= xPixel+1; xNeighbor++) {
							
							this.treat(leafContainingPixel, pixel, xPixel, yPixel, labelPixel, xNeighbor, yNeighbor);
						}
					}
				}else {
	
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
						this.treat(leafContainingPixel, pixel, xPixel, yPixel, labelPixel, xNeighbor, yNeighbor);
					}
				}
			}
			
			this.nbNodes++;
		}
		this.nbInitialAdjacencies = this.getNbAdjacencies();
/*		for(int i = 0; i < this.nbLeaves; ++i) {
			
			int perimeter = this.nodes[i].perimeter;
			System.out.println("Perimeter of n-"+ i +": "+ perimeter);
			
			System.out.println("For each regions: ");
			for(Entry<Node, Adjacency> entry: this.nodes[i].listOfNeighbors.entrySet()) {
				
				Adjacency adjacency = entry.getValue();
				System.out.println("adja-"+ adjacency.getIndex() +" frontier: "+ adjacency.frontier);
			}
		}
 */	
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
		
		int estimatedNbLeaves = labelMatrix.getNbRegions() + labelMatrix.getNbRegions() - 1;
		
		if(this.preSegImage != null){
			
			/* Get all the connected regions contained in the preseg image */
			SegmentByConnexityRaw sbc = new SegmentByConnexityRaw(this.preSegImage);
			this.labelMatrix = sbc.runForFullImage();
			estimatedNbLeaves = this.labelMatrix.getNbInitialRegions();
			
			/* saving the preseg label matrix -- only if needed */
			HashMap<Integer, Color> lut = new HashMap<Integer, Color>();
			BufferedImage pixelRegions = ImTool.generateRegions(this.labelMatrix, lut);
			ImTool.save(pixelRegions, ImTool.getDirOf(this.preSegImage) +"//"+ ImTool.getNameOf(this.preSegImage) +".png");
		}
		
		/* Prepare the list of leaves */
		this.nodes = new Node[estimatedNbLeaves];
		Log.println(context, Strings.NB_NODES_TO_CREATE +": "+ this.nodes.length +" (including leaves)");
		Log.println(context, Strings.NB_LEAVES_TO_CREATE +": "+ this.labelMatrix.getNbRegions());
		
		/* Regroup pixels in the leaves */
		for(int y = 0; y < this.labelMatrix.getHeight(); y++) {
			for(int x = 0; x < this.labelMatrix.getWidth(); x++) {
				
				int nodeName = this.labelMatrix.getLabel(x, y);
				Node leaf = this.nodes[nodeName];
				
				/* Create the leaf if it does not exist */
				if(leaf == null) {
					
					leaf = new Node(nodeName);
					leaf.label = nodeName; 
					this.metric.initMF(leaf);
					this.nodes[nodeName] = leaf;
					this.nbLeaves++;
				}				
				leaf.type = TypeOfNode.LEAF;
				
				/* Associate the pixels to the leaf */
				leaf.addPixel(x, y);
				int lsize = leaf.getSize();
				if(this.biggestLeafSize < lsize) {
					
					this.biggestLeafSize = lsize;
				}
			}
		}
		
		/*
		 * Update the values of the features within the leaves.
		 * (!) this can be done only after adding all points (~ pixels) in the leaf (~ region)
		 */
		for(int i=0; i < this.nbLeaves; i++) {
			
			Node leaf = this.nodes[i];
			this.metric.updateMF(leaf);
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
	
	@Override
	public String getDirectory() {

		return this.directory;
	}

	@Override
	public BufferedImage getImage() {

		return this.image;
	}

	@Override
	public String getImagePath() {

		return this.imgPath;
	}

	@Override
	public LabelMatrix getLabelMatrix() {

		return this.labelMatrix;
	}

	/**
	 * 
	 * @return the set of leaves used to start the BPT creation process.
	 */
	@Override
	public Node[] getLeaves() {
		
		if(this.labelMatrix == null) {
			
			this.labelMatrix = ImTool.getLabelMatrixOf(this.image); /* initiated from pixels */
		}
		
		Node[] leaves = new Node[this.labelMatrix.getNbInitialRegions()];
		int leafLabel = 0;
		while(leafLabel < leaves.length) {
			leaves[leafLabel] = this.nodes[leafLabel];
			leafLabel++;
		}
		return leaves;
	}
	
	@Override
	public int getMaxLonger() {

		return this.maxLonger;
	}

	@Override
	public Metric getMetric() {
	
		return this.metric;
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
	public int[] getPixels(Node leaf) {

		int[] points = null;
		
		if(this.reader != null) {
			
			int[][] row = reader.int32().readMatrixBlock(Strings.VAR_STRUCTURE +"/"+ Strings.VAR_LEAVES, 1, leaf.getSize(), leaf.name, 1);
			points = row[0];
		}
		
		return points;
	}

	@Override
	public BufferedImage getPreSegImage() {

		return this.preSegImage;
	}

	@Override
	public String getPreSegPath() {

		return this.preSegPath;
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
		Metric.currentFeaturePos = -1;
		
		if(this.metric == null) { // Default metric
			
			this.metric = new Ominmax(image);
		}
		
		this.processName = Strings.STARTING_TO_GROW;
		Log.println(context, Strings.STARTING_TREE_CREATION);	
		long startingTime = System.nanoTime();
		
		this.processName = Strings.PREPARING_LABEL_MATRIX;
		this.prepareLabelMatrix();
		
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
		Log.println(context, Strings.TREE_CREATION_IN +" "+ this.timeMs +" ms ("+ this.timeS +" s)\n");
		
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
	 * Determines whether a pixel defined with a coordinates x,y is contained in the image or not.
	 * 
	 * @param x index of the column
	 * @param y index of the row
	 * @return true if inside and false if outside
	 * 
	 */
	@Override
	public boolean isInStudiedAread(int x, int y) {
	
		return (x >= 0 && y >=0 && x < this.image.getWidth() && y < this.image.getHeight());
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
			newNode.type = TypeOfNode.NODE;
			newNode.rememberMerginScore(potentialAdjacency.distance);
			this.metric.initMF(newNode);
			this.metric.updateMF(newNode);
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
	public void prepareLabelMatrix() {
		
		this.labelMatrix = ImTool.getLabelMatrixOf(this.image);
	}
	
	/**
	 * Regenerate a BPT tree from an HDF5 file by rebuilding 
	 * the tree in a bottom-up fashion from a file.
	 * 
	 * <p>
	 * <li> Defines the leaves
	 * <li> Merge regions (~nodes)
	 * <li> Defines the root
	 * 
	 */
	private void regrow() {
		
		this.setOfAdjacencies = new SetOfAdjacencyBuckets();
		
		this.processName = Strings.STARTING_TO_GROW;
		Log.println(context, Strings.STARTING_TREE_CREATION);	
		long startingTime = System.nanoTime();
		
		this.processName = Strings.PREPARING_LEAVES;
		/* Prepare the list of leaves */
		this.nodes = new Node[this.nbNodes];
		Log.println(context, Strings.NB_NODES_TO_CREATE +": "+ this.nodes.length +" (including leaves)");
		Log.println(context, Strings.NB_LEAVES_TO_CREATE +": "+ this.nbLeaves);
		
		/* Regroup pixels in the leaves */
		int leafIndex = 0;
		for(int i = 0; i < this.nbLeaves; ++i) {
		
			leafIndex = i;
			Node l = new Node(i);
			l.label = i;
			l.type = TypeOfNode.LEAF;
			int[][] nbPixelCell = this.reader.int32().readMatrixBlock(Strings.VAR_STRUCTURE +"/"+ Strings.VAR_LEAVES,
																	  1, 1, i, 0);
			l.nbPixels = nbPixelCell[0][0];
			
			int[][] pixelLocations = this.reader.int32().readMatrixBlock(Strings.VAR_STRUCTURE +"/"+ Strings.VAR_LEAVES, 
																		 1, (l.nbPixels+1), i, 0);
			int lastI = l.nbPixels;
			for(int k = 1; k <= lastI; ++k) {
				
				int posVal = pixelLocations[0][k];
				int x = Formula.toX(posVal, this.maxLonger);
				int y = Formula.toY(posVal, this.maxLonger);
				l.addPixel(x, y);
			}
			this.nodes[i] = l;
		}
		Log.println(context, Strings.NB_LEAVES_CREATED +": "+ (leafIndex + 1) +"/"+ this.nbLeaves);
		
		this.processName = Strings.MERGING_NODES;
		
		/* Number of estimated fusions */
		int nbFusions = this.nbLeaves - 1;
		int numFusion = 1;
		
		/* Merge leaves and nodes until obtaining the root */
		Node n = null;
		for(int i = 0; i < nbFusions; ++i) {
			
			this.progress = (numFusion * 100) / nbFusions;
			Log.println(context +"_FUSION", this.progress +"%");
			
			int[][] childMatrix = this.reader.int32().readMatrixBlock(Strings.VAR_STRUCTURE +"/"+ Strings.VAR_NODES,
																	  1, 4, i, 0);
			int nodeName = childMatrix[0][0];
			Node l = this.nodes[childMatrix[0][1]];
			Node r = this.nodes[childMatrix[0][2]];
			n = new Node(nodeName, l, r);
			n.type = TypeOfNode.NODE;
			n.perimeter = childMatrix[0][3];
			
			/* set the node level and the tree max depth */
			n.updateLvl(Math.max(l.lvl, r.lvl) + 1);
			if(this.maxDepth < n.lvl) this.maxDepth = n.lvl;
			
			this.nodes[nodeName] = n;
			
			numFusion++;			
		}
		
		if(n != null) {
			
			n.type = TypeOfNode.ROOT;
		}
		
		long endingTime = System.nanoTime();
		long regrowTimeMs = (endingTime - startingTime)/1000000;
		long regrowTimeS = regrowTimeMs / 1000;
		Log.println(context, Strings.NB_ADJACENCIES_GENERATED +": "+ this.getNbAdjacencies());
		Log.println(context, Strings.NB_NODES_CREATED +": "+ (leafIndex + numFusion));		
		Log.println(context, Strings.TREE_CREATION_IN +" "+ regrowTimeMs +" ms ("+ regrowTimeS +" s)\n");
		
		this.processName = Strings.FINALIZING;
		this.ended = true;
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
	public void setDirectory(String dir) {

		this.directory = dir;
	}

	@Override
	public void setImage(BufferedImage image) {

		this.image = image;
	}

	@Override
	public void setMetric(Metric metric) {

		this.metric = metric;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public void setParams(String name, BufferedImage image, BufferedImage preSegImage, TypeOfConnectivity connectivity) {

		this.name = name;
		this.image = image;
		this.preSegImage = preSegImage;
		this.connectivity = connectivity;
		
		Log.show = true;
		Log.println(context, Strings.IMAGE +"=: "+ ImTool.getNameOf(image) +" ("+ image.getWidth() +"x"+ image.getHeight() +")");
		Log.println(context, Strings.DIRECTORY +": "+ directory);
		Log.println(context, Strings.NAME +": "+ name);
		Log.println(context, Strings.CONNEXITY +": "+ connectivity);
	}

	@Override
	public void setParams(String name, BufferedImage image, BufferedImage preSegImage, Metric metric, TypeOfConnectivity connectivity) {

		this.name = name;
		this.image = image;
		this.preSegImage = preSegImage;
		this.metric = metric;
		this.connectivity = connectivity;
		
		Log.show = true;
		Log.println(context, Strings.IMAGE +"=: "+ ImTool.getNameOf(image) +" ("+ image.getWidth() +"x"+ image.getHeight() +")");
		Log.println(context, Strings.DIRECTORY +": "+ directory);
		Log.println(context, Strings.NAME +": "+ name);
		Log.println(context, Strings.CONNEXITY +": "+ connectivity);
	}

	@Override
	public void setPreSegImage(BufferedImage preSegImage) {

		this.preSegImage = preSegImage;
		this.setPreSegPath(ImTool.getPathOf(this.preSegImage));
	}

	@Override
	public void setPreSegPath(String preSegPath) {

		this.preSegPath = preSegPath;
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
	 * @param leafContainingPixel
	 * @param pixel
	 * @param xPixel
	 * @param yPixel
	 * @param labelPixel
	 * @param xNeighbor
	 * @param yNeighbor
	 */
	private void treat(Node leafContainingPixel, Point pixel, int xPixel, int yPixel, int labelPixel, int xNeighbor, int yNeighbor) {
	
		if(!this.isInStudiedAread(xNeighbor, yNeighbor)) {
			
			// Compute the perimeter at the border of the image while not considering diagonals
			if(!((xNeighbor == xPixel - 1) && (yNeighbor == yPixel + 1)) &&
			   !((xNeighbor == xPixel + 1) && (yNeighbor == yPixel + 1)) &&
			   !((xNeighbor == xPixel - 1) && (yNeighbor == yPixel - 1)) &&
			   !((xNeighbor == xPixel + 1) && (yNeighbor == yPixel - 1))){ 
				
				leafContainingPixel.perimeter++;
			}
			
		}else if((xNeighbor != xPixel || yNeighbor != yPixel)) {
	
			int labelNeighbor = this.labelMatrix.getLabel(xNeighbor, yNeighbor);
			Node neighbor = this.nodes[labelNeighbor];
			
			if(labelNeighbor != labelPixel) {
				
				Adjacency adjacency = leafContainingPixel.listOfNeighbors.get(neighbor);
				
				if(adjacency == null) { // Create the new adjacency
				
					adjacency = new Adjacency(neighbor, leafContainingPixel); // will be added later
					this.adjacenciesBuffer.add(adjacency);
				}
				
				// Compute the perimeter at the border of the image while not considering diagonals
				if(!((xNeighbor == xPixel - 1) && (yNeighbor == yPixel + 1)) &&
				   !((xNeighbor == xPixel + 1) && (yNeighbor == yPixel + 1)) &&
				   !((xNeighbor == xPixel - 1) && (yNeighbor == yPixel - 1)) &&
				   !((xNeighbor == xPixel + 1) && (yNeighbor == yPixel - 1))){ 
					
					leafContainingPixel.perimeter++;
					
					// Remembering the piece of frontier length between the 2 regions.
					if((xNeighbor == xPixel + 1 && yNeighbor == yPixel) || // left pixel
					   (xNeighbor == xPixel && yNeighbor == yPixel + 1)	) // lower pixel 
					{
						adjacency.frontier++;
					}
				}
			}
		}
	}
}
