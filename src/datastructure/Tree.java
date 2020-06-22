package datastructure;


import java.awt.image.BufferedImage;

import ch.systemsx.cisd.hdf5.IHDF5Reader;
import metric.bricks.Metric;
import standard.sequential.BPT;
import standard.sequential.BPT.TypeOfConnectivity;
import utils.LabelMatrix;

public interface Tree {

	/**
	 * Accepts and records a link between two neighboring regions in the Region Adjacency Graph (RAG).
	 * 
	 * <p>
	 * The RAG models the links (adjacency) between the regions (summit) and their neighbors.
	 * A distance is computed to determine how similar are the neighboring regions. 
	 *  
	 * @param adjacency
	 */
	public void add(Adjacency adjacency);

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
	public void createRAG();

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
	public void defineLeaves();

	/**
	 * Informs that a process has finished.
	 */
	public void endingState();
	
	/**
	 * 
	 * @return the neighboring connectivity.
	 */
	public TypeOfConnectivity getConnectivity();
	
	/**
	 * 
	 * @return the size of the biggest leaf.
	 */
	public int getBiggestLeafSize();

	/**
	 * 
	 * @return the directory where the tree file is (~will be) stored 
	 */
	public String getDirectory();

	/**
	 * 
	 * @return the image to represent
	 */
	public BufferedImage getImage();

	/**
	 * 
	 * @return the path of the image
	 */
	public String getImagePath();

	/**
	 * 
	 * @return the global matrix of labels
	 */
	public LabelMatrix getLabelMatrix();
	
	/**
	 * 
	 * @return the maximum between the width and the height of the image.
	 */
	public int getMaxLonger();

	/**
	 * 
	 * @return the set of leaves used to start the BPT creation process
	 */
	public Node[] getLeaves();

	/**
	 * 
	 * @return the metric used.
	 */
	public Metric getMetric();

	/**
	 * 
	 * @return the file name of the tree
	 */
	public String getName();

	/**
	 * 
	 * @return The number of actual existing adjacency links between neighbors
	 */
	public int getNbAdjacencies();

	/**
	 * 
	 * @return
	 */
	public Integer getNbInitialAdjacencies();

	/**
	 * 
	 * @return number of leaves of the tree
	 */
	public int getNbLeaves();

	/**
	 * 
	 * @return the number of regions (leaves, nodes and root) of the BPT that are currently created
	 */
	public int getNbNodes();

	/**
	 * 
	 * @param index
	 * @return the node having the defined index
	 */
	public Node getNode(int index);

	/**
	 * 
	 * @return the list of nodes.
	 */
	public Node[] getNodes();
	
	/**
	 * Gathers the points of the leaf from an HDF5 file.
	 * 
	 * @param leaf to consider
	 * @return an arrow of values corresponding to each point regrouped in the leaf
	 */
	public int[] getPixels(Node leaf);

	/**
	 * 
	 * @return the image partition containing the leaves
	 */
	public BufferedImage getPreSegImage();

	/**
	 * 
	 * @return the path leading to the image of pre-segmentation
	 */
	public String getPreSegPath();
	
	
	/**
	 * 
	 * @return the name or the state of a current building or cutting process
	 */
	public String getProcessName();

	/**
	 * 
	 * @return the progression of the construction of the tree
	 */
	public int getProgress();
	
	/**
	 * 
	 * @return the opened reader letting the access to a bpt previously stored in the hdf5 file
	 */
	public IHDF5Reader getReader();
	
	/**
	 * 
	 * @return the root
	 */
	public Node getRoot();

	/**
	 * 
	 * @return the time needed for the creation in Ms
	 */
	public Long getTimeMs();

	/**
	 * 
	 * @return the time needed for the creation
	 */
	public Long getTimeS();

	/**
	 * Creates a tree in a bottom-up fashion.
	 * 
	 * <p>
	 * <li> Defines the leaves
	 * <li> Builds the RAG
	 * <li> Merge regions (~nodes)
	 * <li> Defines the root
	 */
	public void grow();

	/**
	 * 
	 * @return true if the construction process of the tree is finished, otherwise false
	 */
	public boolean hasEnded();

	/**
	 * Determines whether a pixel defined with a coordinates x,y is contained in the image or not.
	 * 
	 * @param x index of the column
	 * @param y index of the row
	 * @return true if inside and false if outside
	 * 
	 */
	public boolean isInStudiedAread(int x, int y);

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
	public void nodeMergings();

	/**
	 * Use the matrix of labels of the image.
	 * 
	 * <p>
	 * The matrix is initially filled from the pixels (i.e. one pixel, one specific label).
	 */
	public void prepareLabelMatrix();
	
	/**
	 * Delete an adjacency from the RAG (Region Adjacency Graph).
	 * 
	 * <p>
	 * Only unused adjacencies have to be stored in the RAG. The remaining has to be definitely removed. 
	 * 
	 * @param adjacency
	 */
	public void remove(Adjacency adjacency);
	
	/**
	 * 
	 * @param dir that will contain the tree file.
	 */
	public void setDirectory(String dir);

	/**
	 * Associate the image to the tree
	 * 
	 * @param image to represent
	 */
	public void setImage(BufferedImage image);

	/**
	 * Helps the user to define his/her prior knowledge to consider. 
	 * 
	 * @param metric used during the tree construction.
	 */
	public void setMetric(Metric metric);

	/**
	 * 
	 * @param name of the tree file.
	 */
	public void setName(String name);

	/**
	 * Preparation of the tree, not specifying the metric, before growing it up. 
	 * 
	 * @param name of the tree; should not be null
	 * @param image to represent; should not be null
	 * @param preSegImage defining the leaves; should not be null
	 * @param connectivity of the neighbors; should not be null
	 * 
 	 * @throws NullPointerException if name or image or preSegImage or connectivity is null
 	 * 
	 * @see Tree#setParams(String, BufferedImage, BufferedImage, Metric, TypeOfConnectivity) setting the params while specifying the metric to use
	 */
	public void setParams(String name, BufferedImage image, BufferedImage preSegImage, TypeOfConnectivity connectivity);

	/**
	 * Prepares the tree before growing it up. 
	 * 
	 * @param name of the tree; should not be null
	 * @param image to represent; should not be null
	 * @param preSegImage defining the leaves; should not be null
	 * @param metric defined by the user in order to compute the similarity distance between two regions; should not be null
	 * @param connectivity of the neighbors; should not be null
	 * 
	 * @throws NullPointerException if name or image or preSegImage or metric or connectivity is null
	 * 
	 * @see Tree#setParams(String, BufferedImage, BufferedImage, TypeOfConnectivity) setting the params wile NOT specifying the metric to use
	 */
	public void setParams(String name, BufferedImage image, BufferedImage preSegImage, Metric metric, TypeOfConnectivity connectivity);

	/**
	 * Affect the initial pre-segmented image
	 * public 
	 * @param preSeg image containing the leaves
	 */
	public void setPreSegImage(BufferedImage preSeg);

	/**
	 * Affect the path of the pre-segmented image
	 * 
	 * @param preSegPath leading to the pre-segmented image
	 */
	public void setPreSegPath(String preSegPath);

	/**
	 * Affect the progression
	 * 
	 * @param p value of the tree creation progression
	 */
	public void setProgress(int p);

	/**
	 * Informs that a process has started on the tree.
	 */
	public void startingState();

}
