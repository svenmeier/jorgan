/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * An interpreter of commands.
 * 
 * @see jorgan.shell.Command
 */
public class Interpreter {

	private static String[] encodings;

	/**
	 * The default prompt.
	 */
	public static final String DEFAULT_PROMPT = ">";

	/**
	 * Should this interpreter stop.
	 */
	private boolean stop = false;

	/**
	 * The commands to interpret.
	 */
	private List<Command> commands;

	/**
	 * The unknown command.
	 */
	private Command unknown;

	/**
	 * The prompt to display.
	 */
	private String prompt = DEFAULT_PROMPT;

	/**
	 * The encoding of this interpreter.
	 */
	private String encoding;

	/**
	 * The reader used to read from system in.
	 */
	private BufferedReader reader;

	/**
	 * The writer to write to system out.
	 */
	private PrintWriter writer;

	/**
	 * Create a new interpreter for commands.
	 * 
	 * @param commands
	 *            the command to use
	 * @param unknown
	 *            the command to use in case of an unknown command
	 */
	public Interpreter(List<Command> commands, Command unknown) {

		this.commands = commands;
		this.unknown = unknown;
	}

	/**
	 * Set the prompt to use.
	 * 
	 * @param prompt
	 *            the prompt to use
	 */
	public void setPrompt(String prompt) {
		this.prompt = prompt;
	}

	/**
	 * Set the encoding to use.
	 * 
	 * @param encoding
	 *            the encoding to use
	 * @throws UnsupportedEncodingException
	 */
	public void setEncoding(String encoding)
			throws UnsupportedEncodingException {
		reader = new BufferedReader(new InputStreamReader(System.in, encoding));
		writer = new PrintWriter(new OutputStreamWriter(System.out, encoding));

		this.encoding = encoding;
	}

	/**
	 * Get all supported encodings.
	 * 
	 * @return encodings
	 */
	public static String[] getEncodings() {
		if (encodings == null) {
			Set<String> keys = Charset.availableCharsets().keySet();

			String[] encodings = keys.toArray(new String[keys.size()]);
			
			Arrays.sort(encodings);
			
			Interpreter.encodings = encodings;
		}
		return encodings;
	}

	/**
	 * Get the encoding.
	 * 
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Start the interpretation.
	 */
	public void start() {
		try {
			while (!stop) {
				write(prompt);
				String line = readLine();
				StringTokenizer tokens = new StringTokenizer(line, " ");
				if (tokens.hasMoreTokens()) {
					Command command = getCommand(tokens.nextToken());
					String param = null;
					if (tokens.hasMoreTokens()) {
						param = tokens.nextToken();
					}
					command.execute(param);
				}
			}
		} catch (IOException ex) {
			throw new Error("unexpected", ex);
		}
	}

	/**
	 * Stop this interpreter.
	 */
	public void stop() {
		stop = true;
	}

	/**
	 * Read a line.
	 * 
	 * @return read line
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		if (reader == null) {
			reader = new BufferedReader(new InputStreamReader(System.in));
		}
		return reader.readLine();
	}

	/**
	 * Write text with new line.
	 * 
	 * @param text
	 *            text to print
	 */
	public void writeln(String text) {
		write(text);
		write("\n");
	}

	/**
	 * Write text.
	 * 
	 * @param text
	 *            text to print
	 */
	public void write(String text) {
		if (writer == null) {
			writer = new PrintWriter(new OutputStreamWriter(System.out));
		}
		writer.print(text);
		writer.flush();
	}

	/**
	 * Get the command for the given name. If no command for the name can be
	 * looked up, the 'unkown' command will be returned.
	 * 
	 * @param name
	 *            name to get command for
	 * @return the command
	 */
	public Command getCommand(String name) {
		for (Command command : commands) {
			if (name.equals(command.getName())) {
				return command;
			}
		}
		return unknown;
	}

	/**
	 * Get the count of commands.
	 * 
	 * @return the count of commands
	 */
	public int getCommandCount() {
		return commands.size();
	}

	/**
	 * Get the command for the given index.
	 * 
	 * @param index
	 *            the index to get the command for
	 * @return the command
	 */
	public Command getCommand(int index) {
		return commands.get(index);
	}
}