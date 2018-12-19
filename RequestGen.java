
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

/**
 *
 * @author ningluo
 */
public class RequestGen  implements Runnable {
    
    private final String[] FileList;
    private final String ServerName;
    int ServerPort;
    String ServerIP;
    Socket socket; 
    private BufferedReader inFromServer;
    private DataOutputStream outToServer;
    private final long  threshold;
    private String currentFileName;

    public RequestGen(String[] files, String sn, int p, String sip, long T) throws IOException {
    	
        this.ServerName = sn;
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
//     System.out.println(count);
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
                socket = new Socket(ServerIP, ServerPort);
                inFromServer
                        = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outToServer
                        = new DataOutputStream(socket.getOutputStream());
                SendRequest(FileList[i], ServerName);
                currentFileName = FileList[i];
                
             //   System.out.println(FileList[i]);
                Debug.DEBUG(FileList[i]);
//                Debug.DEBUG(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                ReceiveResponse();
                Debug.DEBUG(currentFileName + " finished");
                
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

//    	while (!inFromServer.ready()) {
//    	}
		try {
		//	String requestMessageLine = inFromServer.readLine();
			//Debug.DEBUG(requestMessageLine);
			BufferedReader br = new BufferedReader(inFromServer);
			String line = br.readLine(); 
			Debug.DEBUG(line);

			

				while ((line = br.readLine()) != null) {
//				System.out.println(line);
				}//				Debug.DEBUG(requestMessageLine);
//				while (!inFromServer.ready()) {
//		    	}
//				try {
//					if (requestMessageLine.startsWith("Content-Length: ")) {
//						int contentLength = Integer.parseInt(requestMessageLine.substring(16));
//						char [] buffer = new char[contentLength];
//						inFromServer.read(buffer, 0, contentLength);
//						
//					}
//					requestMessageLine = inFromServer.readLine();

			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Debug.DEBUG(currentFileName);
//			Debug.DEBUG(requestMessageLine);
//			throw e;
		}
		inFromServer.close();
		socket.close();
		
        return;

    }

}

