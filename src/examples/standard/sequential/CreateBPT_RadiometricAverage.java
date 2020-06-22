package examples.standard.sequential;

import java.awt.image.BufferedImage;

import datastructure.Tree;
import metric.color.RadiometricAverage;
import standard.sequential.BPT;
import utils.ImTool;

/**
 * Example showing a standard BPT creation from one image using RADIOMETRIC_AVERAGE metric.
 *
 */
public class CreateBPT_RadiometricAverage {

	public static void main(String[] args) {
		
		String path = "xp//examples//six_regions_3_3.png";
		BufferedImage image = ImTool.read(path);
		
		Tree bpt = new BPT(image);
		bpt.setMetric(new RadiometricAverage(image));
		bpt.grow();
		
		if(bpt.hasEnded()) {
			
			System.out.println("[Test] BPT Creation succeded with the "+ bpt.getMetric().type +" metric!");
		}
	}
}
