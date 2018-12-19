import java.lang.management.ClassLoadingMXBean;
import java.net.URL;
import java.net.URLClassLoader;

import org.omg.CORBA.PRIVATE_MEMBER;

public class MonitorFactory {
	private  static final String CLASS_FOLDER = util.MONITOR_PATH;
	
	public static Class getClassFromFile(String MonitorName) throws Exception {
    	URLClassLoader loader = new URLClassLoader(new URL[] {
	    new URL("file://"+ CLASS_FOLDER)
	});
	return loader.loadClass(MonitorName);
}
//	public static Object GetInstance (String MonitorName) throws Exception {
//		Class myClass = getClassFromFile(MonitorName);
//		return myClass.newInstance();
//	}
//	
}

//private static final String CLASS_FOLDER =
//"/Users/juneyoungoh/Downloads/";
//
//private static Class getClassFromFile(String fullClassName) throws Exception {
//URLClassLoader loader = new URLClassLoader(new URL[] {
//    new URL("file://" + CLASS_FOLDER)
//});
//return loader.loadClass(fullClassName);
//}
//
//public static void main( String[] args ) throws Exception {
//System.out.println((getClassFromFile("ClassFile"));
//}