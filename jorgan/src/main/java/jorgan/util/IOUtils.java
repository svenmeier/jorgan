package jorgan.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {

	private static final int DEFAULT_BUFFER_SIZE = 1024;

	public static void closeQuietly(Closeable closeable) {
		if (closeable == null) {
			return;
		}

		try {
			closeable.close();
		} catch (IOException quiet) {
		}
	}

	public static int copy(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}
}
