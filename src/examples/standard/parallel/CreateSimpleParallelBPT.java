package examples.standard.parallel;

import java.awt.image.BufferedImage;

import datastructure.Tree;
import standard.parallel.BPTP;
import utils.ImTool;
import utils.Log;

/**
 * Example showing a standard BPT simple parallel creation from one image.
 *
 */
public class CreateSimpleParallelBPT {

	public static void main(String[] args) {
		
		String path = "xp//examples//six_regions_3_3.png";
		BufferedImage image = ImTool.read(path);
		
		Log.show = true;
		Tree bptp = new BPTP(image);
		bptp.grow();
		
		if(bptp.hasEnded()) {
			
			System.out.println("[Test] Simple Parallel BPT Creation succeded!");
		}
	}
}
