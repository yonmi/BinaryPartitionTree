package examples.standard.sequential.xp_rfiap24;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import datastructure.Tree.TypeOfConnectivity;
import datastructure.set.AdjacencySet.OptimalOption;
import metric.others.CooccurrenceMatrixMetric;
import standard.sequential.BPT;
import utils.Log;
import utils.SaveBPT;

/**
 * XP on the example 2 of the paper - MCT - RFIAP24 - ICPR24
 * 1- Prepare values
 * 2- Generate image
 * 3- Build a BPTVC (BPTValue Set using Cooccurrence Matrix
 * 4- Export the BPT to DOT format
 */
public class XpForMCTEx2 {

	public static void main(String[] args) {
		
		/* 1- Prepare values
		 * ************************************************************************************/		
		System.out.println("## Step-1: Prepare values ## ");
		int width = 5;
		int height = 5;
		int nbValues = 9; // number of possible values of each point
		ArrayList<Double> valueSet = new ArrayList<Double>(nbValues);
		for(double i = 0.0; i < nbValues; i++) {
			valueSet.add(i); /* Transform each value to an RGB color with R = i, G = i, B = i */
		}
		System.out.println("Values preparation: SUCCESS \n");

		/* 2- Generate Image
		 * ************************************************************************************/		
		System.out.println("## Step-2: Generate Image ##");
		/* Prepare the image of the paper */
		BufferedImage img = XpForMCTEx2.generateImgExample(width, height, valueSet);
		System.out.println("Image Generation: SUCCESS  \n");

		/* 3- Create a BPTValue Set using a Cooccurrence Matrix
		 * ************************************************************************************/
		System.out.println("## Step-3: BPT creation from a value set (using cooccurrence Matrix) ##");
		Log.show = true;
		
		/* Prepare the tree */
		BPT<Double> bptvc = new BPT<Double>(valueSet);
		bptvc.setOptimalOption(OptimalOption.MAXIMUM);
		
		/* Let all the points of the RAG be connected to all the others */
		bptvc.setConnectivity(TypeOfConnectivity.ALL);

		/* Prepare the metric: cooccurrence matrix metric */
		int connexity = 4;
		CooccurrenceMatrixMetric<Double> metric = new CooccurrenceMatrixMetric<Double>(valueSet, img, connexity); 
		bptvc.setMetric(metric);
						
		/* Let the tree grow */
		bptvc.grow();
		if(bptvc.hasEnded()) {
			
			System.out.println("[Test] BPTVS Creation succeded with the "+ bptvc.getMetric().type +" metric!");
		}
		System.out.println("BPTVC Creation: SUCCESS \n");

		/* 4- Export bptvc to DOT format
		 * ************************************************************************************/
		System.out.println("## Step-4: Export to DOT format ##");
		bptvc.setDirectory("xp"+ File.separator +"rfiap24");
		bptvc.setName("bptvc.dot");
		SaveBPT.toDOT(bptvc);
		System.out.println("BPTVC exportation: "+ bptvc.getDirectory() + File.separator + bptvc.getName());
		System.out.println("BPTVC exportation: SUCCESS \n");
	}

	/**
	 * Just generate an image for the test.
	 * @return a specific generated image.
	 */
	private static BufferedImage generateImgExample(int width, int height, ArrayList<Double> values) {

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);		
		
		/* 1st column */
		img.setRGB(0, 0, genColor(7).getRGB());
		img.setRGB(0, 1, genColor(7).getRGB());
		img.setRGB(0, 2, genColor(8).getRGB());
		img.setRGB(0, 3, genColor(4).getRGB());
		img.setRGB(0, 4, genColor(4).getRGB());
		
		/* 2nd column */
		img.setRGB(1, 0, genColor(6).getRGB());
		img.setRGB(1, 1, genColor(0).getRGB());
		img.setRGB(1, 2, genColor(0).getRGB());
		img.setRGB(1, 3, genColor(0).getRGB());
		img.setRGB(1, 4, genColor(4).getRGB());
		
		/* 3rd column */
		img.setRGB(2, 0, genColor(5).getRGB());
		img.setRGB(2, 1, genColor(5).getRGB());
		img.setRGB(2, 2, genColor(8).getRGB());
		img.setRGB(2, 3, genColor(8).getRGB());
		img.setRGB(2, 4, genColor(6).getRGB());

		/* 4th column */
		img.setRGB(3, 0, genColor(3).getRGB());
		img.setRGB(3, 1, genColor(3).getRGB());
		img.setRGB(3, 2, genColor(4).getRGB());
		img.setRGB(3, 3, genColor(4).getRGB());
		img.setRGB(3, 4, genColor(8).getRGB());

		/* 5th column */
		img.setRGB(4, 0, genColor(0).getRGB());
		img.setRGB(4, 1, genColor(0).getRGB());
		img.setRGB(4, 2, genColor(3).getRGB());
		img.setRGB(4, 3, genColor(3).getRGB());
		img.setRGB(4, 4, genColor(1).getRGB());
		
		return img;
	}
	
	/**
	 * Just transform a gray scale value v into an RGB Color object.
	 * @param v gray scale value.
	 * @return a Color object.
	 */
	private static Color genColor(int v) {
		return  new Color(v, v, v);
	}
}

