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
package jorgan.shell;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.App;
import jorgan.UI;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.io.DispositionFileFilter;
import jorgan.io.DispositionStream;
import jorgan.play.OrganPlay;
import jorgan.play.Problem;
import jorgan.play.event.PlayEvent;
import jorgan.play.event.PlayListener;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Shell for playing of an organ.
 */
public class OrganShell implements UI {

	private static final Logger logger = Logger.getLogger(OrganShell.class
			.getName());

	private static Configuration config = Configuration.getRoot().get(
			OrganShell.class);

	private File file;

	private Organ organ;

	private OrganPlay organPlay;

	private Interpreter interpreter;

	private InternalPlayerListener playerListener = new InternalPlayerListener();

	private boolean useDefaultEncoding;

	private String encoding;

	/**
	 * Create a new organShell.
	 */
	public OrganShell() {
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
		writeMessage("splash", App.getInstance().getVersion());

		if (!useDefaultEncoding) {
			try {
				interpreter.setEncoding(encoding);

				writeEncoding();
			} catch (UnsupportedEncodingException ex) {
				writeMessage("unsupportedEncoding", encoding);
			}
		}

		writeMessage("help");

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

			this.file = file;

			setOrgan(organ);

			writeMessage("organOpened", DispositionFileFilter
					.removeSuffix(file));
		} catch (IOException ex) {
			writeMessage("openOrganException", file.getName());
		} catch (Exception ex) {
			logger.log(Level.INFO, "opening organ failed", ex);

			writeMessage("openOrganInvalid", file.getName());
		}
	}

	/**
	 * Set the organ.
	 * 
	 * @param organ
	 *            the organ
	 */
	public void setOrgan(Organ organ) {
		if (organPlay != null) {
			organPlay.close();
			organPlay.dispose();
			organPlay = null;
		}

		this.organ = organ;

		if (organ != null) {
			organPlay = new OrganPlay(organ);
			organPlay.addPlayerListener(playerListener);

			for (int e = 0; e < organ.getElementCount(); e++) {
				showElementStatus(organ.getElement(e));
			}

			organPlay.open();
		}
	}

	/**
	 * Save the current organ.
	 */
	protected void saveOrgan() {
		try {
			new DispositionStream().write(organ, file);

			writeMessage("organSaved");
		} catch (Exception ex) {
			writeMessage("saveOrganException", file.getName());
		}
	}

	protected void showElementStatus(Element element) {

		List problems = organPlay.getProblems(element);
		if (problems != null) {
			for (int p = 0; p < problems.size(); p++) {
				Problem problem = (Problem) problems.get(p);

				String pattern = "problem/" + problem;

				writeMessage(pattern, element.getName(), problem.getValue());
			}
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
			writeMessage("defaultEncoding", defaultEncoding);
		} else {
			writeMessage("currentEncoding", encoding);
		}
	}

	/**
	 * The command for opening of a disposition.
	 */
	private class OpenCommand extends AbstractCommand {
		public String getPrefix() {
			return "openCommand";
		}

		public void execute(String param) {
			if (param == null) {
				writeMessage("openCommand/format");
				return;
			}
			openOrgan(new File(param));
		}
	}

	/**
	 * The command for closing of a disposition.
	 */
	private class CloseCommand extends AbstractCommand {
		public String getPrefix() {
			return "closeCommand";
		}

		public void execute(String param) {
			if (file == null) {
				writeMessage("closeCommand/file");
				return;
			}
			file = null;
			setOrgan(null);

			writeMessage("closeCommand/confirm");
		}
	}

	/**
	 * The command to save the current disposition.
	 */
	private class SaveCommand extends AbstractCommand {
		public String getPrefix() {
			return "saveCommand";
		}

		public void execute(String param) {
			if (param != null) {
				writeMessage("saveCommand/format");
				return;
			}
			if (file == null) {
				writeMessage("saveCommand/file");
				return;
			}
			saveOrgan();
		}
	}

	/**
	 * The help command.
	 */
	private class HelpCommand extends AbstractCommand {
		public String getPrefix() {
			return "helpCommand";
		}

		public void execute(String param) {
			if (param != null) {
				Command command = interpreter.getCommand(param);
				interpreter.writeln(command.getLongDescription());
			} else {
				writeMessage("helpCommand/header");
				int length = 0;
				for (int c = 0; c < interpreter.getCommandCount(); c++) {
					Command command = interpreter.getCommand(c);
					length = Math.max(length, command.getName().length());
				}
				for (int c = 0; c < interpreter.getCommandCount(); c++) {
					Command command = interpreter.getCommand(c);
					String name = pad(command.getName(), length);
					writeMessage("helpCommand/body", name, command
							.getDescription());
				}
				writeMessage("helpCommand/footer");
			}
		}
	}

	/**
	 * The command to show/change current encoding.
	 */
	private class EncodingCommand extends AbstractCommand {
		public String getPrefix() {
			return "encodingCommand";
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
					writeMessage("unsupportedEncoding", param);
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
		public String getPrefix() {
			return "recentCommand";
		}

		public void execute(String param) {
			List recents = new DispositionStream().getRecentFiles();
			if (recents.size() == 0) {
				writeMessage("recentCommand/none");
				return;
			}

			if (param == null) {
				writeMessage("recentCommand/header");

				for (int r = 0; r < recents.size(); r++) {
					File recent = (File) recents.get(r);
					writeMessage("recentCommand/list", new Object[] {
							new Integer(r + 1), recent });
				}
			} else {
				try {
					int index = Integer.parseInt(param) - 1;

					openOrgan((File) recents.get(index));
				} catch (Exception ex) {
					Integer from = new Integer(1);
					Integer to = new Integer(recents.size());
					writeMessage("recentCommand/format", new Object[] { from,
							to });
				}
			}
		}
	}

	/**
	 * The command to exit jOrgan.
	 */
	private class ExitCommand extends AbstractCommand {
		public String getPrefix() {
			return "exitCommand";
		}

		public void execute(String param) {
			if (param != null) {
				writeMessage("exitCommand/format");
				return;
			}

			if (organPlay != null) {
				organPlay.close();
				organPlay.dispose();
				organPlay = null;
			}

			writeMessage("exitCommand/confirm");

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
			writeMessage("unknownCommand");
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
			config.get(getPrefix()).read(this);
		}

		protected abstract String getPrefix();

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
	 * The monitor of player events.
	 */
	private class InternalPlayerListener implements PlayListener {

		public void inputAccepted() {
		}

		public void outputProduced() {
		}

		public void playerAdded(PlayEvent ev) {
		}

		public void playerRemoved(PlayEvent ev) {
		}

		public void problemAdded(PlayEvent ev) {
			showElementStatus(ev.getElement());
		}

		public void problemRemoved(PlayEvent ev) {
			showElementStatus(ev.getElement());
		}

		public void opened() {
		}

		public void closed() {
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