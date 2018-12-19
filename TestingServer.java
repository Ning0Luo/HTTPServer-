
import java.nio.channels.*;

import org.w3c.dom.NamedNodeMap;

import serverCache.ServerCacheSingleton;
import serverCache.SynchronizedCache;

import java.net.*;
import java.io.IOException;

public class TestingServer {

   
    public static int port =10000;
    public static String WWW_ROOT = util.WS_ROOT;

    public static ServerSocketChannel openServerChannel(int port) {

        ServerSocketChannel serverChannel = null;
        try {

            // open server socket for accept
            serverChannel = ServerSocketChannel.open();

            // extract server socket of the server channel and bind the port
            ServerSocket ss = serverChannel.socket();
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);

            // configure it to be non blocking
            serverChannel.configureBlocking(false);
            Debug.DEBUG("Server listening for connections on port " + port);

        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        } // end of catch

        return serverChannel;
    } // end of open serverChannel

    public static void main(String[] args) throws IOException {

        properties prop = new properties();

        if (args.length >= 1) {
            prop.config(util.CONFIG_PATH+"/"+args[0]);
            port = prop.getServerPort();
            //System.out.println(port);
        }

        // get dispatcher/selector
        Dispatcher dispatcher = new Dispatcher();

        // open server socket channel
        
        ServerSocketChannel sch = openServerChannel(port);
        ServerCacheSingleton.init(2*1024*1024*10); // 10 files of 2MB
        
        // create server acceptor for Echo Line ReadWrite Handler
        GetRequesHandlerFactory GetResponseHeaderFactory= new GetRequesHandlerFactory(prop.getNameMap());
        Acceptor acceptor = new Acceptor(GetResponseHeaderFactory);

        Thread dispatcherThread;
        //Thread TimeOutThread;
        // register the server channel to a selector
        try {
            SelectionKey key = sch.register(dispatcher.selector(), SelectionKey.OP_ACCEPT);
            key.attach(acceptor);
            Debug.DEBUG(key);
            // start dispatcher
            dispatcherThread = new Thread(dispatcher);
          //  TimeWatcher timeWatcher = new TimeWatcher(dispatcher.selector());
         //   TimeOutThread = new Thread(timeWatcher);
            dispatcherThread.start();
            //TimeOutThread.start();
        } catch (IOException ex) {
            System.out.println("Cannot register and start server");
            System.exit(1);
        }
        // may need to join the dispatcher thread

    } // end of main

} // end of class
