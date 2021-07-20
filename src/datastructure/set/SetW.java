/****************************************************************************
* Copyright AGAT-Team (2014)						       
* 									    
* Contributors:								
* J.F. Randrianasoa							    
* K. Kurtz								    
* E. Desjardin								    
* N. Passat								    
* 									    
* This software is a computer program whose purpose is to [describe	    
* functionalities and technical features of your software].		    
* 									    
* This software is governed by the CeCILL-B license under French law and    
* abiding by the rules of distribution of free software.  You can  use,     
* modify and/ or redistribute the software under the terms of the CeCILL-B  
* license as circulated by CEA, CNRS and INRIA at the following URL	    
* "http://www.cecill.info". 						    
* 									    
* As a counterpart to the access to the source code and  rights to copy,    
* modify and redistribute granted by the license, users are provided only   
* with a limited warranty  and the software's author,  the holder of the    
* economic rights,  and the successive licensors  have only  limited	    
* liability. 								    
* 									    
* In this respect, the user's attention is drawn to the risks associated    
* with loading,  using,  modifying and/or developing or reproducing the     
* software by the user in light of its specific status of free software,    
* that may mean  that it is complicated to manipulate,  and  that  also	   
* therefore means  that it is reserved for developers  and  experienced     
* professionals having in-depth computer knowledge. Users are therefore     
* encouraged to load and test the software's suitability as regards their   
* requirements in conditions enabling the security of their systems and/or  
* data to be ensured and,  more generally, to use and operate it in the     
* same conditions as regards security. 					    
*								            
* The fact that you are presently reading this means that you have had	    
* knowledge of the CeCILL-B license and that you accept its terms.          
* 									   		
* The full license is in the file LICENSE, distributed with this software.  
*****************************************************************************/

package datastructure.set;

import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import datastructure.Adjacency;
import datastructure.ListW;
import datastructure.Adjacency.State;
import metric.bricks.Metric;
import metric.bricks.MetricFactory;
import multi.strategy.consensus.bricks.Consensus;
import metric.bricks.Metric.TypeOfMetric;
import utils.ImTool;
import utils.Log;

/**
 * A @link {@link TreeSet} based structure containing all the adjacency links of the region adjacency graph (RAG).
 * 
 * <p> The structure is maintained ordered and allows each adjacency to know its rank (~position) among all.
 * It also associates and links an image and a similarity metric to each other.
 * The similarity distances computed with the metric allow the management of the order although a chaining structure helps to keep the ranks updated.
 *
 */
public class SetW implements ListW{

	/**
	 * knowing the strategy used when creating the structure of hierarchy will help to minimize the instructions for some methods (e.g.: reduction by not updating the chaining links between consecutive adjacency links as the ranks are not used.
	 */
	public Consensus strategy;
	
	/**
	 * Identification of the setW (or listW by language abuse)
	 */
	public int index;
	
	/**
	 * Similarity metric to consider while computing the distance scores
	 */
	public Metric metric;
	
	/**
	 * Core structure containing the ordered adjacency links
	 */
	public TreeSet<Adjacency> set;
	
	/**
	 * Determines where to start the rank update
	 */
	public Adjacency updateStart;
	
	/**
	 * Creates a setW that will be ready to contain and order adjacency links 
	 * 
	 * @param listIndex identification
	 * @param image of interest; should not be null
	 * @param metricType defining the similarity metric to consider; should not be null
	 * @param consensus {@link SetW#strategy strategy} helping the choice of the couple of nodes to merge when creating a structure of hierarchy
	 * 
	 * @throws NullPointerException if adjacency or metricTye is null
	 */
	public SetW(int listIndex, BufferedImage image, TypeOfMetric metricType, Consensus consensus) {
		
		this.index = listIndex;
		this.metric = MetricFactory.initMetric(metricType, image);
		this.strategy = consensus;
		AdjaComparator adjaComparator = new AdjaComparator(this.index, this.strategy.needRanks());
		this.set = new TreeSet<Adjacency>(adjaComparator);
		Log.println(String.valueOf(CONTEXT), "ListW linking <Image-"+ ImTool.getNameOf(this.metric.img) +", "+ this.metric.type +"> prepared!");
	}
	
	@Override
	public void add(Adjacency adjacency) {

		boolean succed = this.set.add(adjacency);
		
		if(succed) {
			
			adjacency.updateChains[this.index] = false;
			adjacency.state[this.index] = State.ADDED;
			
			if(this.strategy.needRanks()) {
				
				this.setUpdateStart(adjacency);
			}
		}
	}

	@Override
	public Set<Adjacency> elements() {

		return this.set;
	}

	@Override
	public int getIndex() {

		return this.index;
	}

	@Override
	public Metric getMetric() {

		return this.metric;
	}

	@Override
	public int getRankOf(Adjacency adjacency) {

		return adjacency.ranks[this.index];
	}

	@Override
	public void initRanks() {

		if(this.strategy.needRanks()) {
			
			int r = 1;
			Adjacency prevTmp = null;

			for(Adjacency a: this.elements()){

				a.ranks[this.getIndex()] = r++;
				a.previous[this.getIndex()] = prevTmp;
				if(prevTmp != null) prevTmp.next[this.getIndex()] = a; 
				prevTmp = a;
			}
		}
	}

	@Override
	public boolean isEmpty() {

		return set.isEmpty();
	}

	@Override
	public Iterator<Adjacency> iterator() {

		return this.set.iterator();
	}

	@Override
	public Adjacency optimalElement() {

		return this.set.first();
	}

	@Override
	public void print() {

		System.out.println("size of the listW: "+ this.size());
		
		for(Adjacency adjacency: this.elements()){
			
			System.out.print("\n["+ CONTEXT+this.index +"] adja: "+ adjacency.getIndex() +" rank: ("+ this.getRankOf(adjacency) +") score: "+ adjacency.scores[this.index]);
			
			if(adjacency.previous[this.index] != null) {
				
				System.out.print("  previous: "+ adjacency.previous[this.index].getIndex());
				
			}else System.out.print("  previous: null");
			
			if(adjacency.next[this.index] != null) {
				
				System.out.println("  next: "+ adjacency.next[this.index].getIndex());
			}else System.out.println("  next: null");
		}
	}

	@Override
	public void remove(Adjacency adjacency) {

		adjacency.updateChains[this.index] = false;
		boolean succed = this.set.remove(adjacency);
		
		if(succed){

			adjacency.updateChains[this.index] = false;
			adjacency.state[this.index] = State.REMOVED;
			
			if(this.strategy.needRanks()) {
				
				if(adjacency.next[this.index] != null) {
					
					adjacency.next[this.index].previous[this.index] = adjacency.previous[this.index];
				}
				
				if(adjacency.previous[this.index] != null) {
					
					adjacency.previous[this.index].next[this.index] = adjacency.next[this.index];
				}
				this.setUpdateStart(adjacency);	
			}
		}else {
			
			System.err.println(this.index+ ") Cannot remove: "+ adjacency.getIndex());
		}
	}

	/**
	 * 
	 * @param adjacency potentially used as a starting point when updating the ranks; should not be null
	 * 
	 * @throws NullPointerException if adjacency is null
	 */
	public void setUpdateStart(Adjacency adjacency){
		
		if(updateStart != null){
			
			if(updateStart.compareTo(adjacency) > 0) {
				
				updateStart = adjacency;
			}
			
		}else this.updateStart = adjacency;
	}

	@Override
	public int size() {

		return this.set.size();
	}

	@Override
	public void updateRanks() {

		if(this.updateStart != null){
			
			Adjacency current = this.updateStart;
			Adjacency next = this.updateStart.next[this.index];
			switch(this.updateStart.state[this.index]){
			case ADDED:

				while(next != null){
					
					next.ranks[this.index] = next.previous[this.index].ranks[this.index] + 1;
					current = next;
					next = next.next[this.index];
				}
				break;
				
			case REMOVED:
				
				while(next != null && current.state[this.index]==State.REMOVED){
					
					if(next.previous[this.index] == null) next.ranks[this.index] = 1;
					else next.ranks[this.index] = next.previous[this.index].ranks[this.index] + 1;
					
					current = next;
					next = current.next[this.index];
				}
				
				while(next != null){
					
					next.ranks[this.index] = next.previous[this.index].ranks[this.index] + 1;
					next = next.next[this.index];
				}
				break;
				
			default:
				
				break;
			}
			this.updateStart = null;
		}
	}
}
