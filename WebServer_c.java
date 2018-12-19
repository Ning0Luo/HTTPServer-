
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
public class WebServer_c {
    
    private static ServerSocket welcomeSocket  =null;
    public final static int THREAD_COUNT = 10;
    private static properties prop;
    public static int serverPort = 10000;
    public static String WWW_ROOT = util.WS_ROOT;
    
    public static void main(String[] args) throws IOException, Exception {
        prop = new properties();
        if (args.length > 1) {
            prop.config(args[1]);
            serverPort = prop.getServerPort();
        }

        
        try {
            welcomeSocket= new ServerSocket(serverPort);
            //System.out.println("Time server listens at port: " + serverPort);
            ServerCacheSingleton.init(prop.getCacheSize());


            // Create Java Executor Pool
            TimeServerHandlerExecutePool myExecutor
                    = new TimeServerHandlerExecutePool(prop.getpoolSize(),10000);

            Socket socket = null;
            while (true) {
                socket =  welcomeSocket.accept();
                myExecutor.execute(new WebRequestHandler(socket,prop.getNameMap()));
            } // end of while

        } finally {
            if ( welcomeSocket != null) {
              //  System.out.println("The time server closes.");
                welcomeSocket.close();
                welcomeSocket = null;
            }
        } // end of finally
    } // end of main
}


