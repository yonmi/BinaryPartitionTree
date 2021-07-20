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

package datastructure;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import standard.parallel.BPTP.Side;


/**
 * A node is a region regrouping a set of pixels of the images.
 * 
 * <p>
 * The pixels are spatially connected thus have relations with their neighbors.
 * A node can be a son or a father of other nodes.
 * It can be considered as a summit in various graphs such as the Binary Partition Tree and the Regions Adjacency Graph.
 * A node can be also a leaf when it represents the smallest piece of objects contained in a BPT.
 * It can be also considered as a root representing the support of the image.
 *
 */
public class Node implements Serializable, Comparable<Node>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * List of pixels contained in the edge border of the region.
	 */
	public Set<Integer> borderPoints;
	
	/**
	 * Box surrounding the region.
	 * <li> minX 
	 * <li> maxX 
	 * <li> minY 
	 * <li> maxY
	 */
	public int [] boundingBox = new int[4]; 

	/**
	 * Higher hierarchy
	 */
	public Node father;
	
	/**
	 * Used to determine the similarities between two regions.
	 * 
	 * <p>
	 * The Integer part corresponds to the index localization of the features among all the others.<br/>
	 * The Double part corresponds to the value associated to each feature.
	 */
	public HashMap<Integer, Double> features = new HashMap<Integer, Double>();
	
	/**
	 * States if the node is currently active or not.
	 */
	public boolean isActive = true;

	/**
	 * When a tree is build in a parallel fashion from various crops of the image, the neighboring links at the edge have to consider the neighbors on the other crops.
	 * 
	 * <p>
	 * The neighbors on the other side are considered as foreigns.  
	 */
	public boolean isForeign = false;

	/**
	 * <b> For parallel fashion purpose: </b>
	 * when a tree is build in a parallel fashion from various crops of the image, the neighboring links at the edge have to consider the neighbors on the other crops.
	 * 
	 * <p>
	 * The nodes on the edge of the image and having foreign neighbors are considered as frontiers.
	 */
	public boolean isFrontier = false;

	/**
	 * Label associated to the region.
	 * All pixels of the region are associated to this label.
	 * A father has the same label as the child having the most number of pixels.
	 */
	public int label = -1;

	/**
	 * <b> For parallel fashion purpose: </b>
	 * lists the neighbors of the other sides. 
	 */
	public ConcurrentHashMap<Node, Adjacency> listOfForeignNeighbors;

	/**
	 * All current neighbors and the corresponding adjacency links.
	 */
	public ConcurrentHashMap<Node, Adjacency> listOfNeighbors;

	/**
	 * All pixels contained in the region.
	 */
	public ArrayList<Point> listOfPixels = new ArrayList<Point>();

	/**
	 * Used for synchronization lock during parallel tasks.
	 */
	public boolean locked = false;

	/**
	 * Lower hierarchy: first son
	 */
	public Node leftNode;
	
	/**
	 * Inverse of depth (i.e. 0 for leaves)
	 */
	public int lvl = 0;
	
	/**
	 * Distance between the children.
	 */
	public double merginScore;

	/**
	 * Identification of the region
	 */
	public int name;

	/**
	 * Number of the smallest points contained in the region.
	 */
	public int nbPixels = 0;

	/**
	 * Regrouping neighbors that require updates after a node merging process.
	 */
	public ConcurrentHashMap<Node, Adjacency> neighborToTreat;

	/**
	 * In the case where the index of the node would change, the old one is stored. 
	 */
	public int oldName = -1;

	/**
	 * Length of the border of the corresponding region.
	 */
	public int perimeter;

	/**
	 * Lower hierarchy: second son.
	 */
	public Node rightNode;
	
	/**
	 * <b> For parallel fashion purpose: </b> 
	 * Side of the cropped image on which the construction is done. 
	 */
	public Side side = Side.PART1;

	/**
	 * Type of the node:
	 * 
	 * <li> LEAF
	 * <li> NODE
	 * <li> ROOT
	 */
	public TypeOfNode type;

	/**
	 * 
	 * Each node is defined by a precise type that compose a tree.
	 *
	 */
	public static enum TypeOfNode{
		
		LEAF,
		NODE,
		ROOT
	}
	
	
	/* For Pont-Tuset evaluation */
	public Integer[] vector;
	public boolean optRacine;
	
	/* For Extrinsic evaluation */
	public boolean s = false;
	public int[][] d = new int[2][3];
	
	
	/**
	 * Creates an empty node.
	 * 
	 * @see Node#Node(int) create a node (~leaf) while specifying its index
	 * @see Node#Node(Node, Node) create a node from two children
	 * @see Node#Node(int, Node, Node) create a node from two children while specifying its index
	 */
	public Node() { }
	
	/**
	 * Creates a node (~leaf) while specifying its index.
	 * 
	 * @param name index identifying the node
	 * 
 	 * @see Node#Node() create an empty node
	 * @see Node#Node(Node, Node) create a node from two children
	 * @see Node#Node(int, Node, Node) create a node from two children while specifying its index
	 */
	public Node(int name) { // leaf

		this.name = name;
		this.listOfPixels = new ArrayList<Point>();
		this.listOfNeighbors = new ConcurrentHashMap<Node, Adjacency>();
		this.neighborToTreat = new ConcurrentHashMap<Node, Adjacency>();
		this.type = TypeOfNode.LEAF;
	}

	/**
	 * Creates a node from two children.
	 * 
	 * @param leftNode first son; should not be null
	 * @param rightNode second son; should not be null
	 * 
	 * @throws NullPointerException if leftNode is null or rightNode is null
	 * 
 	 * @see Node#Node() create an empty node
	 * @see Node#Node(int) create a node (~leaf) while specifying its index
	 * @see Node#Node(int, Node, Node) create a node from two children while specifying its index
	 */
	public Node(Node leftNode, Node rightNode) {
	
		this.listOfNeighbors = new ConcurrentHashMap<Node, Adjacency>(leftNode.listOfNeighbors);
		this.removeNeighbor(rightNode);
		this.listOfNeighbors.putAll(rightNode.listOfNeighbors);
		this.removeNeighbor(leftNode);
		
		this.neighborToTreat = new ConcurrentHashMap<Node, Adjacency>();
	
		this.leftNode = leftNode;
		this.rightNode = rightNode;
		
		this.leftNode.father = this;
		this.rightNode.father = this;
		
		this.listOfPixels.addAll(this.leftNode.getPixels());
		this.listOfPixels.addAll(this.rightNode.getPixels());
		this.nbPixels = this.leftNode.nbPixels + this.rightNode.nbPixels;
		this.updateLabel();
		
		Adjacency adjacency = leftNode.listOfNeighbors.get(rightNode);
		
		try {
			
			this.perimeter = (leftNode.perimeter + rightNode.perimeter) - 2*(adjacency.frontier);
			
		}catch(Exception e) {
			
			// no problem, the tree is certainly loaded from a .h5 file.
		}
				
		this.type = TypeOfNode.NODE;
	}

	/**
	 * Creates a node from two children while specifying its index
	 * 
	 * @param name index identifying the node
	 * @param leftNode first son; should not be null
	 * @param rightNode second son; should not be null
	 * 
 	 * @see Node#Node() create an empty node
	 * @see Node#Node(int) create a node (~leaf) while specifying its index
	 * @see Node#Node(Node, Node) create a node from two children
	 */
	public Node(int name, Node leftNode, Node rightNode) {

		this(leftNode, rightNode);
		this.name = name;
	}

	/**
	 * Saves a neighbor and the corresponding adjacency link.
	 * 
	 * @param node spatial neighbor; should not be null
	 * @param adjacency link associated to a similarity distance between the node ant his neighbor; should not be null
	 * 
	 * @throws NullPointerException if node is null or adjacency is null
	 */
	public void addNeighbor(Node node, Adjacency adjacency) {
	
		if(!this.listOfNeighbors.containsValue(adjacency)) {

			this.listOfNeighbors.put(node, adjacency);
		}
	}

	/**
	 * Saves a pixel contained in the node (~region).
	 * 
	 * @param x index of the column; should be in [0, imageWidth]
	 * @param y index of the row; should be in [0, imageHeight]
	 */
	public void addPixel(int x, int y) {
		
		this.listOfPixels.add(new Point(x,y));
		this.nbPixels++;
	}

	@Override
	public int compareTo(Node node) {
	
		int nodeIndex = node.name;
		if(this.name == nodeIndex) {
			
			return 0;
			
		}else if(this.name < nodeIndex) {
			
			return -1;
			
		}else return 1;
	}
	
	/**
	 * Computation based on the pixels considered as borders. 
	 * 
	 * @return the number of pixels at the border of the region.
	 */
	public int computePerimeter() {
	
		return 0; 
	}

	/**
	 * Converts each coordinates of each points to an integer value and stores it into a set.
	 * @return a set containing all locations of all points as integer values 
	 */
	public TreeSet<Integer> getIntegerPoints() {
		
		TreeSet<Integer> result = new TreeSet<Integer>();
		int width = 100; // warning
		
		for(Point p: this.listOfPixels) {
			
			int val = p.x + (p.y * width);
			result.add(val);
		}
		
		return result;
	}

	/**
	 * The pixels are stored by the leaves.
	 * The pixels forming a region (~node) are defined from the set of leaves forming it.
	 * 
	 * @return a list of pixels contained in the node (~region)
	 */
	public ArrayList<Point> getPixels() {
		
		return this.listOfPixels;
	}

	/**
	 * 
	 * @return The number of pixels in the region (~node)
	 */
	public int getSize() { 
		
		//if(this.nbPixels < 1) {
			
			return this.getPixels().size();
			
		/*}else {
			
			return this.nbPixels;
		}*/
	}
	
	/**
	 * 
	 * @param pixel to consider.
	 * @return true if the pixel is considered as a border of the region, otherwise false.
	 */
	public boolean isBorder(Point pixel) {
		
		return false; 
	}

	/**
	 * 
	 * @param distance of similarity used while creating the node object by merging the children
	 */
	public void rememberMerginScore(double distance) { 
		
		this.merginScore = distance; 
	}

	/**
	 * If the index of the node has to be changed, the old one could be saved.
	 */
	public void rememberPreviousName() { 
		
		this.oldName = this.name; 
	}

	/**
	 * Forget a link between the node and a neighbor
	 * 
	 * @param neighbor to break with; should not be null
	 * 
	 * @throws NullPointerException if neighbor is null
	 */
	public void removeNeighbor(Node neighbor) {
	
		this.listOfNeighbors.remove(neighbor);
	}

	/**
	 * <b> For parallel fashion purpose: </b>
	 * defines if the node is on the border of the cropped image.
	 *  
	 * @param isFrontier or not
	 */
	public void setFrontier(boolean isFrontier) {
	
		this.isFrontier = isFrontier;
	}

	/**
	 * Gives a new identification to the node and remembers his past.
	 * 
	 * @param name new index of the node
	 */
	public void setName(int name) {

		this.rememberPreviousName();
		this.name = name;
	}

	/**
	 * <b> For parallel fashion purpose: </b>
	 * defines the side belonging the node.
	 * 
	 * @param side where the construction occurs
	 */
	public void setSide(Side side) {
	
		this.side = side;
	}

	/**
	 * Removes all pixels of the node2 from the list of pixels of the node1.
	 * 
	 * @param node1 to resize; should not be null
	 * @param node2 having the pixels to remove from node1; should not be null
	 * 
	 * @throws NullPointerException is node1 is null or node2 is null
	 */
	public static void substract(Node node1, Node node2) {
	
		ArrayList<Point> pixels1 = node1.getPixels();
		ArrayList<Point> pixels2 = node2.getPixels();
		pixels1.removeAll(pixels2);
	}

	/**
	 * The label is associated to a regions.
	 * The label of the father is the same as the child having the most number of pixels.
	 */
	public void updateLabel() {

		if(this.leftNode.nbPixels >= this.rightNode.nbPixels) {

			this.label = this.leftNode.label;

		}else {

			this.label = this.rightNode.label;
		}
	}
	
	/**
	 * Update the node level and the level of its descendant.
	 * @param newLvl 
	 */
	public void updateLvl(int newLvl) {
		
		this.lvl = newLvl;
		
		if(this.leftNode != null)
			this.leftNode.updateLvl(newLvl - 1);
			
		if(this.rightNode != null)
			this.rightNode.updateLvl(newLvl -1 );	
		
	}
}