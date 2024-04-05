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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Writer;
import datastructure.Node;
import datastructure.Tree;
import lang.Strings;
import metric.bricks.Metric;
import utils.d2.Formula;

/**
 * A BPT can be saved in the disk.
 * 
 */
public class SaveBPT {
	
	public static final String CONTEXT = "BPT_SAVING";
	
	/**
	 * <p>
	 * Two files are created and stored:
	 * <li> 1- a .csv containing informations about the BPT creation:
	 * <ul>
	 * <li> Name of the tree
	 * <li> Name of the image
	 * <li> Size of the image
	 * <li> Number of bands of the image
	 * <li> Directory where the BPT is stored
	 * <li> Regions similarity used during the BPT creation
	 * <li> Region neighbors connectivity
	 * <li> Number of the initial adjacencies
	 * <li> Number of nodes created
	 * <li> Number of leaves
	 * <li> Time of creation in ms and in s
	 * </ul>
	 * <li> 2- a .dot containing DOT (graph description language)
	 *  
	 * @param tree to save; should not be null
	 * @return true if the files are saved successfully; else false
	 * 
	 * @throws NullPointerException if bpt is null
	 */
	public static boolean toDOT(Tree tree) {
		
		boolean success1 = false;
		boolean success2 = false;
		
		/* prepare the first file containing informations */
		try {

			String syllabus[] = tree.getName().split("//.");
			String extension = syllabus[syllabus.length-1];
			String csvFileName = tree.getName().split("//."+extension)[0] +".csv";
			PrintWriter writer = new PrintWriter(tree.getDirectory() +"//"+ csvFileName, "UTF-8");
			
			String source = "pixels";
			BufferedImage preSeg = tree.getPreSegImage();
			if(preSeg != null) {
				source = tree.getName();
			}
			
			BufferedImage img = tree.getImage();
			
			String separator = ";";
			
			Metric metric = tree.getMetric();
			StringBuilder metricInfo = new StringBuilder(metric.type +"");
			for(double param: metric.params) {
				
				metricInfo.append(separator + param);
			}
			
			writer.println(Strings.NAME + separator + tree.getName());
			
			if(img != null) {
			
				writer.println(Strings.IMAGE + separator + ImTool.getNameOf(img));
				writer.println(Strings.VAR_IMAGE_PATH + separator + ImTool.getPathOf(img));
				writer.println(Strings.IMAGE_SIZE + separator + img.getWidth() +"x"+ img.getHeight());
				writer.println(Strings.VAR_PRESEG_PATH + separator + tree.getPreSegPath());			
				writer.println(Strings.NB_BANDS + separator + ImTool.getNbBandsOf(img) +"");
			}
			
			writer.println(Strings.DIRECTORY + separator + tree.getDirectory());
			writer.println(Strings.METRIC + separator + metricInfo);
			writer.println(Strings.CONNEXITY + separator + tree.getConnectivity());
			writer.println(Strings.NB_INITIAL_ADJACENCIES + separator + tree.getNbNodes() + separator + Strings.INITIALLY_GENERATED);
			writer.println(Strings.NB_NODES + separator + tree.getNbInitialAdjacencies() + separator + Strings.INCLUDING_LEAVES);
			writer.println(Strings.NB_LEAVES + separator + tree.getNbLeaves() + separator + Strings.REGIONS +" "+ Strings.FROM + separator + source);
			writer.println(Strings.TIME_OF_CREATION + separator + tree.getTimeMs() + separator + "ms" + separator + tree.getTimeS() + separator + "s");
	
			/* close the writer */
			writer.close();
			
			success1 = true;

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		/* prepare the second file containing the DOT structure */
		try {
			
			tree.startingState();
			tree.setProgress(0);
			PrintWriter writer = new PrintWriter(tree.getDirectory() +"//"+ tree.getName(), "UTF-8");
			
			writer.print("digraph bpt{");
			
			Node[] nodes =  tree.getNodes();
			for(int n = 0; n < nodes.length; n++) {
				
				tree.setProgress((100 * n) / nodes.length);

				Node node = nodes[n];
				
				if(node == null) {
					break;
				}
				
				Node left = node.leftNode;
				Node right = node.rightNode;

				if(node.leftNode != null) {
					
					writer.print(node.name +" -> "+ left.name +";");
					writer.print(node.name +" -> "+ right.name +";");
					
				}
			}
			
			writer.println("}");
			
			/* close the writer */
			writer.close();
			
			tree.endingState();
			success2 = true;
	
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
				
		return success1 && success2;
	}
	
	/**
	 * <p>
	 * Two files are created and stored:
	 * <li> 1- a .csv containing informations about the BPT creation:
	 * <ul>
	 * <li> Name of the tree
	 * <li> Name of the image
	 * <li> Size of the image
	 * <li> Number of bands of the image
	 * <li> Directory where the BPT is stored
	 * <li> Regions similarity used during the BPT creation
	 * <li> Region neighbors connectivity
	 * <li> Number of the initial adjacencies
	 * <li> Number of nodes created
	 * <li> Number of leaves
	 * <li> Time of creation in ms and in s
	 * </ul>
	 * <li> 2- a .xml containing GraphML structure
	 *  
	 * @param tree to save; should not be null
	 * @return true if the files are saved successfully; else false
	 * 
	 * @throws NullPointerException if bpt is null
	 */
	public static boolean toGRAPHML(Tree tree) {
		
		boolean success1 = false;
		boolean success2 = false;
		
		/* prepare the first file containing informations */
		try {

			String syllabus[] = tree.getName().split("//.");
			String extension = syllabus[syllabus.length-1];
			String csvFileName = tree.getName().split("//."+extension)[0] +".csv";
			PrintWriter writer = new PrintWriter(tree.getDirectory() +"//"+ csvFileName, "UTF-8");
			
			String source = "pixels";
			BufferedImage preSeg = tree.getPreSegImage();
			if(preSeg != null) {
				source = tree.getName();
			}
			
			BufferedImage img = tree.getImage();
			
			String separator = ";";

			Metric metric = tree.getMetric();
			StringBuilder metricInfo = new StringBuilder(metric.type +"");
			for(double param: metric.params) {
				
				metricInfo.append(separator + param);
			}
			
			writer.println(Strings.NAME + separator + tree.getName());
			writer.println(Strings.IMAGE + separator + ImTool.getNameOf(img));
			writer.println(Strings.VAR_IMAGE_PATH + separator + ImTool.getPathOf(img));
			writer.println(Strings.IMAGE_SIZE + separator + img.getWidth() +"x"+ img.getHeight());
			writer.println(Strings.VAR_PRESEG_PATH + separator + tree.getPreSegPath());			
			writer.println(Strings.NB_BANDS + separator + ImTool.getNbBandsOf(img) +"");
			writer.println(Strings.DIRECTORY + separator + tree.getDirectory());
			writer.println(Strings.METRIC + separator + metricInfo);
			writer.println(Strings.CONNEXITY + separator + tree.getConnectivity());
			writer.println(Strings.NB_INITIAL_ADJACENCIES + separator + tree.getNbNodes() + separator + Strings.INITIALLY_GENERATED);
			writer.println(Strings.NB_NODES + separator + tree.getNbInitialAdjacencies() + separator + Strings.INCLUDING_LEAVES);
			writer.println(Strings.NB_LEAVES + separator + tree.getNbLeaves() + separator + Strings.REGIONS +" "+ Strings.FROM + separator + source);
			writer.println(Strings.TIME_OF_CREATION + separator + tree.getTimeMs() + separator + "ms" + separator + tree.getTimeS() + separator + "s");
	
			/* close the writer */
			writer.close();
			
			success1 = true;

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		/* prepare the second file containing the GraphML structure */
		try {
			
			tree.startingState();
			tree.setProgress(0);
			try (PrintWriter writer = new PrintWriter(tree.getDirectory() +"//"+ tree.getName() +".xml", "UTF-8")) {
				writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>/n" + 
							 "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"/n" + 
							 "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance/n" + 
							 "    xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/n" + 
							 "     http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">/n" + 
							 "<graph id=\"G\" edgedefault=\"undirected\">");
				
				Node[] nodes =  tree.getNodes();

				/* Store the leaves */
				int lindex = 0;
				for(lindex = 0; lindex < tree.getNbLeaves(); ++lindex) {
					
					writer.println("	<node id=/"+ lindex +"//>");
				}
				
				
				/* Store the nodes and the edges */
				for(int n = lindex; n < nodes.length; n++) {
					
					tree.setProgress((100 * n) / nodes.length);

					Node node = nodes[n];
					
					if(node == null) {
						break;
					}
					
					Node left = node.leftNode;
					Node right = node.rightNode;

					if(node.leftNode != null) {
						
						writer.println("	<node id=\""+ n +"\"/>");
						writer.println("	<edge source=\""+ node.name +"\" target=\""+ left.name +"\"/>");
						writer.println("	<edge source=\""+ node.name +"\" target=\""+ right.name +"\"/>");
					}
				}
				
				writer.println("  </graph>/n" + 
							   "</graphml>");
				
				/* close the writer */
				writer.close();
			}
			
			tree.endingState();
			success2 = true;
	
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
				
		return success1 && success2;
	}
	
	/**
	 * <p>
	 * Creates a HDF5 file containing: <br>
	 * The only method that can, for now, save the perimeter of each node.
	 * <ul>
	 * <li> info
	 * <li> structure
	 * </ul>
	 *  
	 * @param tree to save; should not be null
	 * @return true if the files are saved successfully; else false
	 * 
	 * @throws NullPointerException if bpt is null
	 */
	public static boolean toHDF5(Tree tree){
		
		Log.println(CONTEXT, "...");
		
		boolean success = false;
		
		long startingTime = System.nanoTime();
		
		try {
			
			/* prepare and write the HDF5 file */
			String source = "pixels";
			BufferedImage preSeg = tree.getPreSegImage();
			if(preSeg != null) {
				
				source = tree.getName();
			}

			BufferedImage img = tree.getImage();
			
			Metric metric = tree.getMetric();
			StringBuilder metricInfo = new StringBuilder(metric.type +"");
			for(double param: metric.params) {
				
				metricInfo.append(";"+ param);
			}

			/* Major information */
			IHDF5Writer writer = HDF5Factory.open(tree.getDirectory() +"//"+ tree.getName() +".h5");
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_NAME, tree.getName());
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_IMAGE_NAME, ImTool.getNameOf(img));
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_IMAGE_PATH, ImTool.getPathOf(img));
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_IMAGE_SIZE, img.getWidth() +"x"+ img.getHeight());
			try{writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_PRESEG_PATH, tree.getPreSegPath());}catch(Exception e) { /* no preseg */};			
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_NB_BANDS, ImTool.getNbBandsOf(img) +"");
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_DIRECTORY, tree.getDirectory());
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_METRIC, metricInfo.toString());
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_SOURCE, source);
			writer.writeString(Strings.VAR_INFO +"/"+ Strings.VAR_CONNEXITY, tree.getConnectivity() +"");
			writer.writeInt(Strings.VAR_INFO +"/"+ Strings.VAR_NB_INITIAL_ADJACENCIES, tree.getNbInitialAdjacencies());
			writer.writeInt(Strings.VAR_INFO +"/"+ Strings.VAR_NB_NODES, tree.getNbNodes());
			writer.writeInt(Strings.VAR_INFO +"/"+ Strings.VAR_NB_LEAVES, tree.getNbLeaves());
			writer.writeInt(Strings.VAR_INFO +"/"+ Strings.VAR_MAX_LONGER, tree.getMaxLonger());
			writer.writeInt(Strings.VAR_INFO +"/"+ Strings.VAR_BIGGEST_LEAF_SIZE, tree.getBiggestLeafSize());
			writer.writeLong(Strings.VAR_INFO +"/"+ Strings.VAR_TIME_OF_CREATION_MS, tree.getTimeMs());
			writer.writeLong(Strings.VAR_INFO +"/"+ Strings.VAR_TIME_OF_CREATION_S, tree.getTimeS());
			
			
			// TODO set progression of the task.
			tree.startingState();
			tree.setProgress(0);
			
			Node[] nodes =  tree.getNodes();

			int nbLeaves = tree.getNbLeaves();
			
			/* Saving the leaves of the tree */
			int nbCols = tree.getBiggestLeafSize() + 1;
			writer.int32().createMatrix(Strings.VAR_STRUCTURE +"/"+ Strings.VAR_LEAVES,
										nbLeaves, nbCols);
			
			int posVal = 0;
			
			for(int i = 0; i < nbLeaves; ++i) {
				
				Node l = nodes[i];
				int lSize = l.getSize();
				int nbCol = lSize + 1;
				int idCol = 0;
				int[][] leafData = new int[1][nbCol];
				leafData[0][idCol++] = lSize; // first column that represents the number of pixels contained in the leaf.
				for(Point p: l.getPixels()) {
					
					posVal = Formula.toVal(p.x, p.y, tree.getMaxLonger());
					leafData[0][idCol++] = posVal;
				}
				writer.int32().writeMatrixBlock(Strings.VAR_STRUCTURE +"/"+ Strings.VAR_LEAVES,
												leafData, l.name, 0);
			}
			
			/* Saving the nodes of the tree */
			int nbOnlyNodes = nodes.length - nbLeaves;
			writer.int32().createMatrix(Strings.VAR_STRUCTURE +"/"+
										Strings.VAR_NODES,
										nbOnlyNodes, 3);
			int verticalIndex = 0;
			for(int n = nbLeaves; n < nodes.length; n++) {

				tree.setProgress((100 * n) / nodes.length);

				Node node = nodes[n];
				
				if(node == null) {
					break;
				}
				
				Node left = node.leftNode;
				Node right = node.rightNode;

				if(left != null) {
					
					int[][] nodeData = new int[1][4];
					nodeData[0][0] = n;
					nodeData[0][1] = left.name;
					nodeData[0][2] = right.name;
					nodeData[0][3] = node.perimeter;
					writer.int32().writeMatrixBlock(Strings.VAR_STRUCTURE +"/"+
													Strings.VAR_NODES, nodeData,
													verticalIndex++, 0);
				}
			}

			/* close the writer */
			writer.close();

			success = true;
			
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		long endingTime = System.nanoTime();
		long savingTimeMs = (endingTime - startingTime)/1000000;
		long savingTimeS = savingTimeMs / 1000;
		
		Log.println(CONTEXT, savingTimeMs +" ms ("+ savingTimeS +" s)");
				
		return success;
	}
}
