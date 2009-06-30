/*
 * jOrgan - Java Virtual Pipe Organ
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
package jorgan.cli;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.Info;
import jorgan.UI;
import jorgan.disposition.Elements;
import jorgan.disposition.Organ;
import jorgan.io.DispositionStream;
import jorgan.io.disposition.DispositionFileFilter;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.ProblemListener;
import jorgan.problem.Severity;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Command line interface implementation.
 */
public class CLI implements UI, SessionAware {

	private static final Logger logger = Logger.getLogger(CLI.class.getName());

	private static Configuration config = Configuration.getRoot()
			.get(CLI.class);

	private Organ organ;

	private OrganSession session;

	private Interpreter interpreter;

	private InternalProblemListener problemListener = new InternalProblemListener();

	private boolean useDefaultEncoding;

	private String encoding;

	/**
	 * Create a new organShell.
	 */
	public CLI() {
		config.read(this);

		List<Command> commands = new ArrayList<Command>();
		commands.add(new HelpCommand());
		commands.add(new EncodingCommand());
		commands.add(new OpenCommand());
		commands.add(new CloseCommand());
		commands.add(new RecentCommand());
		commands.add(new SaveCommand());
		commands.add(new ExitCommand());

		interpreter = new Interpreter(commands, new UnknownCommand());
		config.get("interpreter").read(interpreter);
	}

	/**
	 * Start the user interaction.
	 * 
	 * @param file
	 *            optional file that contains an organ
	 */
	public void display(File file) {
		writeMessage("splash", new Info().getVersion());

		if (!useDefaultEncoding) {
			try {
				interpreter.setEncoding(encoding);

				writeEncoding();
			} catch (UnsupportedEncodingException ex) {
				writeMessage("encodingUnsupported", encoding);
			}
		}

		if (file != null) {
			openOrgan(file);
		}

		interpreter.start();
	}

	/**
	 * Open an organ.
	 * 
	 * @param file
	 *            file that contains the organ
	 */
	public void openOrgan(File file) {
		try {
			Organ organ = new DispositionStream().read(file);

			setSession(new OrganSession(organ, file));

			writeMessage("openConfirm", DispositionFileFilter
					.removeSuffix(file));
		} catch (IOException ex) {
			writeMessage("openException", file.getName());
		} catch (Exception ex) {
			logger.log(Level.INFO, "opening organ failed", ex);

			writeMessage("openInvalid", file.getName());
		}
	}

	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.destroy();
			this.session = null;
		}

		this.session = session;

		if (session != null) {
			this.session = session;
			session.get(ElementProblems.class).addListener(problemListener);

			for (Problem problem : session.get(ElementProblems.class)
					.getProblems()) {
				problemListener.problemAdded(problem);
			}
		}
	}

	/**
	 * Save the current organ.
	 */
	protected void saveOrgan() {
		try {
			new DispositionStream().write(organ, session.getFile());

			writeMessage("saveConfirm");
		} catch (Exception ex) {
			writeMessage("saveException", session.getFile().getName());
		}
	}

	/**
	 * Show a message.
	 * 
	 * @param key
	 *            key of message to show
	 * @param args
	 *            arguments to message
	 */
	protected void writeMessage(String key, Object... args) {

		String text = config.get(key).read(new MessageBuilder()).build(args);

		interpreter.writeln(text);
	}

	private void writeEncoding() {
		if (useDefaultEncoding) {
			String defaultEncoding = System.getProperty("file.encoding");
			writeMessage("encodingDefault", defaultEncoding);
		} else {
			writeMessage("encodingCurrent", encoding);
		}
	}

	/**
	 * The command for opening of a disposition.
	 */
	private class OpenCommand extends AbstractCommand {
		@Override
		public String getKey() {
			return "open";
		}

		public void execute(String param) {
			if (param == null) {
				writeMessage("openParameter");
				return;
			}
			openOrgan(new File(param));
		}
	}

	/**
	 * The command for closing of a disposition.
	 */
	private class CloseCommand extends AbstractCommand {
		@Override
		public String getKey() {
			return "close";
		}

		public void execute(String param) {
			if (param != null) {
				writeMessage("closeParameter");
				return;
			}
			if (session == null) {
				writeMessage("closeNone");
				return;
			}
			setSession(null);

			writeMessage("closeConfirm");
		}
	}

	/**
	 * The command to save the current disposition.
	 */
	private class SaveCommand extends AbstractCommand {
		@Override
		public String getKey() {
			return "save";
		}

		public void execute(String param) {
			if (param != null) {
				writeMessage("saveParameter");
				return;
			}
			if (session == null) {
				writeMessage("saveNone");
				return;
			}
			saveOrgan();
		}
	}

	/**
	 * The help command.
	 */
	private class HelpCommand extends AbstractCommand {
		@Override
		public String getKey() {
			return "help";
		}

		public void execute(String param) {
			if (param != null) {
				Command command = interpreter.getCommand(param);
				interpreter.writeln(command.getLongDescription());
			} else {
				writeMessage("helpHeader");
				int length = 0;
				for (int c = 0; c < interpreter.getCommandCount(); c++) {
					Command command = interpreter.getCommand(c);
					length = Math.max(length, command.getName().length());
				}
				for (int c = 0; c < interpreter.getCommandCount(); c++) {
					Command command = interpreter.getCommand(c);
					String name = pad(command.getName(), length);
					writeMessage("helpElement", name, command.getDescription());
				}
				writeMessage("helpFooter");
			}
		}
	}

	/**
	 * The command to show/change current encoding.
	 */
	private class EncodingCommand extends AbstractCommand {
		@Override
		public String getKey() {
			return "encoding";
		}

		public void execute(String param) {

			if (param != null) {
				try {
					interpreter.setEncoding(param);

					String defaultEncoding = System
							.getProperty("file.encoding");
					if (defaultEncoding.equalsIgnoreCase(param)) {
						useDefaultEncoding = true;
					} else {
						useDefaultEncoding = false;
						encoding = param;
					}
				} catch (UnsupportedEncodingException ex) {
					writeMessage("encodingUnsupported", param);
					return;
				}
			}

			writeEncoding();
		}
	}

	/**
	 * The command to show recent dispositions and opening a recent disposition.
	 */
	private class RecentCommand extends AbstractCommand {
		@Override
		public String getKey() {
			return "recent";
		}

		public void execute(String param) {
			List<File> recents = new DispositionStream().getRecentFiles();
			if (recents.size() == 0) {
				writeMessage("recentNone");
				return;
			}

			if (param == null) {
				writeMessage("recentHeader");

				for (int r = 0; r < recents.size(); r++) {
					File recent = recents.get(r);
					writeMessage("recentElement", new Object[] {
							new Integer(r + 1), recent });
				}
			} else {
				File file;
				try {
					int index = Integer.parseInt(param) - 1;
					file = recents.get(index);
				} catch (RuntimeException ex) {
					Integer from = new Integer(1);
					Integer to = new Integer(recents.size());
					writeMessage("recentParameter", new Object[] { from, to });
					return;
				}
				openOrgan(file);
			}
		}
	}

	/**
	 * The command to exit jOrgan.
	 */
	private class ExitCommand extends AbstractCommand {
		@Override
		public String getKey() {
			return "exit";
		}

		public void execute(String param) {
			if (param != null) {
				writeMessage("exitParameter");
				return;
			}

			if (session != null) {
				session.destroy();
				session = null;
			}

			writeMessage("exitConfirm");

			interpreter.stop();
		}
	}

	/**
	 * The command used in case of a interpretation of an unknown command.
	 */
	private class UnknownCommand implements Command {
		public String getName() {
			return null;
		}

		public String getDescription() {
			return null;
		}

		public String getLongDescription() {
			return null;
		}

		public void execute(String param) {
			writeMessage("unknown");
		}
	}

	/**
	 * The abstract base class for all commands of this organ shell.
	 */
	public abstract class AbstractCommand implements Command {

		private String name;

		private String description;

		private String longDescription;

		protected AbstractCommand() {
			config.get(getKey()).read(this);
		}

		protected abstract String getKey();

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getLongDescription() {
			return longDescription;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public void setLongDescription(String longDescription) {
			this.longDescription = longDescription;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * The monitor of problems.
	 */
	private class InternalProblemListener implements ProblemListener {

		public void problemAdded(Problem problem) {

			String key;
			if (problem.getSeverity() == Severity.ERROR) {
				key = "error";
			} else {
				key = "warning";
			}
			writeMessage(key, Elements.getDisplayName(problem.getElement()),
					problem.getMessage());
		}

		public void problemRemoved(Problem problem) {
		}
	}

	/**
	 * Utility method to pad a string with whitespace to the given length.
	 * 
	 * @param text
	 *            text to pad with whitespace
	 * @param length
	 *            length to pad to
	 * @return padded string
	 */
	private static String pad(String text, int length) {
		StringBuffer buffer = new StringBuffer(length);
		buffer.append(text);

		for (int c = text.length(); c < length; c++) {
			buffer.append(" ");
		}

		return buffer.toString();
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isUseDefaultEncoding() {
		return useDefaultEncoding;
	}

	public void setUseDefaultEncoding(boolean useDefaultEncoding) {
		this.useDefaultEncoding = useDefaultEncoding;
	}
}