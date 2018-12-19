import java.io.IOException;
import java.nio.channels.SelectionKey;

import javax.imageio.ImageTypeSpecifier;

public interface Monitor {
	public boolean ifAccept() throws IOException;
}
