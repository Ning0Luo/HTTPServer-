
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ningluo
 */
class TimeServerHandlerExecutePool {
   
    private ExecutorService executor;

    public  TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
		executor = new ThreadPoolExecutor(
		 				  Runtime.getRuntime().availableProcessors(), 
		 				  maxPoolSize,
		 				  120L, TimeUnit.SECONDS,
		 				  new ArrayBlockingQueue<java.lang.Runnable>(queueSize)
		 );
    }
    public  void execute(Runnable task) {
		executor.execute(task);
    }
}

    
    

