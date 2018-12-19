
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
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
public class Service_b implements Runnable {

	public static String WWW_ROOT;
	properties prop;
	Queue<Socket> queue = new LinkedList<>();

	public Service_b(Queue<Socket> q, properties p, String root) {
		queue = q;
		prop = p;
		WWW_ROOT = root;
	}

	@Override
	public void run() {
		while (true) {
			Socket connectionSocket = null;
			while (connectionSocket == null) {
				synchronized (queue) {
					if (!queue.isEmpty()) {
						connectionSocket = queue.remove();
					}
				}
			}
			WebRequestHandler wrh;
			try {
				wrh = new WebRequestHandler(connectionSocket, prop.getNameMap());
				wrh.processRequest();
			} catch (Exception ex) {
				Logger.getLogger(Service_b.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

	}

}
