public class Debug {

    private static boolean DEBUG = true;
    public static void DEBUG(Object s) {
	if (DEBUG)
	    System.out.println(s);
    }
}
