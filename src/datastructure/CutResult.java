package datastructure;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * A cut performed on a BPT gives a partition regrouping regions at a precised hierarchy level.
 * 
 * <p>
 * Important attributes are:
 * 
 * <li> a set of images that are partitions at each level
 * <li> a set of regions at each level
 *
 */
public class CutResult {

	/**
	 * Hierarchical structure owning the cutting results.
	 */
	public Tree tree;
	
	/**
	 * Set of partitions for each level. 
	 */
	public TreeMap<Integer, BufferedImage> regionImages;
	
	/**
	 * Set of regions contained in a partition for each level.
	 */
	public TreeMap<Integer, ArrayList<Node>> relatedNodes;
	
	/**
	 * Creates and prepares the place where to store the different cutting results.  
	 * 
	 * @param tree on which the cut will be performed; should not be null
	 * 
	 * @throws NullPointerException if tree is null
	 */
	public CutResult(Tree tree) {

		this.tree = tree;
		regionImages = new TreeMap<Integer, BufferedImage>();
		relatedNodes = new TreeMap<Integer, ArrayList<Node>>();
	}
	
	/**
	 * Saving the partition and its regions (~nodes).
	 * 
	 * @param nbRegions index determining the current level; should be > 0
	 * @param regionImage image partition of the current level; should not be null
	 * @param activeNodesList regions of the current level; should not be null
	 * 
	 * @throws NullPointerException if regionImage is null nor activeNodesList is null
	 */
	public void add(int nbRegions, BufferedImage regionImage, ArrayList<Node> activeNodesList) {
		
		ArrayList<Node> list = new ArrayList<Node>();
		list.addAll(activeNodesList);
		regionImages.put(nbRegions, regionImage);
		relatedNodes.put(nbRegions, list);
	}

	/**
	 * 
	 * @param nbRegions used as index determining a specific level
	 * @return the partition having the precised number of regions
	 */
	public BufferedImage get(int nbRegions) { 
		
		return regionImages.get(nbRegions); 
	}

	/**
	 * 
	 * @param nbRegions used as index determining a specific level
	 * @return the set of nodes representing the regions forming the partition at a precised level
	 */
	public ArrayList<Node> getRelatedNodes(int nbRegions){ 
		
		return relatedNodes.get(nbRegions); 
	}

	/**
	 * Delete the partition and the set of regions saved at a precised level
	 * 
	 * @param nbRegions index determining a specific level
	 */
	public void remove(int nbRegions) {
		
		regionImages.remove(nbRegions);
		relatedNodes.remove(nbRegions);
	}
}
