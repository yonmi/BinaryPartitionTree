package examples.standard.sequential;

import java.awt.image.BufferedImage;

import datastructure.Tree;
import standard.sequential.BPT;
import utils.ImTool;
import utils.SaveBPT;

/**
 * Example saving a created BPT in a file having .bpt format.
 *
 */
public class SavingBPTIntoFile {

	public static void main(String[] args) {

		boolean success = false;
		
		try {

			//String path = "xp//examples//six_regions_3_3.png";
			//String path = "xp//pleiade_stbg.tif";
			String path = "xp//examples//border_test.png";
			BufferedImage image = ImTool.read(path);
			
			String presegPath = "xp//examples//border_test.png";
			BufferedImage presegImage = ImTool.read(presegPath);
			
			Tree bpt = new BPT(image);
			bpt.setPreSegImage(presegImage);
			bpt.grow();
			
			if(bpt != null) {
				
				bpt.setName("tree.bpt");
				bpt.setDirectory("xp//examples");

				//bpt.setName("tree2.bpt");
				//bpt.setName("tree3.bpt");
				//bpt.setDirectory("xp");
				
				// Saving the tree structure in a .dot file and info in .css
				SaveBPT.toDOT(bpt);
				
				// Saving the tree structure in a .xml file and info in .css
				SaveBPT.toGRAPHML(bpt);
				
				// Saving the info and the tree structure in a .h5 (hdf5) file
				SaveBPT.toHDF5(bpt);

				success = true;
	
			}
						
		}catch(Exception e){
			
			e.printStackTrace();
		}
		
		if(success)	System.out.println("[Test] BPT saved successfully!");
	}
}
