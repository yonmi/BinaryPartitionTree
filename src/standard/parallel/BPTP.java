package standard.parallel;

import java.awt.image.BufferedImage;
import java.util.HashMap;

import datastructure.Node;
import datastructure.set.SetOfAdjacencies;
import standard.parallel.bricks.IndividualTask;
import standard.sequential.BPT;
import utils.Log;

public class BPTP extends BPT{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum Side{
		
		PART1,
		PART2,
		PART3,
		PART4,
		COMMON
	}
	
	public HashMap<Side, SetOfAdjacencies> sideResource;
	public HashMap<Side, IndividualTask> sideTasks;
	
	public BPTP() {
		
		super();
	}
	
	public BPTP(BufferedImage image) {

		super(image);
	}

	@Override
	public void grow() {
		
		this.prepareLabelMatrix();
		
		/* Split limit */
		int verticalFrontier = this.image.getWidth() / 2;
		int horizontalFrontier = this.image.getHeight() / 2;
	
		/* Prepare set of adjacencies structures */
		this.setOfAdjacencies = null;
		SetOfAdjacencies adjaSet1 = new SetOfAdjacencies();
		adjaSet1.currentNodeIndex = 0;
		adjaSet1.xMin = 0;
		adjaSet1.xMax = verticalFrontier;
		adjaSet1.yMin = 0;
		adjaSet1.yMax = horizontalFrontier;
	
		SetOfAdjacencies adjaSet2 = new SetOfAdjacencies();
		adjaSet2.currentNodeIndex = 0;
		adjaSet2.xMin = verticalFrontier;
		adjaSet2.xMax = this.image.getWidth();
		adjaSet2.yMin = 0;
		adjaSet2.yMax = horizontalFrontier;
	
		SetOfAdjacencies adjaSet3 = new SetOfAdjacencies();
		adjaSet3.currentNodeIndex = 0;
		adjaSet3.xMin = 0;
		adjaSet3.xMax = verticalFrontier;
		adjaSet3.yMin = horizontalFrontier;
		adjaSet3.yMax = this.image.getHeight();
	
		SetOfAdjacencies adjaSet4 = new SetOfAdjacencies();
		adjaSet4.currentNodeIndex = 0;
		adjaSet4.xMin = verticalFrontier;
		adjaSet4.xMax = this.image.getWidth();
		adjaSet4.yMin = horizontalFrontier;
		adjaSet4.yMax = this.image.getHeight();
	
		this.sideResource = new HashMap<Side, SetOfAdjacencies>();
		this.sideResource.put(Side.PART1, adjaSet1);
		this.sideResource.put(Side.PART2, adjaSet2);
		this.sideResource.put(Side.PART3, adjaSet3);
		this.sideResource.put(Side.PART4, adjaSet4);
		
		Log.println(context, "Starting tree creation.");
		
		long startingTime = System.nanoTime();
		
		/* Prepare the list of nodes */
		int nbPixels = this.image.getWidth() * this.image.getHeight();
		this.nodes = new Node[(nbPixels * 2) - 1];
		Log.println(context, "Nb nodes to create: "+ this.nodes.length +" (including leaves)");
		
		/* Prepare the indifidual tasks */
		IndividualTask task1 = new IndividualTask(adjaSet1, this, Side.PART1, false);
		IndividualTask task2 = new IndividualTask(adjaSet2, this, Side.PART2, false);
		IndividualTask task3 = new IndividualTask(adjaSet3, this, Side.PART3, false);
		IndividualTask task4 = new IndividualTask(adjaSet4, this, Side.PART4, true);
	
		this.sideTasks = new HashMap<Side, IndividualTask>();
		this.sideTasks.put(task1.side, task1);
		this.sideTasks.put(task2.side, task2);
		this.sideTasks.put(task3.side, task3);
		this.sideTasks.put(task4.side, task4);
		
		/* Start and join individual tasks */
		try {
			
			task1.start();
			task2.start();
			task3.start();
			task4.start();
			
			task1.join();
			task2.join();
			task3.join();
			task4.join();
			
		} catch (InterruptedException e) {
	
			e.printStackTrace();
		}
	
		this.nbLeaves = adjaSet1.nbLeaves + adjaSet2.nbLeaves + adjaSet3.nbLeaves + adjaSet4.nbLeaves;
		Log.println(context, "Nb leaves created: "+ this.nbLeaves +" nb leaves1: "+ adjaSet1.currentNodeIndex +" nb leaves2: "+ adjaSet2.currentNodeIndex +" nb leaves3: "+ adjaSet3.currentNodeIndex +" nb Leaves4: "+ adjaSet4.currentNodeIndex);
	
		/* Regroup leaves */
		adjaSet1.currentNodeIndex = 0;
		adjaSet2.currentNodeIndex = 0;
		adjaSet3.currentNodeIndex = 0;
		adjaSet4.currentNodeIndex = 0;
		int index = 0;
		for(int i = 0; i < adjaSet1.nbLeaves; i++) {
			
			this.nodes[index] = adjaSet1.nodes[i];
			this.nodes[index].name = index;
			this.nodes[index].label = index;
			adjaSet1.currentNodeIndex++;
			index++;
		}
		
		for(int i = 0; i < adjaSet2.nbLeaves; i++) {
			
			this.nodes[index] = adjaSet2.nodes[i];
			this.nodes[index].name = index;
			this.nodes[index].label = index;
			adjaSet2.currentNodeIndex++;
			index++;
		}
		
		for(int i = 0; i < adjaSet3.nbLeaves; i++) {
			
			this.nodes[index] = adjaSet3.nodes[i];
			this.nodes[index].name = index;
			this.nodes[index].label = index;
			adjaSet3.currentNodeIndex++;
			index++;
		}
		
		for(int i = 0; i < adjaSet4.nbLeaves; i++) {
			
			this.nodes[index] = adjaSet4.nodes[i];
			this.nodes[index].name = index;
			this.nodes[index].label = index;
			adjaSet4.currentNodeIndex++;
			index++;
		}
	
		
		/* Regroup nodes */
		try {
		while(adjaSet1.currentNodeIndex < adjaSet1.nodes.length ||
			  adjaSet2.currentNodeIndex < adjaSet2.nodes.length ||
			  adjaSet3.currentNodeIndex < adjaSet3.nodes.length ||
			  adjaSet4.currentNodeIndex < adjaSet4.nodes.length) {
			
			Node node1 = null, node2 = null, node3 = null, node4 = null, nodeL = null, nodeR = null;
			SetOfAdjacencies setToUseL = null, setToUseR = null;
	
			if(adjaSet1.currentNodeIndex < adjaSet1.nodes.length) {
				
				node1 = adjaSet1.nodes[adjaSet1.currentNodeIndex];
			}
			
			if(adjaSet2.currentNodeIndex < adjaSet2.nodes.length) {
				
				node2 = adjaSet2.nodes[adjaSet2.currentNodeIndex];
			}
	
			if(adjaSet3.currentNodeIndex < adjaSet3.nodes.length) {
				
				node3 = adjaSet3.nodes[adjaSet3.currentNodeIndex];
			}
			
			if(adjaSet4.currentNodeIndex < adjaSet4.nodes.length) {
				
				node4 = adjaSet4.nodes[adjaSet4.currentNodeIndex];
			}
			
			if(node1 != null) {
				
				nodeL = node1;
				setToUseL = adjaSet1;
			}
			
			if(node2 != null) {
				
				if(nodeL != null) {
					
					if(node2.merginScore < nodeL.merginScore) {
						
						nodeL = node2;
						setToUseL = adjaSet2;
					}
					
				}else {
					
					nodeL = node2;
					setToUseL = adjaSet2;
				}
			}
			
			if(node3 != null) {
				
				nodeR = node3;
				setToUseR = adjaSet3;
			}
			
			if(node4 != null) {
				
				if(nodeR != null) {
					
					if(node4.merginScore< nodeR.merginScore) {
						
						nodeR = node4;
						setToUseR = adjaSet4;
					}
					
				}else {
					
					nodeR = node4;
					setToUseR = adjaSet4;
				}
			}
			
			if(nodeL != null) {
				
				if(nodeR != null) {
					
					if(nodeL.merginScore < nodeR.merginScore) {
						
						this.nodes[index] = nodeL;
						this.nodes[index].setName(index);
						this.nodes[index].updateLabel();
						setToUseL.currentNodeIndex++;
						index++;
						continue;
						
					}else {
						
						this.nodes[index] = nodeR;
						this.nodes[index].setName(index);
						this.nodes[index].updateLabel();
						setToUseR.currentNodeIndex++;
						index++;
						continue;
					}
				}else {
					
					this.nodes[index] = nodeL;
					this.nodes[index].setName(index);
					this.nodes[index].updateLabel();
					setToUseL.currentNodeIndex++;
					index++;
					continue;
				}
				
			}else {
				
				if(nodeR != null) {
					
					this.nodes[index] = nodeR;
					this.nodes[index].setName(index);
					this.nodes[index].updateLabel();
					setToUseR.currentNodeIndex++;
					index++;
					continue;
				}
			}
		}
		}catch(Exception e) {e.printStackTrace();}
		
		this.nbNodes = adjaSet1.currentNodeIndex + adjaSet2.currentNodeIndex + adjaSet3.currentNodeIndex + adjaSet4.currentNodeIndex;
		long endingTime = System.nanoTime();
		this.timeMs = (endingTime - startingTime)/1000000;
		this.timeS = this.timeMs / 1000;
		Log.println(context, "Part1 Nb Adjacencies remaining: "+ adjaSet1.size());
		Log.println(context, "Part2 Nb Adjacencies remaining: "+ adjaSet2.size());
		Log.println(context, "Part3 Nb Adjacencies remaining: "+ adjaSet3.size());
		Log.println(context, "Part4 Nb Adjacencies remaining: "+ adjaSet4.size());
		Log.println(context, "Nb nodes created: "+ (this.nbNodes + adjaSet1.foreignNodes.size() + adjaSet2.foreignNodes.size() + adjaSet3.foreignNodes.size() + adjaSet4.foreignNodes.size()));		
		Log.println(context, "Tree creation in "+ timeMs +" ms ("+ this.timeS +" s)");
	}
}
