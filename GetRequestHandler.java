
import java.awt.RenderingHints.Key;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.rmi.activation.ActivationGroupDesc.CommandEnvironment;
import java.rmi.server.LoaderHandler;
import java.sql.Time;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.PrimitiveIterator.OfDouble;

import javax.lang.model.element.VariableElement;
import javax.xml.ws.RequestWrapper;

import org.w3c.dom.css.ElementCSSInlineStyle;

import serverCache.ServerCacheSingleton;
import serverCache.SynchronizedCache;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ningluo
 */
public class GetRequestHandler implements IReadWriteHandler {

    private ByteBuffer inBuffer;
    private ByteBuffer outBuffer;
    private boolean requestComplete;
    private boolean responseHeaderReady;
    private boolean responseSent;
    private boolean responseReady;
    private boolean channelClosed;
    
  

    private String urlName;
    private String[] cgiArguments;
    private String modifiedDate;
    HashMap<String, String> NameMap; 
    private String hostName;
    SimpleDateFormat sdf = new SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    
    private long start;

    
    public GetRequestHandler() {
        inBuffer = ByteBuffer.allocate(4096);
        outBuffer = ByteBuffer.allocate(2*1024*1024); // 2MB

        // initial state
        requestComplete = false;
        responseHeaderReady = false;
        responseSent = false;
        channelClosed = false;
        responseReady = false;
        long start = System.currentTimeMillis();
    
        
    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     //   Debug.DEBUG("processInBuffer");
    	
    	Debug.DEBUG("->handleRead");

		if (requestComplete) { // this call should not happen, ignore
			return;
		}

		processInBuffer(key);

		if(requestComplete) {
			updateState(key);
			try {
				SetResponseHeader(hostName);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            try {
				SetResponseContents(hostName);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            updateState(key);
        }
    }

    @Override
	public void handleWrite(SelectionKey key) throws IOException {

		SocketChannel client = (SocketChannel) key.channel();
		int writeBytes = client.write(outBuffer);
		Debug.DEBUG("handleWrite: write " + writeBytes + " bytes; after write " + outBuffer);
		if (responseReady && (outBuffer.remaining() == 0)) {
			responseSent = true;
			Debug.DEBUG("handleWrite: responseSent");
			client.close();
			Debug.DEBUG("socket close");
			Debug.DEBUG(client.isOpen());

			// update state
//			updateState(key);
//			responseSent = false;
//			requestComplete = false;
//	        responseHeaderReady = false;
//	        responseSent = false;
//	        responseReady = false;
//	        updateState(key);
		}
	}

    

    @Override
    public int getInitOps() {
    	return SelectionKey.OP_READ;
    }

    @Override
    public void handleException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public long livingTime() {
    	return System.currentTimeMillis() -start;
    	
    }

    
    private void ProcessRequest(String req) throws IOException {
      Debug.DEBUG("enter processing request");
      String[] requests = req.split("\r\n");
     
      String GetCommand = requests[0];
      Debug.DEBUG(GetCommand+"***********");
      ProcessGet(GetCommand);
      Debug.DEBUG("Get Finished!!!!!!");
     
     
      for (int i =1; i<requests.length;i++) {
          String msg = requests[i];
          Debug.DEBUG(msg +"Getline ....********");
          
              if (ifModifiedSince(msg)) {
                  modifiedDate = ProcessModifiedSince(msg);
              }
              if (ifHostLine(msg)) {
                  hostName = ProcessHostLine(msg);
              }
       }
       requestComplete = true ;
      
    }
     
    
  

    private void updateState(SelectionKey key) throws IOException {

        Debug.DEBUG("->Update dispatcher.");
        

        if (channelClosed) {
            return;
        }

       
        int nextState = key.interestOps();
        if (requestComplete) {
            nextState = nextState & ~SelectionKey.OP_READ;
            Debug.DEBUG("New state: -Read since request parsed complete");
        } else {
            nextState = nextState | SelectionKey.OP_READ;
            Debug.DEBUG("New state: +Read to continue to read");
        }

        if (responseReady) {

            if (!responseSent) {
                nextState = nextState | SelectionKey.OP_WRITE;
                Debug.DEBUG("New state: +Write since response ready but not done sent");
            } else {
                nextState = nextState & ~SelectionKey.OP_WRITE;
                Debug.DEBUG("New state: -Write since response ready and sent");
            }
        }

        key.interestOps(nextState);

    }
    
    private void processInBuffer(SelectionKey key) throws IOException {
		Debug.DEBUG("processInBuffer");
		SocketChannel client = (SocketChannel) key.channel();
		int readBytes = client.read(inBuffer);
		Debug.DEBUG("handleRead: Read data from connection " + client + " for " + readBytes + " byte(s); to buffer "
				+ inBuffer);
		
		if (readBytes == -1) { // end of stream
			requestComplete = true;
			Debug.DEBUG("handleRead: readBytes == -1");
		} else {
		   if(CheckIfRequestReady()) {
			   Debug.DEBUG("SUCCESS!!!!!!!!!!!!!!!!!!!!");
			   String request = new String(inBuffer.array(), "ASCII");
			   Debug.DEBUG(request);
			 //  req.put(inBuffer);
			   ProcessRequest(request);
			   inBuffer.clear();
		   }
			
		}
		
	}
    
   
    
    public boolean CheckIfRequestReady() {
    	String EndSymbols = "\r\n\r\n";
    	return coding.endsWith(inBuffer, EndSymbols);

    }
    
    

  
    

    private void ProcessGet(String message) throws IOException {
    	Debug.DEBUG("processing get");
    	Debug.DEBUG(message);
        String[] req = null;
        String[] command = message.split("\\s");
        if (command.length < 2 || !command[0].equals("GET")) {
        	Debug.DEBUG(command[0]);
            outputError(500, "Bad request");
            return;
        } 
        // parse URL to retrieve file name
        String url = command[1];
        req = url.split("[?&]");
        urlName = req[0];
        if (urlName.startsWith("/") == true) {
            urlName = urlName.substring(1);
        }
        cgiArguments = req;
    }

    public String url2FileName(String host) {
        String ServerRootDic = NameMap.get(host);   
        //////////////////// to do need to change latter!!  
      //  Debug.DEBUG(ServerRootDic);
        String fileName = ServerRootDic + "/" + urlName;
        return fileName;
    }

    public void SetResponseContents(String HostName) throws IOException, ParseException {
    	//Debug.DEBUG("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@!!!!!!!!!!!!!!!!");
		if (responseHeaderReady) {
			byte[] contents = GetContents(HostName);
			if (contents != null) {
				int numOfBytes = contents.length;
				byte[] fileLength = coding.encoding("Content-Length: " + numOfBytes + "\r\n");
				outBuffer.put(fileLength);
				//Debug.DEBUG("@@@@@@@@@@@@@@@@@@@@@@@@@@"+outBuffer);
				//Debug.DEBUG(numOfBytes);
		    	//Debug.DEBUG("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@111111111111111111111111111111111111");
				outBuffer.put(contents);
				}
				outBuffer.flip();
			
			responseReady = true;
		}
    	//Debug.DEBUG("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@!!!!!!!!!!!!!!!!");

	}

    public byte[] GetContents(String HostName) throws FileNotFoundException, IOException, ParseException {
        if (cgiArguments == null) {
            return null;
        }
        
        Debug.DEBUG(HostName);
        String fileName = url2FileName(HostName);
        SynchronizedCache cache = ServerCacheSingleton.getInstance();
    	//Debug.DEBUG("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    	byte[] content = cache.get(fileName);
        
    	if (content != null) {
        	return content;
        }
        
        File fileInfo = new File(fileName);
        Debug.DEBUG(fileName);

        if (!fileInfo.isFile()) {
            //fileInfo = null;
            return null;
        } 
        
        if(!CheckModified()) {
        	return null;
        }
     
        if (!fileInfo.canExecute()) {
            int numOfBytes = (int) fileInfo.length();
            byte[] fileInBytes = new byte[numOfBytes];  // need to change hash ! ................!!!!!!!!!!!!!to do  
            FileInputStream fileStream = new FileInputStream(fileName);
            fileStream.read(fileInBytes);
            fileStream.close();
        	cache.tryPut(fileName, fileInBytes);
           // Debug.DEBUG(fileName+"HashMap~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

            return fileInBytes;
            
        } else {
            byte[] fileInBytes  = GetCgiOutput(fileName);
            cache.tryPut(fileName, fileInBytes);
            //Debug.DEBUG(fileName+"HashMap~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            return fileInBytes;
        }

    }

    private byte[] GetCgiOutput(String fileName) {
        File fileInfo = new File(fileName);
        byte[] fileBytes = new byte[8 * 1024];
        List<String> commands = new ArrayList<String>();
        commands.add("python3");
        commands.add(fileName);
        for (String arg : cgiArguments) {
            commands.add(arg);
        }
        ProcessBuilder pb = new ProcessBuilder(commands);
        Map<String, String> env = pb.environment();

        // clear environment
        env.entrySet().removeIf(entry -> !entry.getKey().equals("PATH"));
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
        pb.directory(fileInfo.getParentFile());
      
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
    
   
    private boolean ifModifiedSince(String request) {
        //If-Modified-Since: <day-name>, <day> <month> <year> <hour>:<minute>:<second> GMT
        String[] req = request.split("[ :]");
//        System.out.println("If-Modified-Since test");
//         System.out.println(req[0]);

        return req[0].equals("If-Modified-Since");

    }

    private String ProcessModifiedSince(String request) {
        String date = request.replaceFirst("If-Modified-Since: ", "");
        return date;
    }

    private boolean ifHostLine(String msg) {

        String[] header = msg.split(" ");
        return header[0].equals("Host:");
    }

    private String ProcessHostLine(String msg) {

        String[] header = msg.split(" ");
        return header[1];
    }

    private void outToClient(String response) throws IOException {
        byte[] res = coding.encoding(response);
        outBuffer.put(res);
    }

    private void SetResponseHeader(String HostName) throws IOException, ParseException {
    	
    	if(urlName == null) {
    		//Debug.DEBUG("1@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@SB");
    		outToClient("urlName should not be null");
    		responseHeaderReady = true;
    		return ;
    	}

    	if(CheckIfload()) {
    		boolean IfAccept = true;
            try {
				Class myMonitor = MonitorFactory.getClassFromFile("myMonitor");
				Monitor monitor = (Monitor)myMonitor.newInstance();
				IfAccept = monitor.ifAccept();								
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            if(IfAccept)  outToClient("HTTP/1.0 200 Accept");
            else outputError(503, "overloading");
    		responseHeaderReady = true;
    		return;
    	}
    	
    	if(hostName == null) {
    		outToClient("Require host name ");
    		responseHeaderReady = true;
    		return ;
    	}
    	
    	
    	Debug.DEBUG(HostName);
        String fileName = url2FileName(HostName);
        Debug.DEBUG(fileName);
        File fileInfo = new File(fileName);
        Debug.DEBUG(fileName);
        Debug.DEBUG("------------------");
        if (!fileInfo.isFile()) {
    		//Debug.DEBUG("2@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@SB");
             outputError(404, "Not Found");
             fileInfo = null;
     		 responseHeaderReady = true;
          //   Debug.DEBUG("------------------");

             return ;
         } 
        
    	if(!CheckModified()) {
    		//Debug.DEBUG("3@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@SB");
    		outputError(304, "not modified since" + modifiedDate);
    		responseHeaderReady = true;
    		return;
    	}
    	

    	    	
        outToClient("HTTP/1.0 200 Document Follows\r\n");
        Date d = new Date();
        String date = d.toString();
    	outToClient("Date: "+date+"\r\n");
    	outToClient("Server: "+hostName +"\r\n");
        if(urlName != null) {

        if (urlName.endsWith(".jpg")) {
            outToClient("Content-Type: image/jpeg\r\n");
        } else if (urlName.endsWith(".gif")) {
            outToClient("Content-Type: image/gif\r\n");
        } else if (urlName.endsWith(".html") || urlName.endsWith(".htm")) {
            outToClient("Content-Type: text/html\r\n");
        } else {
            outToClient("Content-Type: text/plain\r\n");
        }
        }
    	
        responseHeaderReady = true;

    }

    public boolean CheckModified() throws ParseException {
    	if(modifiedDate != null) {
   		 String fileName = url2FileName(hostName);
   	     File fileInfo = new File(fileName);
            String lm = sdf.format(fileInfo.lastModified());
   	     Date time1 = sdf.parse(lm);
            Date time2 = sdf.parse(modifiedDate);
   	      return time1.after(time2);
       	} else return true;
    	
    	}
    
    public boolean CheckIfload() {
    	return urlName.equals("load");
    }
   
    
    
    public void outputError(int type, String exp) throws IOException {
    	outBuffer.clear();
        byte[] error = coding.encoding("HTTP/1.0 " + type + " " + exp + "\r\n");
        outBuffer.put(error);
    }
    
    public void SetNameMap(HashMap<String,String> hm) {
    	NameMap = hm;
    	
    }

}
