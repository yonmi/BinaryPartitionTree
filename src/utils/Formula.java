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
