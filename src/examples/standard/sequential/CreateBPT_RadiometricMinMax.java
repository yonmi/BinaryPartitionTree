package examples.standard.sequential;

import java.awt.image.BufferedImage;

import datastructure.Tree;
import standard.sequential.BPT;
import utils.ImTool;
import utils.TreeVisu;

/**
 * Example showing a standard BPT creation from one image.
 *
 */
public class CreateBPT_RadiometricMinMax {

	public static void main(String[] args) {
		
		String path = "xp//examples//six_regions_3_3.png";
		BufferedImage image = ImTool.read(path);
		
		Tree bpt = new BPT(image);
		bpt.grow();
		
		if(bpt.hasEnded()) {
			
			TreeVisu.display(bpt.getRoot(), null);
			System.out.println("[Test] BPT Creation succeded!");
		}
	}
}
