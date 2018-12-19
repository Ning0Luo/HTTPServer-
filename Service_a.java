
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ningluo
 */
public class Service_a implements Runnable {

    ServerSocket WelcomeSocket;
    //Thread t;
    public static String WWW_ROOT;
    properties prop;

    public Service_a(ServerSocket s, properties p, String root) {
        WelcomeSocket = s;
        prop = p;
        WWW_ROOT = root;
    }

    @Override
    public void run() {
        while (true) {
            Socket connectionSocket = null;
            
			synchronized (WelcomeSocket) {
				try {
					connectionSocket = WelcomeSocket.accept();
				} catch (IOException ex) {
					// Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
				} catch (Exception ex) {
					// Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			// System.out.println("\nReceive request from " + connectionSocket);
			WebRequestHandler wrh = null;
			try {
				wrh = new WebRequestHandler(connectionSocket, prop.getNameMap());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				wrh.processRequest();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				connectionSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

    } // end run




