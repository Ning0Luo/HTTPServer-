import java.awt.Checkbox;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JScrollBar;

import org.w3c.dom.NamedNodeMap;
import serverCache.*;

class WebRequestHandler implements Runnable {

    static boolean _DEBUG = true;
    static int reqCount = 0;
    //String WWW_ROOT;
    Socket connSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    String ServerName;
    String urlName;
    String fileName;
    File fileInfo;
    int contentLength;
    boolean lastModified;
    String ModifiedSince;
    String[] cgiArguments;
    HashMap<String, String> NameMap;

    SimpleDateFormat sdf = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

    ProcessBuilder pb = new ProcessBuilder();

    public WebRequestHandler(Socket connectionSocket,
           HashMap hm) throws Exception {
        reqCount++;
        //this.WWW_ROOT = WWW_ROOT;
        NameMap =  hm;
        this.connSocket = connectionSocket;
        inFromClient
                = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
        outToClient
                = new DataOutputStream(connSocket.getOutputStream());
        
    }

    @Override
    public void run(){
        try {
            processRequest();
        } catch (ParseException ex) {
            Logger.getLogger(WebRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void processRequest() throws ParseException {
        try {
            mapURL2File();
            if (fileInfo != null) {
                if (lastModified) {
                    String lm = sdf.format(fileInfo.lastModified());
                    //outToClient.writeBytes("Last-Modified: "+lm+"\r\n");
                    Date time1 = sdf.parse(lm);
                    Date time2 = sdf.parse(ModifiedSince);
                    if (time1.after(time2)) {
                        outputResponseHeader();
                        outputResponseBody();
                    } else outputError(304, "Not modified since " + ModifiedSince);
                    		
                    
                } else {
                    outputResponseHeader();
                    outputResponseBody();
                }
            } // dod not handle error}
            connSocket.close();
//            outToClient.close();
        } catch (Exception e) {
            outputError(400, "Server error");
        }

    } // end of processARequest

    private void mapURL2File() throws Exception {

    	cgiArguments = GetRequest();
        if (cgiArguments == null) {
            return;   // NON FILE NAME SINCE FILE NAME IS A PART OF CGI
        }

        for (int i = 0; i < cgiArguments.length; i++) {
           // DEBUG(cgiArguments[i]);
        }
       
        String ServerRootDic = null;
        String Hostline = inFromClient.readLine();
        String[] header = Hostline.split(" ");
        if(header.length >1 && header[1] != null)
        ServerRootDic = NameMap.get(header[1]);
        if (_DEBUG) {
            String line = inFromClient.readLine();
            while (!line.equals("")) {
         //       DEBUG(line);
                lastModified = ifModifiedSince(line);
                if (lastModified) {
                    ModifiedSince = ModifiedSince(line);
                }
                line = inFromClient.readLine();
            }
        }

        fileName = ServerRootDic + "/" + urlName;
       // DEBUG("Map to File name: " + fileName);
        fileInfo = new File(fileName);
        if (!fileInfo.isFile()) {
            outputError(404, "Not Found");
            fileInfo = null;
        }

    }
    // end mapURL2file
    
    

    private void outputResponseHeader() throws Exception {
        outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
        outToClient.writeBytes("Set-Cookie: MyCool433Seq12345\r\n");

        if (urlName.endsWith(".jpg")) {
            outToClient.writeBytes("Content-Type: image/jpeg\r\n");
        } else if (urlName.endsWith(".gif")) {
            outToClient.writeBytes("Content-Type: image/gif\r\n");
        } else if (urlName.endsWith(".html") || urlName.endsWith(".htm")) {
            outToClient.writeBytes("Content-Type: text/html\r\n");
        } else {
            outToClient.writeBytes("Content-Type: text/plain\r\n");
        }
        if (lastModified) {
            String lm = sdf.format(fileInfo.lastModified());
            outToClient.writeBytes("Last-Modified: " + lm + "\r\n");
        }
    }

    private String[] GetRequest() throws IOException {
        String[] req = null;
        String requestMessageLine = inFromClient.readLine();
      //  DEBUG("Request " + reqCount + ": " + requestMessageLine);
        // process the request
        String[] request = requestMessageLine.split("\\s");

        if (request.length < 2 || !request[0].equals("GET")) {
            outputError(500, "Bad request");
            return req;
        }

        // parse URL to retrieve file name
        String url = request[1];
        req = url.split("[?&]");
        urlName = req[0];
        if (urlName.startsWith("/") == true) {
            urlName = urlName.substring(1);
        }
        return req;
	}

	private byte[] GetCgiOutput() {
		//DEBUG("read from cgi");
		byte[] fileBytes = new byte[8 * 1024];
		List<String> commands = new ArrayList<String>();
		commands.add("python3");
		commands.add(fileName);    //???????????????

//		commands.add("perl");
//		commands.add("perl.cgi");
		for (String arg : cgiArguments) {
                        //System.out.println(arg +" ....lalala");
			commands.add(arg);
		}
		pb = new ProcessBuilder(commands);
		Map<String, String> env = pb.environment();

		// clear environment
		env.entrySet().removeIf(entry -> ! entry.getKey().equals("PATH"));
		// add environment variables
		env.put("AUTH_TYPE", "");
		env.put("CONTENT_TYPE", "GET");
		env.put("CONTENT_LENGTH", "");
		env.put("GATEWAY_INTERFACE", "CGI/1.0");
		env.put("PATH_INFO", "");
		env.put("PATH_TRANSLATED", "");
		env.put("QUERY_STRING", ""); // TODO
		env.put("REMOTE_ADDR", ""); // TODO client IP
		env.put("REMOTE_HOST", "");
		env.put("REMOTE_IDENT", "");
		env.put("REMOTE_USER", "");
		env.put("SCRIPT_NAME", fileName);
		env.put("SERVER_NAME", "");
		env.put("SERVER_PORT", ""); // TODO
		env.put("SERVER_PROTOCOL", "HTTP/1.0");
		env.put("SERVER_SOFTWARE", "Yale CS433/533 Demo Basic Web Server");
		
		int size = 0;
		pb.directory(fileInfo.getParentFile());
		try {
			Process p = pb.start();
			InputStream fileStream = p.getInputStream();
			size = fileStream.read(fileBytes);
		} catch (IOException ex) {
		}
		return Arrays.copyOfRange(fileBytes, 0, size);
	}

    private byte[] GetFile( ) throws FileNotFoundException, IOException {
        // check if cache contains the file~~~~~~~~~~~~~~~~~~~~~~~~
    	SynchronizedCache cache = ServerCacheSingleton.getInstance();
    	byte[] content = cache.get(fileName);
        if (content != null) return content;

        if (!fileInfo.canExecute()) {
            int numOfBytes = (int) fileInfo.length();
            content = new byte[numOfBytes];

////            // send file content
//            if (cache.containsKey(fileName)) {
//                fileInBytes = (byte[]) cache.get(fileName);
//                DEBUG("read from hashmap");
//            } else {
                FileInputStream fileStream = new FileInputStream(fileName);
                fileStream.read(content);
                cache.tryPut(fileName, content);
                fileStream.close();
               // cache.put(fileName, fileInBytes);
   //             DEBUG("read from disk");
            return  content;
        } else {
        	content = GetCgiOutput();
        	cache.tryPut(fileName, content);
        	return content;
        }
    }

    private boolean ifModifiedSince(String request) {
        //If-Modified-Since: <day-name>, <day> <month> <year> <hour>:<minute>:<second> GMT
        String[] req = request.split("[ :]");
//        System.out.println("If-Modified-Since test");
//         System.out.println(req[0]);

        return req[0].equals("If-Modified-Since");

    }

    private String ModifiedSince(String request) {
        String date = request.replaceFirst("If-Modified-Since: ", "");
        return date;
    }

    private void outputResponseBody() throws Exception {
    	byte[] fileInBytes = GetFile();
        int numOfBytes = fileInBytes.length;
        outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
        outToClient.writeBytes("\r\n");
        

//        byte[] fileInBytes = new byte[numOfBytes];
//        // send file content
//        if (cache.containsKey(fileName)) {
//            fileInBytes = (byte[]) cache.get(fileName);
//            DEBUG("read from hashmap");
//        } else {
//            FileInputStream fileStream = new FileInputStream(fileName);
//            fileStream.read(fileInBytes);
//            cache.put(fileName, fileInBytes);
//            DEBUG("read from disk");
//        }
        outToClient.write(fileInBytes, 0, numOfBytes);
    }

    void outputError(int errCode, String errMsg) {
        try {
            outToClient.writeBytes("HTTP/1.0 " + errCode + " " + errMsg + "\r\n");
        } catch (Exception e) {
        }
    }
}

    