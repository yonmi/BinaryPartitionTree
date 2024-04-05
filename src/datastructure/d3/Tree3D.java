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

package datastructure.d3;

import ch.systemsx.cisd.hdf5.IHDF5Reader;
import datastructure.Adjacency;
import datastructure.Node;
import metric.bricks.d3.Metric3D;
import utils.d3.LabelMatrix3D;
import utils.d3.RGBStruct;

public interface Tree3D {
	
	/**
	 * To determine the neighbors of a region, the type of connectivity has to be known.
	 * 
	 * <p>
	 * <li> CN6 or 6-CN considers all faces neighbors that a middle voxel can have.
	 * <li> CN14 or 14-CN considers only 14 neighbors corresponding to faces and egdes neighbors 
	 * <li> CN122 or 22-CN all 22 neighbors surrounding the voxel. </br></br>
	 *
	 */
	public enum TypeOfConnectivity{
		
		CN6,
		CN14,
		CN22,
		ALL; /* ALL means each point will be linked to the others */
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
	 * @return the cube to represent
	 */
	public RGBStruct getCube();

	/**
	 * 
	 * @return the global matrix of labels
	 */
	public LabelMatrix3D getLabelMatrix3D();

	/**
	 * 
	 * @return the set of leaves used to start the BPT creation process
	 */
	public Node[] getLeaves();

	/**
	 * 
	 * @return the metric used.
	 */
	public Metric3D getMetric3D();

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
	public boolean isInStudiedAread(int x, int y, int z);

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
	public void prepareLabelMatrix3D();
	
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
	 * Associate the image to the tree
	 * 
	 * @param image to represent
	 */
	public void setCube(RGBStruct cube);

	/**
	 * Helps the user to define his/her prior knowledge to consider. 
	 * 
	 * @param metric used during the tree construction.
	 */
	public void setMetric3D(Metric3D metric3D);

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
	 * @see Tree3D#setParams(String, BufferedImage, BufferedImage, Metric, TypeOfConnectivity) setting the params while specifying the metric to use
	 */
	public void setParams(String name, RGBStruct cube, TypeOfConnectivity connectivity);

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
	 * @see Tree3D#setParams(String, BufferedImage, BufferedImage, TypeOfConnectivity) setting the params wile NOT specifying the metric to use
	 */
	public void setParams(String name, RGBStruct cube, Metric3D metric3D, TypeOfConnectivity connectivity);

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
