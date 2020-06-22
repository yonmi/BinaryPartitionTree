package utils;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Object regrouping morphological operations such as:
 * 
 * <li> Dilation
 * <li> Erosion
 * <li> Topological reconstruction
 * 
 * Various structural elements are used:
 * 
 * <li> square
 * <li> cross
 * <li> cross infinity
 * 
 * @author C. Kurtz
 *
 */
public class Morphological {
	
	int k =0;
	int width = 0;
	int height = 0;
	
	public Morphological(int widthImage, int heightImage, int kFois){	
		
		width=widthImage;
		height=heightImage;
		k=kFois;
	}
	
	public TreeSet<Integer> dilatationCross(TreeSet<Integer> region){
		
		TreeSet<Integer> res=new TreeSet<Integer>();
	
		int x, y;
		for(int pt:region){
			x=pt % width;
			y=pt / height;
			
			res.add(pt);
	    	
	        if (x>0) 
	        	res.add(new Integer(y*(width)+(x-1)));
	       
	        if (y>0) 
	        	res.add(new Integer((y-1)*(width)+(x)));  
	                    
	        if (x+1<width)
	        	res.add(new Integer((y)*(width)+(x+1)));
	       
	        if (y+1<height) 
	        	res.add(new Integer((y+1)*(width)+(x)));
	                  
	    }
	    return res;
	}

	public TreeSet<Integer> dilatationCrossInfinity(TreeSet<Integer> region){
		
		TreeSet<Integer> res=new TreeSet<Integer>();
	
		int x, y;
		for(int pt:region){
			x=pt % width;
			y=pt / height;
			
			res.add(pt);
	    	
			res.add(new Integer(y*(width)+(x-1)));
			res.add(new Integer((y-1)*(width)+(x)));  
			res.add(new Integer((y)*(width)+(x+1)));
			res.add(new Integer((y+1)*(width)+(x)));             
	    }
	    return res;
	}

	public TreeSet<Integer> dilatationSquare(TreeSet<Integer> region){
			
		TreeSet<Integer> res=new TreeSet<Integer>();
		
		int x, y;
		
		for(int pt:region){
			x=pt % width;
			y=pt / width;
			
			res.add(pt);
        	
            if (x>0) 
            	res.add(new Integer(y*(width)+(x-1)));
           
            if (y>0) 
            	res.add(new Integer((y-1)*(width)+(x)));
           
            if (x>0 && y>0) 
            	res.add(new Integer((y-1)*(width)+(x-1)));
            
            if (x+1<width && y>0) 
            	res.add(new Integer((y-1)*(width)+(x+1)));
            
            if (y+1<height && x>0)
            	res.add(new Integer((y+1)*(width)+(x-1)));
            
            if (x+1<width)
            	res.add(new Integer((y)*(width)+(x+1)));
           
            if (y+1<height) 
            	res.add(new Integer((y+1)*(width)+(x)));
           
            if (x+1<width && y+1<height)
            	res.add(new Integer((y+1)*(width)+(x+1)));
                
	    }
	    return res;
	}
	
	public TreeSet<Integer> erosionCross(TreeSet<Integer> region){
		TreeSet<Integer> res=new TreeSet<Integer>();
	    
		int x, y;
		for(Integer pt:region){
			
			x=pt.intValue() % width;
			y=pt.intValue() / width;
			
			boolean res1 =false,res2 =false,res3 =false,res4 =false;
			
	        if (x>0) 
	        	if(region.contains(new Integer(y*(width)+(x-1)))) res1 =true;
	        
	        if (y>0)
	        	if(region.contains(new Integer((y-1)*(width)+(x)))) res2 =true;
	                 
	        if (x+1<width) 
	        	if(region.contains(new Integer((y)*(width)+(x+1)))) res3 =true;
	        
	        if (y+1<height)if(region.contains(new Integer((y+1)*(width)+(x)))) res4 =true;
	        
	        
	        if(res1 && res2 && res3 && res4)
	        	res.add(pt);
	        
	    }
	    return res;
	}

	public TreeSet<Integer> erosionCrossInfinity(TreeSet<Integer> region){
		TreeSet<Integer> res=new TreeSet<Integer>();
	    
		int x, y;
		for(Integer pt:region){
			
			x=pt.intValue() % width;
			y=pt.intValue() / width;
			
			boolean res1 =false,res2 =false,res3 =false,res4 =false;
			
	        if(region.contains(new Integer(y*(width)+(x-1)))) res1 =true;
	        if(region.contains(new Integer((y-1)*(width)+(x)))) res2 =true;
	        if(region.contains(new Integer((y)*(width)+(x+1)))) res3 =true;
	        if(region.contains(new Integer((y+1)*(width)+(x)))) res4 =true;
	         
	        if(res1 && res2 && res3 && res4)
	        	res.add(pt);
	        
	    }
	    return res;
	}

	public TreeSet<Integer> erosionSquare(TreeSet<Integer> region){
		TreeSet<Integer> res=new TreeSet<Integer>();
	    
		int x, y;
		for(Integer pt:region){
			
			x=pt.intValue() % width;
			y=pt.intValue() / width;
			
			boolean res1 =false,res2 =false,res3 =false,res4 =false,res5 =false,res6 =false,res7 =false,res8 =false;
			
	        if (x>0) 
	        	if(region.contains(new Integer(y*(width)+(x-1)))) res1 =true;
	        
	        if (y>0)
	        	if(region.contains(new Integer((y-1)*(width)+(x)))) res2 =true;
	       
	        if (x>0 && y>0) 
	        	if(region.contains(new Integer((y-1)*(width)+(x-1)))) res3 =true;
	        
	        if (x+1<width && y>0) 
	        	if(region.contains(new Integer((y-1)*(width)+(x+1)))) res4 =true;
	        
	        if (y+1<height && x>0)
	        	if(region.contains(new Integer((y+1)*(width)+(x-1)))) res5 =true;
	        
	        if (x+1<width) 
	        	if(region.contains(new Integer((y)*(width)+(x+1)))) res6 =true;
	        
	        if (y+1<height)if(region.contains(new Integer((y+1)*(width)+(x)))) res7 =true;
	        
	        if (x+1<width && y+1<height) if(region.contains(new Integer((y+1)*(width)+(x+1)))) res8 =true;
	        
	        
	        if(res1 && res2 && res3 && res4 && res5 && res6 && res7 && res8)
	        	res.add(pt);
	        
	    }
	    return res;
	}

	public int getRegionPerimeter(TreeSet<Integer> points) {
		
		int perimeter = 1;
		int xDim = width;
		int xp, yp;
		
		int x, y;
		for (int p : points) {
			 x = p % xDim;
	         y = p / xDim;
			
			boolean goNextPoint = false;
			
			for (xp = x - 1; xp <= x + 1; ++xp) {
				for (yp = y - 1; yp <= y + 1; ++yp) {
					if (xp == x && yp == y)
						continue;
					
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

	public TreeSet<Integer> KdilatationCross(TreeSet<Integer> region){
		
		TreeSet<Integer> res=region;
		for(int i=0;i<k;++i){
			res=dilatationCross(res);
		}
		return res;
	}
	public TreeSet<Integer> KdilatationCrossInfinity(TreeSet<Integer> region){
		
		TreeSet<Integer> res=region;
		for(int i=0;i<k;++i){
			res=dilatationCrossInfinity(res);
		}
		return res;
	}
	
	
	public TreeSet<Integer> KdilatationSquare(TreeSet<Integer> region){
		
		TreeSet<Integer> res=region;
		for(int i=0;i<k;++i){
			res=dilatationSquare(res);
		}
		return res;
	}

	public TreeSet<Integer> KerosionCross(TreeSet<Integer> region){
		
		TreeSet<Integer> res=region;		
		for(int i=0;i<k;++i){
			res=erosionCross(res);
		}
		return res;
	}

	public TreeSet<Integer> KerosionCrossInfinity(TreeSet<Integer> region){
		
		TreeSet<Integer> res=region;		
		for(int i=0;i<k;++i){
			res=erosionCrossInfinity(res);
		}
		return res;
	}
	
	public TreeSet<Integer> KerosionSquare(TreeSet<Integer> region){
		
		TreeSet<Integer> res=region;		
		for(int i=0;i<k;++i){
			res=erosionSquare(res);
		}
		return res;
	}

	public double morphologicalSmoothness(ArrayList<Point> region){
		double smoothness=0.0;
		
		int infinity=1000;
		int new_width   =  width + 2 * infinity;
		int new_height  =  height + 2 * infinity;
		
		TreeSet<Integer> region_infinity=new TreeSet<Integer>();
		int x, y;
		for(Point p:region){
			
			x=p.x;
			y=p.y;
			
			x=x+infinity;
			y=y+infinity;
				
			region_infinity.add(new Integer((y*new_width)+(x) ));
		}
		
		width=new_width;
		height=new_height;
		
	
		TreeSet<Integer> region_infinity_sans_trou=reconstructionTopologiqueInfinity(region_infinity);
		
		double perimetre=getRegionPerimeter(region_infinity_sans_trou);
		
		TreeSet<Integer> res_infinity= reconstructionTopologiqueInfinity(
				KerosionCrossInfinity(reconstructionTopologiqueInfinity(
									  KdilatationCrossInfinity((region_infinity)))));
		
		double perimetre_infinity=getRegionPerimeter(res_infinity);
		
		smoothness=perimetre_infinity/perimetre;
		return smoothness;
	}

	public TreeSet<Integer> operationSquare(TreeSet<Integer> region) {
		return reconstructionTopologique(
				KerosionSquare(
						reconstructionTopologique(
								KdilatationSquare((region)))));
	}
	
	public TreeSet<Integer> operationSquareWithoutTopologicalreconstruction(TreeSet<Integer> region) {
		return KerosionSquare(region);
	}
		
	
	public TreeSet<Integer> operationCross(TreeSet<Integer> region) {
		return reconstructionTopologique(
				KerosionCross(
						reconstructionTopologique(
								KdilatationCross((region)))));
	}
	
	
	public TreeSet<Integer> operationCrossInfinity(TreeSet<Integer> region) {
		
		int infinity=1000;
		
		int old_width   =  width;
		int old_height  =  height;
		
		int new_width   =  width + 2 * infinity;
		int new_height  =  height + 2 * infinity;
		
		int x, y;
		TreeSet<Integer> region_infinity=new TreeSet<Integer>();
		for(Integer p:region){
			
			x=p % width;
			y=p / width;
			
			x=x+infinity;
			y=y+infinity;
				
			region_infinity.add(new Integer((y*new_width)+(x) ));
		}
		
		width=new_width;
		height=new_height;
		
		TreeSet<Integer> res_infinity= reconstructionTopologiqueInfinity(
				KerosionCrossInfinity(
						reconstructionTopologiqueInfinity(
								KdilatationCrossInfinity((region_infinity)))));
		
		
		TreeSet<Integer> res =new TreeSet<Integer>();
		
		for(Integer p:res_infinity){
			
			x=p.intValue() % width;
			y=p.intValue() / width;
			
			x=x-infinity;
			y=y-infinity;
					
			if(x>=0 && x<old_width && y>=0 && y<old_height){
				res.add(new Integer((y)*(old_width)+(x) ));
				
			}
		}
		
		return res;
		
	}
	
	public TreeSet<Integer> reconstructionTopologique(TreeSet<Integer> region){
		
		TreeSet<Integer> regionTemp=new TreeSet<Integer>();
		int x, y;
		for(int p:region){
			x= p % width;
			y= p / width;
			
			x++;
			y++;
			regionTemp.add(new Integer(y*(width)+x));
		}
		
		
		TreeSet<Integer> res=new TreeSet<Integer>();
	    
		//Calcul boite englobante
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
	
		for(int p:regionTemp) {	
			x= p % width;
			y= p / width;
			
			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		//On prend un pixel de plus
		minX--;
		minY--;
		maxX++;
		maxY++;
		
		//Croissance de region dans le fond
		Integer graine=new Integer(minY*(width)+minX);
		TreeSet<Integer> ptsAtteints=new TreeSet<Integer>();
		ptsAtteints.add(graine);
		
		//Pile de croissance
		Stack<Integer> pileCroissance = new Stack<Integer>();
		pileCroissance.push(graine);
		
		int xp, yp;
		while(!(pileCroissance.isEmpty())){
			Integer p=pileCroissance.pop();
			x= p % width;
			y= p / width;
			
			//Pour chaque voisin en 8 connexité et ne dépassant pas la fenetre
			for (xp = x - 1; xp <= x + 1; ++xp) {
				for (yp = y - 1; yp <= y + 1; ++yp) {
					if(xp>=minX && yp>=minY && xp<=maxX && yp<=maxY){				
						if((xp!=x || yp!=y)){	
							if(!(ptsAtteints.contains(new Integer(yp*(width)+xp )))  
								&& 
								!(regionTemp.contains(new Integer(yp*(width)+xp ))))
							{
								pileCroissance.push(new Integer(yp*(width)+xp ));
								ptsAtteints.add(new Integer(yp*(width)+xp ));
							}		
						}
					}
				}
			}	
		}	
		
		//On rend le complementaire des pts atteints
		for (x = minX; x <=maxX; ++x) {
			for (y = minY; y <=maxY; ++y) {
				if(!ptsAtteints.contains(new Integer(y*(width)+x))
						&& x>=0 && y>=0 && x<width && y<height){
					res.add(new Integer((y-1)*(width)+(x-1) ));
				}
				
			}
		}
		return res;
		
	}

	public TreeSet<Integer> reconstructionTopologiqueInfinity(TreeSet<Integer> region){
		
		TreeSet<Integer> regionTemp=new TreeSet<Integer>();
		int x, y;
		int xp, yp;
		
		for(int p:region){
			x= p % width;
			y= p / width;
			
			x++;
			y++;
			regionTemp.add(new Integer(y*(width)+x));
		}
		
		
		TreeSet<Integer> res=new TreeSet<Integer>();
	    
		//Calcul boite englobante
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for(int p:regionTemp) {	
			x= p % width;
			y= p / width;
			
			if(x < minX)
				minX = x;
			if(x > maxX)
				maxX = x;
			if(y < minY)
				minY = y;
			if(y > maxY)
				maxY = y;
		}
		//On prend un pixel de plus
		minX--;
		minY--;
		maxX++;
		maxY++;
		
		//Croissance de region dans le fond
		Integer graine=new Integer(minY*(width)+minX);
		TreeSet<Integer> ptsAtteints=new TreeSet<Integer>();
		ptsAtteints.add(graine);
		
		//Pile de croissance
		Stack<Integer> pileCroissance = new Stack<Integer>();
		pileCroissance.push(graine);
			
		while(!(pileCroissance.isEmpty())){
			Integer p=pileCroissance.pop();
			x= p % width;
			y= p / width;
			
			//Pour chaque voisin en 8 connexité et ne dépassant pas la fenetre
			for (xp = x - 1; xp <= x + 1; ++xp) {
				for (yp = y - 1; yp <= y + 1; ++yp) {
					if(xp>=minX && yp>=minY && xp<=maxX && yp<=maxY){				
						if((xp!=x || yp!=y)){	
							if(!(ptsAtteints.contains(new Integer(yp*(width)+xp )))  
								&& 
								!(regionTemp.contains(new Integer(yp*(width)+xp ))))
							{
								pileCroissance.push(new Integer(yp*(width)+xp ));
								ptsAtteints.add(new Integer(yp*(width)+xp ));
							}		
						}
					}
				}
			}	
		}	
		
		//On rend le complementaire des pts atteints
		for (x = minX; x <=maxX; ++x) {
			for (y = minY; y <=maxY; ++y) {
				if(!ptsAtteints.contains(new Integer(y*(width)+x))){
					res.add(new Integer((y-1)*(width)+(x-1) ));
				}			
			}
		}
		return res;
	}

}
