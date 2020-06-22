package metric.bricks;

import java.awt.Point;
import java.util.Arrays;
import java.util.Stack;
import java.util.TreeSet;

import datastructure.Node;
import datastructure.Node.TypeOfNode;

/**
 * Utilities helping the definition of some metrics such as: 
 * 
 * <li> Elongation
 * <li> Compacity
 * <li> ...
 * 
 * @author C. Kurtz
 *
 */
public class ToolsMetric {

	/**
	 * Converti un point p dans une espace de coordonnées vers
	 * un point p2 dans une autre espace de coordonnées
	 * Paramètres :
	 * xx = par quoi est remplacé la valeur x du point(1,0) du 
	 * premier référentiel.
	 * @return
	 */
	public static void conversion(Point p, Point p2, int xx, int xy, int yx, int yy) {
		
		p2.x = p.x * xx + p.y * yx;
		p2.y = p.x * xy + p.y * yy;
	}

	/**
	 * 
	 * Close the holes of a region provided as a list of points
	 * 
	 * @param region
	 * @param widthImage
	 * @param heightImage
	 * @return a region without holes
	 */
	public static TreeSet<Integer> reconstructionTopologique(TreeSet<Integer> region,int widthImage,int heightImage){

		TreeSet<Integer> regionTemp=new TreeSet<Integer>();
		int x,y;
		
		for(int p:region){
			
			x= p % widthImage;
			y= p / widthImage;

			x++;
			y++;
			regionTemp.add(new Integer(y*(widthImage)+x));
			
		}

		TreeSet<Integer> res=new TreeSet<Integer>();

		/* Compute a bounding box */
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;

		for(int p:regionTemp) {	
			
			x= p % widthImage;
			y= p / widthImage;

			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		
		/* take one more pixel */
		minX--;
		minY--;
		maxX++;
		maxY++;

		//Croissance de region dans le fond
		Integer graine=new Integer(minY*(widthImage)+minX);
		TreeSet<Integer> ptsAtteints=new TreeSet<Integer>();
		ptsAtteints.add(graine);

		//Pile de croissance
		Stack<Integer> pileCroissance = new Stack<Integer>();
		pileCroissance.push(graine);

		Integer p;
		int xp, yp;
		while(!(pileCroissance.isEmpty())){
			p=pileCroissance.pop();
			x= p % widthImage;
			y= p / widthImage;

			/* For each neighbour having 8 connexities and included in the scope of the study */
			for (xp = x - 1; xp <= x + 1; ++xp) {
				for (yp = y - 1; yp <= y + 1; ++yp) {
					if(xp>=minX && yp>=minY && xp<=maxX && yp<=maxY){				
						if((xp!=x || yp!=y)){	
							if(!(ptsAtteints.contains(new Integer(yp*(widthImage)+xp )))  
									&& 
									!(regionTemp.contains(new Integer(yp*(widthImage)+xp ))))
							{
								pileCroissance.push(new Integer(yp*(widthImage)+xp ));
								ptsAtteints.add(new Integer(yp*(widthImage)+xp ));
							}		
						}
					}
				}
			}	
		}	

		/* Return the complement of the reached points */
		for (x = minX; x <=maxX; ++x) {
			for (y = minY; y <=maxY; ++y) {
				if(!ptsAtteints.contains(new Integer(new Integer(y*(widthImage)+x)))
						&& x>=0 && y>=0 && x<widthImage && y<heightImage){
					res.add(new Integer(new Integer((y-1)*(widthImage)+(x-1) )));
				}

			}
		}
		return res;

	}

	public static void computeBorderPixels(Node n, int widthImage, int heightImage) {
		
		if(n.type==TypeOfNode.LEAF){
			
			/* Finding the border pixels */
			
			// Boundig box
			int minX = n.boundingBox[0];
			int maxX = n.boundingBox[1];
			int minY = n.boundingBox[2];
			int maxY = n.boundingBox[3];


			int tailleMatriceX=maxX-minX+ 1;
			int tailleMatriceY=maxY-minY+ 1;
			boolean [][] matrice=new boolean[tailleMatriceX][tailleMatriceY];

			for(int i=0; i<matrice.length; i++)
				Arrays.fill(matrice[i], false);

			int x, y, xp, yp;
			for(Point p : n.getPixels()) {

				x = p.x - minX;
				y = p.y - minY;
				matrice[x][y]=true;
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
							n.borderPoints.add((x+minX) + (y+minY)*widthImage);

					}
				}

			}
			
		}else{
			
			
			n.borderPoints.addAll(n.leftNode.borderPoints);
			n.borderPoints.addAll(n.rightNode.borderPoints);
			
			for(Integer p:n.leftNode.borderPoints){
				if(n.rightNode.borderPoints.contains(p)){
					n.borderPoints.remove(p);
				}
			}
			
		}
		
		
	}
	
	
	public static void computeRegionBoundingBox(Node n,int widthImage,int heightImage) {
		
		if(n.type==TypeOfNode.LEAF){
			
			/* Compute a bounding box */
			int minX = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxY = Integer.MIN_VALUE;
	
			for(Point p: n.getPixels()) {	
				
				int x= p.x;
				int y= p.y;
	
				if(x < minX)
					minX = x;
				if(x > maxX)
					maxX = x;
				if(y < minY)
					minY = y;
				if(y > maxY)
					maxY = y;
			}
			
			n.boundingBox[0]=minX;
			n.boundingBox[1]=maxX;
			n.boundingBox[2]=minY;
			n.boundingBox[3]=maxY;
			
		}else{
			
			n.boundingBox[0] = Math.min(n.leftNode.boundingBox[0], n.rightNode.boundingBox[0]);
			n.boundingBox[1] = Math.max(n.leftNode.boundingBox[1], n.rightNode.boundingBox[1]);
			n.boundingBox[2] = Math.min(n.leftNode.boundingBox[2], n.rightNode.boundingBox[2]);
			n.boundingBox[3] = Math.max(n.leftNode.boundingBox[3], n.rightNode.boundingBox[3]);
		}
		
	}
	
	public static int computeRegionPerimeter(TreeSet<Integer> points,int widthImage,int heightImage) {

		int XDim = widthImage;
		int YDim = heightImage;


		int perimeter = 1;
		int xDim = XDim;
		int yDim = YDim;
		
		int x, y, xp, yp;
		boolean goNextPoint;

		for (int p : points) {
			
			x = p % xDim;
			y = p / xDim;

			goNextPoint = false;

			for (xp = x - 1; xp <= x + 1; ++xp) {
				for (yp = y - 1; yp <= y + 1; ++yp) {
					if (xp == x && yp == y)
						continue;

					if (xp < 0 || yp < 0 || yp >= yDim || xp >= xDim) {
						perimeter++;
						goNextPoint = true;
						break;
					}

					int adjacentPointIndex = yp * xDim + xp;

					if (!points.contains(adjacentPointIndex)) {
						perimeter++;
						goNextPoint = true;
						break;
					}
				}
				if (goNextPoint)
					break;
			}
		}				
		return perimeter;
	}
}
