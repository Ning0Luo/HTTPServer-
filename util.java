import java.sql.SQLNonTransientConnectionException;

public class util {
	//String executionPath = System.getProperty("user.dir");
	public final static String CONFIG_PATH = System.getProperty("user.dir"); // your config file should be set under this dictory
	public final static String WS_ROOT = ""; // your www_ROOT. Please construct a folder for every server. You also need insert the <ServerDocumentRoot, ServerName> in configure file
	public final static String  MONITOR_PATH = System.getProperty("user.dir"); // Where your monitor file is.Please set the monitor.java under this path so the program can find where the .java file is/  
	public final static String REQ_PATH = " "; //where you request file is. 
}
