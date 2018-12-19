package serverCache;

public class ServerCacheSingleton {
	private static SynchronizedCache cache = null;
	
	public static SynchronizedCache init(int size) {
		cache = new SynchronizedCache(size);
		return cache;
	}
	
	public static SynchronizedCache getInstance() {
		return cache;
	}
}
