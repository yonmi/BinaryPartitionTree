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
