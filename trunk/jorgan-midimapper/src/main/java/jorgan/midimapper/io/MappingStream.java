package jorgan.midimapper.io;

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

import jorgan.midimapper.mapping.Mapper;
import jorgan.midimapper.mapping.Mapping;
import jorgan.midimapper.mapping.Message;
import jorgan.util.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XppDriver;

/**
 * A {@link Mapping} streamer.
 */
public class MappingStream {

	private static final String ENCODING = "UTF-8";

	private XStream xstream = new XStream(new XppDriver());

	public MappingStream() {
		xstream.alias("mapping", Mapping.class);
		xstream.alias("mapper", Mapper.class);
		xstream.alias("message", Message.class);
	}

	public Mapping read(File file) throws IOException {
		InputStream input = new FileInputStream(file);

		try {
			return read(input);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public Mapping read(InputStream in) throws IOException {
		Reader reader = new InputStreamReader(new BufferedInputStream(in),
				ENCODING);

		try {
			return (Mapping) xstream.fromXML(reader);
		} catch (Exception ex) {
			IOException io = new IOException(ex.getMessage());
			io.initCause(ex);
			throw io;
		}
	}

	public void write(Mapping mapping, File file) throws IOException {

		FileOutputStream output = new FileOutputStream(file);
		try {
			write(mapping, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}

	public void write(Mapping mapping, OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(new BufferedOutputStream(out),
				ENCODING);
		writer
				.write("<?xml version=\"1.0\" encoding=\"" + ENCODING
						+ "\" ?>\n");
		xstream.toXML(mapping, writer);
	}
}