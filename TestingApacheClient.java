
/*
 *
 *  client for TCPClient from Kurose and Ross
 *
 *  * Usage: java TCPClient [server addr] [server port]
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

public class TestingApacheClient {

	public static void main(String[] args) throws Exception {
		String ServerIP = "localhost";
		String ServerName = "test-server";
		int ServerPort = 10000;
		int threadsNum = 1;
		long threshold = 100;
		for (int i = 0; i < args.length - 1; i++) {
			if (args[i].equals("-server")) {
				ServerIP = args[i + 1];
			} else if (args[i].equals("-servname")) {
				ServerName = args[i + 1];
			} else if (args[i].equals("-port")) {
				ServerPort = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("-parallel")) {
				threadsNum = Integer.parseInt(args[i + 1]);
			} else if (args[i].equals("-T")) {
				threadsNum = Integer.parseInt(args[i + 1]);
			}

		}
		String requestPatternFileName = "/home/nl437/network-ws/gen/request-patterns/requests.txt";
		File requestFile = new File(requestPatternFileName);
		BufferedReader br = new BufferedReader(new FileReader(requestFile));
		List<String> filelist = new ArrayList<String>();
		String line = br.readLine();
		while (line != null) {
			filelist.add("/" + line);
			line = br.readLine();
		}
		br.close();

		int tasksNum = filelist.size();
		int maxTasksPerThread = (int) Math.ceil((double) tasksNum / (double) threadsNum);

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
		int tasksIndex = 0;
		int remainingTasksNum = tasksNum;
		for (int i = 0; i < threadsNum; i++) {
			int tasksThisThread;
			if (maxTasksPerThread < remainingTasksNum) {
				tasksThisThread = maxTasksPerThread;
				remainingTasksNum -= maxTasksPerThread;
			} else {
				tasksThisThread = remainingTasksNum;
				remainingTasksNum = 0;
			}
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					URL url;
					InputStream is = null;
					BufferedReader br;
					String line;
					String[] requestList = filelist.toArray(new String[filelist.size()]);
					long start = System.currentTimeMillis();
					int count =0;
					while (System.currentTimeMillis() - start < threshold) {
				//		count++; 
						for (int i = 0; i < requestList.length; i++) {
							try {
								url = new URL("http://zoo.cs.yale.edu/classes/cs433/web/www-root/html-small/"
										+ requestList[i]);
								is = url.openStream(); // throws an IOException
								br = new BufferedReader(new InputStreamReader(is));

								while ((line = br.readLine()) != null) {
//								System.out.println(line);
								}
							} catch (MalformedURLException mue) {
								mue.printStackTrace();
							} catch (IOException ioe) {
								ioe.printStackTrace();
							} finally {
								try {
									if (is != null)
										is.close();
								} catch (IOException ioe) {
									// nothing to see here
								}
							}
						}
					}
					//System.out.println(count);
				}
			});
			t.start();
		}
	} // end of main
} // end of class TCPClient
