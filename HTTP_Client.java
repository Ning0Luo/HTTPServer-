///*
// *
// *  client for TCPClient from Kurose and Ross
// *
// *  * Usage: java TCPClient [server addr] [server port]
// */
//import java.net.*;
//
//public class HTTP_Client{
//
//    public static void main(String[] args) throws Exception {
//    	String ServerIP = "localhost";
//    	String ServerName = "test-server";
//    	int ServerPort = 10000;
//    	int threadsNum = 1;
//		for (int i = 0; i < args.length - 1; i++) {
//			if (args[i].equals("-server")) {
//				ServerIP = args[i+1];
//			}
//			else if (args[i].equals("-servname")) {
//				ServerName = args[i+1];
//			}
//			else if (args[i].equals("-port" )) {
//				ServerPort = Integer.parseInt(args[i+1]);
//			}
//			else if (args[i].equals("-parallel" )) {
//				threadsNum = Integer.parseInt(args[i+1]);
//			}
//		}
//    	String requestPatternFileName = "/home/nl437/network-ws/gen/request-patterns/requests.txt";       
////       // System.out.println("hello");        
////        rh.run();
////        connectSocket.close();
////        System.out.println("task 1 finished");
////        Socket connectSocket2 = new Socket("localhost", 10000);
////       // System.out.println("hello");        
////        RequestHandler rh2 = new RequestHandler(filelist, "Servername", connectSocket2);
////        rh2.run();
////        System.out.println("task 2 finished");
////        connectSocket2.close();
//        for (int i = 0; i < 1; i++) {
//            int ServerPort = 10000;
//            String ServerIP = "localhost";
//            RequestGen req = new RequestGen(filelist, "server1", ServerPort, ServerIP, 100);
//            Thread t = new Thread(req);
//            t.start();
//        }
//    } // end of main
//    
//    
//   
//
//} // end of class TCPClient
/*
 *
 *  client for TCPClient from Kurose and Ross
 *
 *  * Usage: java TCPClient [server addr] [server port]
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
public class HTTP_Client{
	
    public static void main(String[] args) throws Exception {
    	String ServerIP = "localhost";
    	String ServerName = "server1";
    	int ServerPort = 10000;
    	int threadsNum = 1;
    	String requestPatternFileName = util.REQ_PATH+"/requests2.txt";
    	long threshholdTime  = 100;

		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("-server")) {
				ServerIP = args[i+1];
			}
			else if (args[i].equals("-servname")) {
				ServerName = args[i+1];
			}
			else if (args[i].equals("-port" )) {
				ServerPort = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-parallel" )) {
				threadsNum = Integer.parseInt(args[i+1]);
			}
			else if (args[i].equals("-files")) {
				requestPatternFileName = util.REQ_PATH+"/"+ args[i+1];
			}
			
			else if (args[i].equals("-T")) {
			   threshholdTime = Integer.parseInt(args[i+1]);
			}
		}
		
    	File requestFile = new File(requestPatternFileName);
    	BufferedReader br = new BufferedReader(new FileReader(requestFile));
    	List<String> filelist = new ArrayList<String>();
        String line = br.readLine();
    	while (line!= null) {
    	//	System.out.println(line);
    		filelist.add("/"+line);
    		line = br.readLine();
		}
    	br.close();

        int tasksNum = filelist.size();
        int maxTasksPerThread = (int) Math.ceil((double)tasksNum / (double)threadsNum);
    	
//        String[] filelist = {"/file1", "/file2", "/file3", "test.cgi","hello","/load"};

       
//       // System.out.println("hello");        
//        rh.run();
//        connectSocket.close();
//        System.out.println("task 1 finished");
//        Socket connectSocket2 = new Socket("localhost", 10000);
//       // System.out.println("hello");        
//        RequestHandler rh2 = new RequestHandler(filelist, "Servername", connectSocket2);
//        rh2.run();
//        System.out.println("task 2 finished");
//        connectSocket2.close();
//        int tasksIndex = 0;
//        int remainingTasksNum = tasksNum;
//        for (int i = 0; i < threadsNum; i++) {
//            int tasksThisThread;
//            if (maxTasksPerThread < remainingTasksNum) {
//            	tasksThisThread = maxTasksPerThread;
//            	remainingTasksNum -= maxTasksPerThread;
//            }
//            else {
//            	tasksThisThread = remainingTasksNum;
//            	remainingTasksNum = 0;
//            }
            RequestGen req = new RequestGen(filelist.toArray(new String[filelist.size()]),
            		ServerName, ServerPort, ServerIP, threshholdTime);
            Thread t = new Thread(req);
            t.start();
        
    } // end of main
} // end of class TCPClient
























