import java.io.IOException;

public class myMonitor implements Monitor{
	private static int count =0;

	public boolean ifAccept() throws IOException {
		count = (count +1)%10;
		return (count != 0);
	}
	
}
