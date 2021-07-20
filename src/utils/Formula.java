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

package utils;

/**
 * Contains useful formulas. 
 *
 */
public class Formula {

	/**
	 * 
	 * @param x position of a point (~pixel)
	 * @param y position of a point (~pixel)
	 * @param max number of rows or columns if(rows < columns)
	 * @return a single value embedding the x and the y
	 * 
	 * @see Formula#toX(int, int) compute the x from val
	 * @see Formula#toY(int, int) compute the y from val
	 */
	public static int toVal(int x, int y, int max) {

		return x * max + y; 
	}
	
	/**
	 * 
	 * @param val single value embedding the x and the y
	 * @param max number of rows or columns if(rows < columns); should be the same as the value used when computing 'val'
	 * @return the x position of a point (~pixel)
	 *
	 * @see Formula#toVal(int, int, int) compute a single value embedding the x and the y
	 * @see Formula#toY(int, int) compute the y from val
	 */
	public static int toX(int val, int max) {
		
		return val / max;
	}

	/**
	 * 
	 * @param val single value embedding the x and the y
	 * @param max number of rows or columns if(rows < columns); should be the same as the value used when computing 'val'
	 * @return the y position of a point (~pixel)
	 * 
	 * @see Formula#toX(int, int) compute the x from val
	 */
	public static int toY(int val, int max) {
		
		return val % max;
	}
}
