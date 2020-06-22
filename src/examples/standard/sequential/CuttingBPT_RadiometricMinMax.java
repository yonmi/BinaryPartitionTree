package examples.standard.sequential;

import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import datastructure.CutResult;
import datastructure.Tree;
import standard.sequential.BPT;
import ui.ImFrame;
import utils.CutBPT;
import utils.ImTool;

/**
 * Example of a simple BPT cutting.
 *
 */
public class CuttingBPT_RadiometricMinMax {

	public static void main(String[] args) {

		String path = "xp//examples//six_regions_3_3.png";
		BufferedImage image = ImTool.read(path);
		
		Tree bpt = new BPT(image);
		bpt.grow();
		
		if(bpt.hasEnded()) {
			
			System.out.println("[Test] BPT Creation succeded!");
		}
		
		int starting = 6;
		int ending = 0;
		int step = 1;
		CutResult cutResult = CutBPT.execute(bpt, starting, ending, step);
		System.out.println("[Test] BPT Cutting finished!");
		
		for(Entry<Integer, BufferedImage> entry: cutResult.regionImages.entrySet()) {
			
			int numberOfRegions = entry.getKey();
			BufferedImage partition = entry.getValue();
			
			ImTool.show(partition, ImFrame.IMAGE_DEFAULT_SIZE, numberOfRegions +" regions");			
		}
	}
}
