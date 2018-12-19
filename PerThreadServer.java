import java.io.*;
import java.net.*;
import java.util.*;

class PerThreadServer{

    public static int serverPort = 6789;    
    //public static String WWW_ROOT = "/home/httpd/html/zoo/classes/cs433/";
    public static String WWW_ROOT = util.WS_ROOT;
    

    public static void main(String args[]) throws Exception  {
	
	// see if we do not use default server port
//	if (args.length >= 1)
//	    serverPort = Integer.parseInt(args[0]);
//
//	// see if we want a different root
//	if (args.length >= 2)
//	    WWW_ROOT = args[1];
        
        properties prop = new properties();
        
        if (args.length >= 1){
            prop.config(util.CONFIG_PATH+"/"+args[0]);
            serverPort = prop.getServerPort();
        }
        
	// create server socket
	ServerSocket listenSocket = new ServerSocket(serverPort);
	System.out.println("server listening at: " + listenSocket);
	System.out.println("server www root: " + WWW_ROOT);

	while (true) {

	    try {
		    // take a ready connection from the accepted queue
                    
		    Socket connectionSocket = listenSocket.accept();
		    WebRequestHandler wrh = 
			        new WebRequestHandler(connectionSocket,prop.getNameMap());
		    System.out.println("\nReceive request from " + connectionSocket);
                    Thread t = new Thread(wrh);
                    t.start();
	    } catch (Exception e)
		{
		}
	} // end of while (true)
	
    } // end of main

} // end of class WebServer
