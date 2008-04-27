package jorgan.util;

import java.io.Closeable;
import java.io.IOException;

public class IOUtils {

	public static void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}
		
		try {
			closeable.close();
		} catch (IOException quiet) {
		}
	}
}
