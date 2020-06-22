package metric.shape;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import datastructure.Node;
import metric.bricks.Metric;
import metric.bricks.MetricInterface;
import metric.bricks.ToolsMetric;
import utils.Log;

/**
 * 
 * Metric based on the shape of the regions. Precisely, this metric focuses on the elongation of the regions.
 * The computation of the distance between two regions is determined by their respective elongation value.
 * This elongation value is between 0 and 1 (~ [0, 1]). The value near 1 means that the object is not long at all.
 * The longest object will have an elongation value near 0.
 * Long regions will be chosen first during the merging session.
 * (!) All metric classes must inherit from the 'Metric' class and implement the interface 'MetricInterface' and override all its methods.
 * 
 * @author C. Kurtz
 *
 */
public class Elongation extends Metric implements MetricInterface{

	/**
	 * Position of the elongation feature for all nodes
	 */
	int elongPos = -1;
	
	/**
	 * Register an image within the metric and create the metric object based on the elongation of a region (~ NODE).
	 * 
	 * @param type of the metric to consider
	 * @param image should not be null
	 * 
	 * @throws NullPointerException if image is null
	 */
	public Elongation(TypeOfMetric type, BufferedImage image) {
		
		this.type = type;
		this.img = image;
		
		/* define the position of the feature */
		this.elongPos = ++Metric.currentFeaturePos;
		
		Log.println(String.valueOf(this.type), "Metric prepared!");
	}
	
	/**
	 * Computes a distance between 'n1' and 'n2' using the Metric Features (MF):
	 * - elongation: value associated with the elongation shape of the region (~ node).</br>
	 * 
	 * @param n1 First Node; should not be null
	 * @param n2 Second Node; should not be null
	 * @return a score (~ distance) between 'n1' and 'n2'.
	 * (!) TODO - think again about it.
	 * 
	 * @throws NullPointerException if n1 or n2 is null
	 */
	@Override
	public double computeDistances(Node n1, Node n2) {
		
		double score = 0.0;
	
		double elongation1 = n1.features.get(this.elongPos);
		double elongation2 = n2.features.get(this.elongPos);
	
		double averageChildren =  (elongation1 + elongation2)/2.0;
		//double averageChildren =  (n1.size * elongation1 + n2.size * elongation2)/(n1.size + n2.size );
			
		ArrayList<Point> pointsFakeFather = new ArrayList<Point>();
		pointsFakeFather.addAll(n1.getPixels());
		pointsFakeFather.addAll(n2.getPixels());
		
		//Bounding box fake father
		int [] boundingBox = new int[4];
		boundingBox[0] = Math.min(n1.boundingBox[0],n2.boundingBox[0]);
		boundingBox[1] = Math.max(n1.boundingBox[1], n2.boundingBox[1]);
		boundingBox[2] = Math.min(n1.boundingBox[2], n2.boundingBox[2]);
		boundingBox[3] = Math.max(n1.boundingBox[3], n2.boundingBox[3]);
		
		double elongationpotentialFather = Elongation.computeElongation(this.type, pointsFakeFather,boundingBox,this.img.getWidth(), this.img.getHeight());
		
		score = Math.abs(elongationpotentialFather - averageChildren);
		
		return score;
	}
	
	/**
	 * @param typeOfElongation defining how to compute the elongation score (e.g.: fast, simple, ...)
	 * @param listOfPoints group of connected pixels defining a region
	 * @param boundingBox encompassing the region
	 * @param imgWidth should be > 0
	 * @param imgHeight should be > 0
	 * @return an elongation score of a list of Points by computing its Bounding Box
	 * 
	 * @throws NullPointerException if typeOfElongation or listOfPoints or boundingBox is null
	 * @throws IndexOutOfBoundsException if imgWidth or imgHeight does not fit the dimension of the image
	 */
	public static double computeElongation(TypeOfMetric typeOfElongation, ArrayList<Point> listOfPoints, int [] boundingBox,  int imgWidth, int imgHeight) {	
		
		//FIXME - If the region is big, no need to consider it.
		if(listOfPoints.size()>10000)
			return 1.0;
		
		if(listOfPoints.size() == 0)
			return 1.0;
		else if(listOfPoints.size() == 1)
			return 1.0;
		if(typeOfElongation == TypeOfMetric.FAST_ELONGATION)
			return computeElongation3(listOfPoints.size(),boundingBox,imgWidth,imgHeight);
		else if(typeOfElongation == TypeOfMetric.SIMPLE_ELONGATION)
			return computeElongation1(listOfPoints, boundingBox,imgWidth,imgHeight);
			else return computeElongation2(listOfPoints, boundingBox,imgWidth,imgHeight);
	}
	
	/**
	 * 
	 * @param listOfPoints group of connected pixels defining a region
	 * @param boundingBox encompassing the region
	 * @param imgWidth should be > 0
	 * @param imgHeight should be > 0
	 * @return an elongation score of a list of Points by computing its Bounding Box
	 *
	 * @throws NullPointerException if listOfPoints or boundingBox is null
	 * @throws IndexOutOfBoundsException if imgWidth or imgHeight does not fit the dimension of the image
	 */
	public static double computeElongation1(ArrayList<Point> listOfPoints, int [] boundingBox,  int imgWidth, int imgHeight) {	
		
		if(listOfPoints.size() == 0)
			return 1.0;
		else if(listOfPoints.size() == 1)
			return 1.0;
		
		double elongation = 0.0;
				
		/* On teste dans les coordonnées normales,
		 * rotationnées de 45° sens horaire de rotation
		 */
		double e1 = computeElongationAABB(listOfPoints, imgWidth, imgHeight, 1, 0, 0, 1, 1);
		double e2 = computeElongationAABB(listOfPoints, imgWidth, imgHeight, 3, 1, -1, 3, 1.0 / Math.sqrt(10.0));
		double e3 = computeElongationAABB(listOfPoints, imgWidth, imgHeight, 2, 1, -1, 2, 1.0 / Math.sqrt(5.0));
		double e4 = computeElongationAABB(listOfPoints, imgWidth, imgHeight, 1, 1, -1, 1, 1.0 / Math.sqrt(2.0));
		double e5 = computeElongationAABB(listOfPoints, imgWidth, imgHeight, 1, 2, -2, 1, 1.0 / Math.sqrt(5.0));
		double e6 = computeElongationAABB(listOfPoints, imgWidth, imgHeight, 1, 3, -3, 1, 1.0 / Math.sqrt(10.0));
		
		double min1= Math.min(e1, e2);
		double min2= Math.min(e3, e4);
		double min3= Math.min(e5, e6)	;
		elongation = Math.min( Math.min(min1, min2) , min3 );
		
		return elongation;
	}
	
	/**
	 * According to the contained points in the node (~ region), compute a value that will determine if the region (~ node) is long or not.
	 * @param listOfPoints List of points forming the region (~ node); should not be null
	 * @param imgWidth Width of the image; should be > 0
	 * @param imgHeight Height of the image; should be > 0
	 * @return a value between 0 and 1 associated with the elongation of the region
	 * 
	 * @throws NullPointerException if listOfPoints is null
	 * @throws IndexOutOfBoundsException if imgWidth or imgEight does not fit the image
	 */
	public static double computeElongation2(ArrayList<Point> listOfPoints, int [] boundingBox,  int imgWidth, int imgHeight) {
		
		double elongation = 1;
	
		/* Finding the border pixels */
		ArrayList<Integer>bord = new  ArrayList<Integer>();
		// -Boundig box
		int minX = boundingBox[0];
		int maxX = boundingBox[1];
		int minY = boundingBox[2];
		int maxY = boundingBox[3];
	
	
		int tailleMatriceX=maxX-minX+ 1;
		int tailleMatriceY=maxY-minY+ 1;
		boolean [][] matrice=new boolean[tailleMatriceX][tailleMatriceY];
	
		for(int i=0; i<matrice.length; i++)
			Arrays.fill(matrice[i], false);
	
		int x, y, xp, yp;
		for(Point p : listOfPoints) {
	
			matrice[p.x][p.y]=true;
		}
	
		/* For each point */
		for(x=0;x<tailleMatriceX;++x){
			for(y=0;y<tailleMatriceY;++y){
	
				boolean estAuBord=false;
	
				if(matrice[x][y]==true){
	
					for (xp = x - 1; xp <= x + 1; ++xp) {
						for (yp = y - 1; yp <= y + 1; ++yp) {
							if((xp!=x || yp!=y)){
								if(xp>=0 && yp>=0 && xp<tailleMatriceX && yp<tailleMatriceY ){
									if(matrice[xp][yp]==false){
										estAuBord=true;
									}
								}else{
									estAuBord=true;
								}
							}
						}
					}
	
					if(estAuBord)
						bord.add((x+minX) + (y+minY)*imgWidth);
	
				}
			}
	
		}
	
		/* We have now the border pixels */
		/* Find the greatest and the smallest segment that we can draw in the region */
		double[] maxs=new double[bord.size()];
	
		double maxTemp, distance;
		for(int i=0; i<bord.size(); ++i) {
	
			Integer p=bord.get(i);
			maxTemp=0.0;
	
			for(Integer p_temp : bord) {
				distance= Math.sqrt( ((p_temp %imgWidth)  - (p %imgWidth)) * ((p_temp %imgWidth)  - (p %imgWidth))
						+  ((p_temp /imgWidth)  - (p /imgWidth)) * ((p_temp /imgWidth)  - (p /imgWidth)));
	
				if(distance>maxTemp)
					maxTemp=distance;
			}
	
			maxs[i]=maxTemp;
	
		}
	
		double grandCote=maxs[0];
		double petitCote=maxs[0];
	
		for(int i=1; i<maxs.length; ++i) {
			if(maxs[i]>grandCote)
				grandCote=maxs[i];
	
			if(maxs[i]<petitCote)
				petitCote=maxs[i];
	
		}
	
		elongation = ((petitCote/grandCote)-0.5)*2.0;
	
		return elongation;
	}

	/**
	 * 
	 * @param sizeListOfPoints number of points forming the region of interest
	 * @param boundingBox encompassing the region
	 * @param imgWidth should be > 0
	 * @param imgHeight should be > 0
	 * @return an elongation score of the region of interest
	 * 
	 * @throws NullPointerException if boundingBox is null
	 * @throws IndexOutOfBoundsException if sizeListOfPoints or imgWidth or imgHeight does not fit the image
	 */
	public static double computeElongation3(int sizeListOfPoints, int [] boundingBox,  int imgWidth, int imgHeight) {	
		
		if(sizeListOfPoints == 0)
			return 1.0;
		else if(sizeListOfPoints == 1)
			return 1.0;			
		
		int minX = boundingBox[0];
		int maxX = boundingBox[1];
		int minY = boundingBox[2];
		int maxY = boundingBox[3];
		
		double x = (maxX - minX);
		double y = (maxY - minY);
		
		if(x < y){
			if(y==0) y=1;
			return x/y;
		}else{
			if(x==0) x=1;
			return y/x;
		}
	}
	
	/**
	 * @param listOfPoints group of pixels forming a region
	 * @param imgWidth should be > 0
	 * @param imgHeight should be > 0
	 * @param xx
	 * @param xy
	 * @param yx
	 * @param yy
	 * @param scale
	 * @return an elongation score of the axis aligned bounding box in a modified referential
	 */
	private static double computeElongationAABB(ArrayList<Point> listOfPoints,  int imgWidth, int imgHeight,int xx, int xy, int yx, int yy, double scale) {
		
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		// Point dans l'espace transformé
		Point p2 = new Point();
		
		for(Point p : listOfPoints) {

			ToolsMetric.conversion(p, p2, xx, xy, yx, yy);
			if(p2.x < minX)
				minX = p2.x;
			if(p2.x > maxX)
				maxX = p2.x;
			if(p2.y < minY)
				minY = p2.y;
			if(p2.y > maxY)
				maxY = p2.y;
		}
		
		double x = (maxX - minX)*scale + 1.0;
		double y = (maxY - minY)*scale + 1.0;
		
		if(x < y)
			return x/y;
		else
			return y/x;
	}
	
	/**
	 * Prepares all the Metric Features (MF) corresponding to the elongation value of the specified region (~ node):</br>
	 * - elongation: value associated with the elongation shape of the region (~ node).</br>
	 * @param n Concerned node.
	 */
	@Override
	public void initMF(Node n) {

		/* Nothing else to initiate */
	}

	/**
	 * Initiates or update the values of the Metric Features (MF):</br>
	 *- elongation: value associated with the elongation shape of the region (~ node).</br>
	 * @param n Concerned node.
	 */
	@Override
	public void updateMF(Node n) {
		
		/*
		 * Compute region elongation.
		 */
		double elongation = Elongation.computeElongation(this.type, n.getPixels(), n.boundingBox, this.img.getWidth(), this.img.getHeight());
		
		/*
		 * Set or Update the node metric feature (~ MF).
		 */
		n.features.put(this.elongPos, elongation);
	}
}