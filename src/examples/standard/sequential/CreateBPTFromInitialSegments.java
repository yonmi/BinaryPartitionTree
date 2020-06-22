package examples.standard.sequential;

import java.awt.image.BufferedImage;

import datastructure.Tree;
import metric.color.RadiometricAverage;
import standard.sequential.BPT;
import utils.ImTool;

/**
 * Example showing a standard BPT creation from one image using RADIOMETRIC_AVERAGE metric.
 * The starting leaves are defined from an initial set of segments extracted from a label map.
 *
 */
public class CreateBPTFromInitialSegments {

	public static void main(String[] args) {
		
		//String path = "xp//examples//six_regions_3_3.png";
		String path = "xp//examples//border_test.png";
		BufferedImage image = ImTool.read(path);
		
		String presegPath = "xp//examples//border_test.png";
		BufferedImage presegImage = ImTool.read(presegPath);
		ImTool.show(presegImage, 30);
		
		Tree bpt = new BPT(image);
		bpt.setPreSegImage(presegImage);
		bpt.setMetric(new RadiometricAverage(image));
		bpt.grow();
		
		if(bpt.hasEnded()) {
			
			System.out.println("[Test] BPT Creation succeded with the "+ bpt.getMetric().type +" metric!");
		}
	}
}
