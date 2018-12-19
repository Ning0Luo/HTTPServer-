package serverCache;
import java.util.HashMap;

public class SynchronizedCache {
	private HashMap<String, byte []> map;
	private int size;
	private int maxsize;
	public SynchronizedCache(int maxsize) {
		this.map = new HashMap<String, byte []>();
		this.maxsize = maxsize;
	}
	
	public byte[] get(String key) {
		synchronized (this) {
			return map.get(key);
		}
	}
	
	public void tryPut(String key, byte[] value) {
		synchronized (this) {
			int valueSize = value.length;
			if (maxsize >= size + valueSize) {
				map.put(key, value);
				size += valueSize;
			}
		}
	}
}
