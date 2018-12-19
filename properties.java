/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ningluo
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ningluo
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.PrimitiveIterator.OfDouble;

import org.omg.CORBA.PUBLIC_MEMBER;

public class properties {
    private int port = 6789;
    private int CacheSize = 8096;
    private HashMap<String , String> NameMap;
    private int poolsize = 50;
   
    
   public properties() {
	   NameMap = new HashMap<String,String>();
   }
//    public static void main(String[] args) throws IOException {
//        String file = "/Users/ningluo/Desktop/networks/assignment/assignment3/config.conf";
//        config(file);
//        System.out.println(getServerDic("cicada.cs.yale.edu")); 
//        System.out.println(getServerPort());    
//    }
//    
    public  void config(String filename) throws IOException {
    	File file = new File(util.CONFIG_PATH+"/"+filename);
        GetPortInfo(file);
        GetCacheInfo(file);
        buildNameMap(file);
        setPoolSize(file);
        
    }
    
    public  int getCacheSize(){
        
        return CacheSize;
    }
    
    public  int getServerPort(){
      
        return port;
    }
    
    public  int getpoolSize(){
        System.out.println(poolsize);
        return poolsize;
    }
    
    public  String getServerDic(String ServerName){
        return NameMap.get(ServerName);
    }
    
    public HashMap getNameMap(){
        return NameMap;
    }
    
    private void buildNameMap(File file) throws FileNotFoundException, IOException {
       
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            if (st.length() == 0) {
                continue;
            }
            if (st.startsWith("<VirtualHost *:")) {
           //     System.out.println("yes");
                st = br.readLine();
                st = st.trim();
         //       System.out.println(st);
                String[] prop = st.split(" +");
                
                String DocumentRoot = prop[1];
                if (DocumentRoot.length()>2) {
                	 DocumentRoot =DocumentRoot.substring(0, DocumentRoot.length());
                }
               
                st = br.readLine();
                st = st.trim();
                
                prop = st.split(" +");
                String  ServerName= prop[1];
             //   System.out.println(ServerName+"  "+DocumentRoot); 
                NameMap.put(ServerName, DocumentRoot);
            } 
        }
        br.close();
    }
    
    private void setPoolSize(File file) throws IOException {
   	 BufferedReader br = new BufferedReader(new FileReader(file));
        String st = br.readLine();

       
        String[] poolInfo = st.split(" +");
        while (st!= null ) {
        	if (poolInfo.length>1){
        //	System.out.println(poolInfo[0]);
        	if(poolInfo[0].equals("ThreadPoolSize")) {
        		System.out.println(poolInfo[1]);
        		poolsize = Integer.parseInt(poolInfo[1]);
        		return;
        	}
        	}
        	st = br.readLine();
            poolInfo = st.split(" +");
        }
        
        
    }
        
        
    
    private void GetPortInfo(File file) throws IOException {
    	 BufferedReader br = new BufferedReader(new FileReader(file));
         String st = br.readLine();
         String[] portInfo = st.split(" +");
         while (st.length()== 0 ||!portInfo[0].equals("Listen") ) {
        	 st = br.readLine();
         }
        
         if (portInfo.length ==2) {
        	 port = Integer.parseInt(portInfo[1]);
        
         } else {
        	 System.out.println("incorrect config file");
         } 
         br.close();
    }
    
    private void GetCacheInfo(File file) throws IOException {
   	 BufferedReader br = new BufferedReader(new FileReader(file));
        String st = br.readLine();
        //Debug.DEBUG(st);
        String[] CacheInfo = st.split(" +");
        
        while ((st.length()== 0 || !CacheInfo[0].equals("CacheSize")) && st != null ) {
          	//bug.DEBUG(st);
          	st  = br.readLine();
          	CacheInfo = st.split(" +");
        }
       
        if (CacheInfo.length ==2 && CacheInfo[0]=="CacheSize") {
       	 CacheSize = Integer.parseInt(CacheInfo[1]);
        } else {
         
       	//ystem.out.println("incorrect config file");
        }  
        br.close();
   }
   
    
}
            
            
//            st = st.trim();
//            String[] prop = st.split(" +");
//            if (prop.length > 1)
//            System.out.println(prop[0]+":" + prop[1]);
//        }
//    
        
    
        
        
        
        
        
        
//        while ((st = br.readLine()) != null) 
//            if (!st.startsWith("<") && st.length()>0){
//            st = br.readLine())   
//            System.out.println(st);
//            String[] prop = st.split("\\s+"); 
//            System.out.println(prop[1]);
//            System.out.println(prop[2]);
//        }
    



