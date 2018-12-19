import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeWatcher implements Runnable {
     private Selector Monitoredselector;
     public TimeWatcher (Selector s) {
    	 Monitoredselector = s;
     }
	@Override
	public void run() {
		Set<SelectionKey> readyKeys;
		synchronized (this) {
			readyKeys = Monitoredselector.selectedKeys();
		}

		Iterator<SelectionKey> iterator = readyKeys.iterator();
		while (iterator.hasNext()) {
			SelectionKey key = (SelectionKey) iterator.next();
			if (key.isReadable() && !key.isWritable()) {
				GetRequestHandler rwH = (GetRequestHandler) key.attachment();
				long WaitingTime = rwH.livingTime();
				if (WaitingTime > 3000) {
					synchronized (key) {
						try {
							SocketChannel client = (SocketChannel)key.channel();
							ByteBuffer outBuffer = ByteBuffer.allocate(4096);
							byte[] error = coding.encoding("TimeOut");
					        outBuffer.put(error);
					        client.write(outBuffer);
							client.close();
							key.cancel();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}

		}

	};
     
     
}
