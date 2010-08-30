package jorgan.midimerger.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import jorgan.midimerger.merging.Merger;
import jorgan.midimerger.merging.Merging;
import jorgan.util.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * A {@link Mapping} streamer.
 */
public class MergingStream {

	private static final String ENCODING = "UTF-8";

	private XStream xstream = new XStream(new XppDriver());

	public MergingStream() {
		xstream.alias("merging", Merging.class);
		xstream.alias("merger", Merger.class);
	}

	public Merging read(File file) throws IOException {
		InputStream input = new FileInputStream(file);

		try {
			return read(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public Merging read(InputStream in) throws IOException {
		Reader reader = new InputStreamReader(new BufferedInputStream(in),
				ENCODING);

		try {
			return (Merging) xstream.fromXML(reader);
		} catch (Exception ex) {
			IOException io = new IOException(ex.getMessage());
			io.initCause(ex);
			throw io;
		}
	}

	public void write(Merging merging, File file) throws IOException {

		FileOutputStream output = new FileOutputStream(file);
		try {
			write(merging, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	public void write(Merging merging, OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(new BufferedOutputStream(out),
				ENCODING);
		writer
				.write("<?xml version=\"1.0\" encoding=\"" + ENCODING
						+ "\" ?>\n");
		xstream.toXML(merging, writer);
	}
}