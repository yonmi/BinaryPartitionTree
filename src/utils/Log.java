package utils;

/**
 * Prints information according to a context.
 * 
 * <p>
 * It helps the developer as landmarks in the code.
 *
 */
public class Log {
	
	/**
	 * Activates or not the logging.
	 */
	public static boolean show = false;
	
	/**
	 * Shows on the console the logging message. 
	 * 
	 * @param context defining the corresponding part of the code
	 * @param message defining success, errors or situations
	 */
	public static void println(String context, String message) {
		
		if(Log.show)
			System.out.println("["+ context +"] "+ message);
		
	}
}
