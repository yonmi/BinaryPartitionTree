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

package examples.multi.sequential;

import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import datastructure.CutResult;
import datastructure.Tree;
import metric.bricks.Metric.TypeOfMetric;
import multi.sequential.MBPT;
import multi.strategy.consensus.bricks.Consensus.ConsensusStrategy;
import ui.ImFrame;
import utils.CutBPT;
import utils.ImTool;
import utils.Log;

/**
 * Example creating a multi-featured binary partition tree using two metrics: 
 * 
 *  <li> RADIOMETRIC_MIN_MAX
 *  <li> RADIOMETRIC_AVERAGE
 *  
 * The main steps of the MBPT creation are:
 * 
 * 	<li> Preparing the image(s)
 *  <li> Creating an empty tree
 *  <li> Register the image(s)
 *  <li> Associating the metric(s) to the image(s)
 *  <li> Choosing the consensus strategy to use
 *  <li> Growing the tree
 *
 */
public class CreateAndCutMBPT {

	public static void main(String[] args) {

		Log.show = true;
		
//		String path = "xp//examples//six_regions_9_9.png";
//		String path = "xp//stbg400//pleiade_stbg.tif";
		String path = "xp//dataset//42049.png";
		
		BufferedImage image = ImTool.read(path);
		
		/* Create an empty tree */
		Tree tree = new MBPT();
		
		/* Register the image(s) */
		((MBPT) tree).registerImage(image);

		/* Choosing the consensus strategy to use */
		int consensusRange = 5; /* percentage defining the interval of the list to consider */
		int progressive = 1; /* the interval is defined proportionally to remaining number of adjacency links */
		((MBPT) tree).setConsensusStrategy(ConsensusStrategy.SCORE_OF_RANK, consensusRange, progressive);
		
		/* Linking metrics to the image */
		((MBPT) tree).linkMetricToAnImage(image, TypeOfMetric.RADIOMETRIC_MIN_MAX);
		((MBPT) tree).linkMetricToAnImage(image, TypeOfMetric.RADIOMETRIC_AVERAGE);
		
		tree.grow();
		
		if(tree.hasEnded()) {
			
			System.out.println("[Test] MBPT Creation succeded!");
		}
		
		/* Cutting */
		int starting = 25;
		int ending = 0;
		int step = 5;
		CutResult cutResult = CutBPT.execute(tree, starting, ending, step);
		System.out.println("[Test] MBPT Cutting finished!");
		
		for(Entry<Integer, BufferedImage> entry: cutResult.regionImages.entrySet()) {
			
			int numberOfRegions = entry.getKey();
			BufferedImage partition = entry.getValue();
			
			ImTool.show(partition, ImFrame.IMAGE_DEFAULT_SIZE, numberOfRegions +" regions");			
		}
	}
}
