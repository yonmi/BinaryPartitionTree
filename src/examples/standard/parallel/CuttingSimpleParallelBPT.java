package examples.standard.parallel;

import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import datastructure.CutResult;
import datastructure.Tree;
import standard.parallel.BPTP;
import ui.ImFrame;
import utils.CutBPT;
import utils.ImTool;

/**
 * Example of a simple cutting on a standard BPT built in a parallel fashion.
 *
 */
public class CuttingSimpleParallelBPT {

	public static void main(String[] args) {

		String path = "xp//examples//six_regions_3_3.png";
		BufferedImage image = ImTool.read(path);
		
		Tree bptp = new BPTP(image);
		bptp.grow();
		
		if(bptp.hasEnded()) {
			
			System.out.println("[Test] BPT Parallel Creation succeded!");
		}
		
		int starting = 6;
		int ending = 0;
		int step = 1;
		CutResult cutResult = CutBPT.execute(bptp, starting, ending, step);
		System.out.println("[Test] BPT Cutting finished!");
		
		for(Entry<Integer, BufferedImage> entry: cutResult.regionImages.entrySet()) {
			
			int numberOfRegions = entry.getKey();
			BufferedImage partition = entry.getValue();
			
			ImTool.show(partition, ImFrame.IMAGE_DEFAULT_SIZE, numberOfRegions +" regions");			
		}
	}
}
