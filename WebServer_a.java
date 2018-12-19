/* 
 *
 * CS433/533 Demo
 */
import java.io.*;
import java.util.*;

import serverCache.ServerCacheSingleton;

import java.net.*;

public class WebServer_a {

    private static ServerSocket welcomeSocket;
    public static int THREAD_COUNT = 10;
    private Thread[] threads;
    private static properties prop;
    public static int serverPort = 10000;
    public static String WWW_ROOT = util.WS_ROOT;

    /* Constructor: starting all threads at once */
    public WebServer_a(int serverPort) {

        try {
            // create server socket
            welcomeSocket = new ServerSocket(serverPort);
          //  System.out.println("server listening at: " + welcomeSocket);
        	ServerCacheSingleton.init(4096);

            // create thread pool
            threads = new Thread[THREAD_COUNT];

            Service_a service = new Service_a(welcomeSocket, prop, WWW_ROOT);

            // start all threads
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(service);
                threads[i].start();
            }
        } catch (Exception e) {
            //System.out.println("Server construction failed.");
        } // end of catch

    } // end of Server

    public static void main(String[] args) throws IOException {

        properties prop = new properties();
     //   System.out.println(THREAD_COUNT);
        if (args.length > 1) {
            prop.config(args[1]);
           // System.out.println(THREAD_COUNT);
            serverPort = prop.getServerPort();
        }
        welcomeSocket = new ServerSocket(serverPort);
       // System.out.println(THREAD_COUNT);
        Service_a service = new Service_a(welcomeSocket, prop, WWW_ROOT);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
               Thread thread = new Thread(service);
                thread.start();
            }
    }
    

//    public void run() {
//
//        try {
//            for (int i = 0; i < threads.length; i++) {
//                threads[i].join();
//            }
//            //System.out.println("All threads finished. Exit");
//        } catch (Exception e) {
//          //  System.out.println("Join errors");
//        } // end of catch
//
//    }
}
