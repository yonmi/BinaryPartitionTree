package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import datastructure.Node;

public class TreeVisu {

    /**
     * Shows the structure of tree and store it into a file
     * @param root of the tree 
     * @param savePath file to create to store the structure  of the tree; if null the structure is not saved
     */
	public static void display(Node root, String savePath) {
		
		if(savePath != null){ /* saving */
        	/* Create the file and start to write in it. */
			try{

				PrintWriter writer = new PrintWriter(savePath, "UTF-8");
				show(root, 0, new ArrayList<Integer>(), writer);
				writer.close();

			}catch(IOException e){
				e.printStackTrace();
			}			
        }else /* not saving */
        	show(root, 0, new ArrayList<Integer>(), null);
		
	}
    
	/**
	 * Core method drawing the tree structure
	 * @param n node from which the drawing starts
	 * @param lvl from which the drawing starts
	 * @param bracket list remembering each block
	 * @param writer needed if the structure has to be stored in a file
	 */
	private static void show(Node n, int lvl, ArrayList<Integer> bracket, PrintWriter writer){
		
		String line = "`--";
		String space = "";
		String indent = "  ";
		String bar = " |";
		ArrayList<Integer> bracketRight = new ArrayList<Integer>();
		bracketRight.addAll(bracket);
		
		if(n != null){
			
			StringBuilder s = new StringBuilder();
			for(int i=0; i<lvl; ++i){

				if(bracket.contains(i))
					space = space + bar;
				else space = space + indent;

			}
			
			s.append(space).append(line).append(n.name+":"+n.lvl);
			System.out.println(s);
			if(writer != null)
				writer.println(s);
			
			bracket.add(lvl);
			lvl += 1;
			show(n.leftNode, lvl, bracket, writer);
			show(n.rightNode, lvl, bracketRight, writer);
			
		}	
	}
}
