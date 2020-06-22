package datastructure.set;

import java.util.Comparator;

import datastructure.Adjacency;

/**
 * Comparator used to maintain a structure as the listW ordered.
 *
 * <p>
 * Two adjacency links a1 and a2 can be compared and the results can be:
 * 
 *  <li> 0: a1 and a2 are same
 *  <li> > 0: a1 > a2
 *  <li> < 0: a1 < a2
 */
public class AdjaComparator implements Comparator<Adjacency>{

	/**
	 * Identification of the listW or setW that has to be ordered according to this comparator
	 */
	public int listIndex;
	
	public boolean maintainChaining = false;
	
	/**
	 * Creates a comparator by specifying the index of the listW associated with it
	 * 
	 * @param listIndex defining the listW
	 * @param maintainChaining if true, the chaining links is maintained during comparisons
	 */
	public AdjaComparator(int listIndex, boolean maintainChaining){
		
		this.listIndex = listIndex;
		this.maintainChaining = maintainChaining;
	}
	
	@Override
	public int compare(Adjacency a1, Adjacency a2) {

		double distance1 = a1.scores[this.listIndex];
		double distance2 = a2.scores[this.listIndex];
		int res = 0;
		
		if(distance1 == distance2) {
	
			res = a1.compareTo(a2);
			
		}else if(distance1 < distance2) res = -1;
		else res = 1;
		
		if(this.maintainChaining && a1.updateChains[this.listIndex]) {
			
			if(res < 0) {
				
				Adjacency previous2 = a2.previous[this.listIndex];
				
				if(previous2 != null) {
					
					if(distance1 > previous2.scores[this.listIndex] || (distance1 == previous2.scores[this.listIndex] && a1.compareTo(previous2) == 1)) {

						previous2.next[this.listIndex] = a1;
						a1.previous[this.listIndex] = previous2;
						
						a1.next[this.listIndex] = a2;
						a2.previous[this.listIndex] = a1;
						
						a1.updateChains[this.listIndex] = false;
					}
					
				}else {
					
					a1.next[this.listIndex] = a2;
					a2.previous[this.listIndex] = a1;
					a1.updateChains[this.listIndex] = false;
				}
				
			}else if(res > 0){
				
				Adjacency next2 = a2.next[this.listIndex];
				
				if(next2 != null) {
					
					if(distance1 < next2.scores[this.listIndex] || (distance1 == next2.scores[this.listIndex] && a1.compareTo(next2) == -1)) {

						next2.previous[this.listIndex] = a1;
						a1.next[this.listIndex] = next2;
						
						a2.next[this.listIndex] = a1;
						a1.previous[this.listIndex] = a2;
						a1.updateChains[this.listIndex] = false;
					}					
				}else {
					
					a2.next[this.listIndex] = a1;
					a1.previous[this.listIndex] = a2;
					a1.updateChains[this.listIndex] = false;
				}
			}
		}
		
		return res;
	}
}
