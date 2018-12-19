
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

import serverCache.ServerCacheSingleton;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ningluo
 */

    /**
 ** Yale CS433/533 Demo Basic Web Server
 **/


class WebServer_b{

    public static int serverPort = 10000;    
    //public static String WWW_ROOT = "/home/httpd/html/zoo/classes/cs433/";
    public static String WWW_ROOT = util.WS_ROOT;
    public static int PoolSize = 10;

    public static void main(String args[]) throws Exception  {
	
	// see if we do not use default server port
//	if (args.length >= 1)
//	    serverPort = Integer.parseInt(args[0]);
//
//	// see if we want a different root
//	if (args.length >= 2)
//	    WWW_ROOT = args[1];
        
        properties prop = new properties();
        Queue<Socket> dispatcher = new LinkedList<>(); 
        
        
        if (args.length > 1){
            prop.config(args[1]);
            serverPort = prop.getServerPort();
        }
        
	// create server socket
	ServerSocket listenSocket = new ServerSocket(serverPort);
	System.out.println("server listening at: " + listenSocket);
	System.out.println("server www root: " + WWW_ROOT);
	ServerCacheSingleton.init(4096);
	PoolSize = prop.getpoolSize();
	System.out.println(PoolSize);
    ServerCacheSingleton.init(prop.getCacheSize());


        Service_b worker = new Service_b(dispatcher,prop,WWW_ROOT);                   //??????????????????????????
        for (int i =0; i<PoolSize; i++){
            Thread t = new Thread(worker);
            t.start();
        }
        
        while (true) {
            Socket con = listenSocket.accept();
            synchronized(dispatcher)
            {
                dispatcher.add(con);
            }
        }
        
//	while (true) {
//
//	    try {
//		    // take a ready connection from the accepted queue
//		    Socket connectionSocket = listenSocket.accept();
//		    System.out.println("\nReceive request from " + connectionSocket);
//		    // process a request
//		    WebRequestHandler wrh = 
//		        new WebRequestHandler(connectionSocket, WWW_ROOT,prop.getNameMap());
//		    Thread t = new Thread(wrh);
//                    t.start();                 
//	    } catch (Exception e)
//		{
//                    System.out.println("server error;");
//		}
//	} 
	
    } // end of main

} // end of class WebServer

    

