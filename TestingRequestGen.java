
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

/**
 *
 * @author ningluo
 */
public class TestingRequestGen extends RequestGen implements Runnable {
    
    private final String[] FileList;
    private final String ServerName;
    int ServerPort;
    String ServerIP;
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private final long  threshold;

    public TestingRequestGen(String[] files, String sn, int p, String sip, long T) throws IOException {
    	super(files, sn, p, sip, T);
        ServerName = sn;
        this.FileList = files;
        this.ServerPort = p;
        this.ServerIP = sip;
        this.threshold = T;
    }

    @Override
    public void run(){
     long start = System.currentTimeMillis();
     while(System.currentTimeMillis() - start < threshold){
         PackageExchange();
     }
    }
//    
//    timer.schedule(new TimerTask(){
//16    public void run() {
//     PackageExchange();
//23    }
//24   }, 0, 1000);
//25 
//26   timer.schedule(new TimerTask(){
//27    public void run() {
//28     timer.cancel();
//29    }
//30   }, new Date(end));
//    
    
    
    
    public void PackageExchange() {
        try {
        	
            for (int i = 0; i < FileList.length; i++) {
            	Socket client = new Socket(ServerIP, ServerPort);
                inFromServer
                        = new BufferedReader(new InputStreamReader(client.getInputStream()));
                outToServer
                        = new DataOutputStream(client.getOutputStream());
                SendRequest(FileList[i], ServerName);
       //       System.out.println(FileList[i]);
                //Debug.DEBUG(FileList[i]);
              //Debug.DEBUG(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                ReceiveResponse();
                

            }
           
        
        } catch (Exception ex) {
            Logger.getLogger(TestingRequestGen.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void SendRequest(String filename, String serverName) throws Exception {
//    	if (filename.equals("test.cgi")) {
//    		 long start = System.currentTimeMillis();
//    		 
//    	     while(System.currentTimeMillis() - start < 4000) {
//    	     }
//    	     
//    	}
        outToServer.writeBytes("GET " + filename + " HTTP/1.0 \r\n");
        outToServer.writeBytes("Host: " + serverName + "\r\n");
        outToServer.writeBytes("If-Modified-Since: Sun, 28 Oct 2009 07:28:00 GMT"+"\r\n");
        outToServer.writeBytes("\r\n");
        
    } // end send request

    private void ReceiveResponse() throws Exception {
    	//System.out.println(inFromServer.ready());
    	String requestMessageLine = inFromServer.readLine();
    	System.out.println(requestMessageLine);
    	
    	
  		//Debug.DEBUG(requestMessageLine);
//  		if (requestMessageLine != null) {
//  			String[] response = requestMessageLine.split("\\s");
//
//  			if (response.length < 2) {
//  				//System.out.println(response[0]);
//
//  			} else if (!response[1].equals("200")) {
//  				//System.out.println(requestMessageLine);
//  				requestMessageLine = inFromServer.readLine();
//  			}

  			while (requestMessageLine != null) {
  			//	Debug.DEBUG(requestMessageLine);
  				requestMessageLine = inFromServer.readLine();
  			}
  			inFromServer.close();
  		
          return;
      }

}
