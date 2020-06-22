package examples.standard.sequential;

import datastructure.Node;
import datastructure.Tree;
import standard.sequential.BPT;
import utils.Formula;
import utils.TreeVisu;

/**
 * Example loading a BPT from a file.
 *
 */
public class LoadingBPTFromFile {

	public static void main(String[] args) {
		
		String filePath = "xp//examples//tree.bpt.h5";
		//String filePath = "xp//tree3.bpt.h5";
		
		Tree bpt = new BPT(filePath); // re-growing is included.
		
		if(bpt != null) {
			
			System.out.println("[Test] BPT regrow succeded!");
			
			/* Get the pixels of a leaf from the hdf5 file */
			Node leaf = bpt.getNodes()[3];
			int[] points = bpt.getPixels(leaf);
			
			for(int i = 0; i < points.length; ++i){
				
				int p = points[i];
				int x = Formula.toX(p, bpt.getMaxLonger());
				int y = Formula.toY(p, bpt.getMaxLonger());
				System.out.println("point: "+ p +" x: " + x +" y: "+ y);
			}
		}
		
		// Drawing the tree in a file
		Node root = bpt.getRoot();
		String savePath = "xp//tree3-visu.txt";
		TreeVisu.display(root, savePath);
	}
}
