package datastructure.set;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import datastructure.Adjacency;
import utils.Log;

public class AdjacencySameScore implements Comparable<AdjacencySameScore>{

	public double score;
	
	public Set<Adjacency> bucket;
	
	public AdjacencySameScore(double distance) {

		this.score = distance;
		this.bucket = new HashSet<Adjacency>();
	}

	public boolean add(Adjacency adjacency) {

		return this.bucket.add(adjacency);
	}

	public boolean contains(Adjacency adjacency) {

		return this.bucket.contains(adjacency);
	}

	@Override
	public int compareTo(AdjacencySameScore adjacencySameScore) {

		if(this.score == adjacencySameScore.score) {
			
			return 0;
			
		}else if(this.score < adjacencySameScore.score) {
			
			return -1;
			
		}else return 1;
	}
	
	@Override
	public boolean equals(Object o) {
	
		if (o == this) return true;
        
		if (!(o instanceof AdjacencySameScore)) {
			
            return false;
        }
		
		return this.score == ((AdjacencySameScore) o).score;
	}
	
	@Override
    public int hashCode() {
    	
        return Objects.hash(this.score);
    }
	
	public boolean isEmpty() {

		return this.bucket.isEmpty();
	}

	public Adjacency optimal() {
	
		return this.bucket.iterator().next();
	}

	public boolean remove(Adjacency adjacency) {

		boolean removed = this.bucket.remove(adjacency);
		
		if(!removed) {
			
			Log.println("SetOfAdjacencies", adjacency.getIndex() +"Not found");
		}
		return removed;
	}

	public int size() {

		return this.bucket.size();
	}	
}
