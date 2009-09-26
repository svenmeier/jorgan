package jorgan.memory.io;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jorgan.memory.io.xstream.BooleanArrayConverter;
import jorgan.memory.io.xstream.FloatArrayConverter;
import jorgan.memory.state.CombinationState;
import jorgan.memory.state.ContinuousReferenceState;
import jorgan.memory.state.MemoryState;
import jorgan.memory.state.ReferenceState;
import jorgan.memory.state.SwitchReferenceState;
import jorgan.util.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * A {@link MemoryState} streamer.
 */
public class MemoryStateStream {

	private XStream xstream = new XStream(new XppDriver());

	public MemoryStateStream() {
		xstream.alias("memory", MemoryState.class);
		xstream.alias("combination", CombinationState.class);
		xstream.alias("switch", SwitchReferenceState.class);
		xstream.alias("continuous", ContinuousReferenceState.class);

		xstream.useAttributeFor(CombinationState.class, "id");
		xstream.useAttributeFor(ReferenceState.class, "id");

		xstream.registerConverter(new FloatArrayConverter());
		xstream.registerConverter(new BooleanArrayConverter());
	}

	public MemoryState read(File file) throws IOException {
		InputStream input = new FileInputStream(file);

		try {
			MemoryState state = read(input);
			return state;
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public MemoryState read(InputStream in) throws IOException {
		try {
			return (MemoryState) xstream.fromXML(new BufferedInputStream(in));
		} catch (Exception ex) {
			IOException io = new IOException(ex.getMessage());
			io.initCause(ex);
			throw io;
		}
	}

	public void write(MemoryState memoryState, File file) throws IOException {

		FileOutputStream output = new FileOutputStream(file);
		try {
			write(memoryState, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	public void write(MemoryState memoryState, OutputStream out)
			throws IOException {
		xstream.toXML(memoryState, out);

		out.close();
	}
}