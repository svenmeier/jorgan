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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import jorgan.App;
import jorgan.UI;
import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.io.DispositionFileFilter;
import jorgan.io.DispositionReader;
import jorgan.io.DispositionWriter;
import jorgan.play.OrganPlay;
import jorgan.play.Problem;
import jorgan.play.event.PlayEvent;
import jorgan.play.event.PlayListener;
import jorgan.xml.XMLFormatException;

/**
 * Shell for playing of an organ.
 */
public class OrganShell implements UI {

    private static final Logger logger = Logger.getLogger(OrganShell.class
            .getName());

    private static ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.shell.resources");

    private File file;

    private Organ organ;

    private OrganPlay organPlay;

    private Interpreter interpreter;

    private InternalPlayerListener playerListener = new InternalPlayerListener();

    /**
     * Create a new organShell.
     */
    public OrganShell() {

        List commands = new ArrayList();
        commands.add(new HelpCommand());
        commands.add(new EncodingCommand());
        commands.add(new OpenCommand());
        commands.add(new CloseCommand());
        commands.add(new RecentCommand());
        commands.add(new SaveCommand());
        commands.add(new ExitCommand());
        interpreter = new Interpreter(commands, new UnknownCommand());
        interpreter.setPrompt(resources.getString("prompt"));

        if (!Configuration.instance().getUseDefaultEncoding()) {
            String encoding = Configuration.instance().getEncoding();
            try {
                interpreter.setEncoding(encoding);
                showMessage("encoding", new Object[] { encoding });
            } catch (UnsupportedEncodingException ex) {
                showMessage("encoding.unsupported", new Object[] { encoding });
            }
        }
    }

    /**
     * Start the user interaction.
     * 
     * @param file
     *            optional file that contains an organ
     */
    public void start(File file) {
        showMessage("splash", new Object[] { App.getVersion() });

        showMessage("help", new Object[0]);

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
            DispositionReader reader = new DispositionReader(
                    new FileInputStream(file));

            Organ organ = (Organ) reader.read();

            this.file = file;

            jorgan.io.Configuration.instance().addRecentFile(file);

            setOrgan(organ);

            showMessage("command.open.confirm",
                    new Object[] { DispositionFileFilter.removeSuffix(file) });
        } catch (XMLFormatException ex) {
            logger.log(Level.INFO, "opening organ failed", ex);

            showMessage("command.open.exception.invalid", new String[] { file
                    .getName() });
        } catch (IOException ex) {
            showMessage("command.open.exception",
                    new String[] { file.getName() });
        }
    }

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
            DispositionWriter writer = new DispositionWriter(
                    new FileOutputStream(file));
            writer.write(organ);

            jorgan.io.Configuration.instance().addRecentFile(file);

            showMessage("command.save.confirm", new Object[0]);
        } catch (Exception ex) {
            showMessage("save.exception", new String[] { file.getName() });
        }
    }

    protected void showElementStatus(Element element) {

        List problems = organPlay.getProblems(element);
        if (problems != null) {
            for (int p = 0; p < problems.size(); p++) {
                Problem problem = (Problem) problems.get(p);

                String pattern = "play." + problem;

                showMessage(pattern, new Object[] { element.getName(),
                        problem.getValue() });
            }
        }
    }

    /**
     * Show a message.
     * 
     * @param pattern
     *            pattern of message to show
     * @param args
     *            arguments to message
     */
    protected void showMessage(String pattern, Object[] args) {

        String text = MessageFormat.format(resources.getString(pattern), args);

        interpreter.writeln(text);
    }

    /**
     * The command for opening of a disposition.
     */
    private class OpenCommand extends AbstractCommand {
        public String getPrefix() {
            return "command.open";
        }

        public void execute(String param) {
            if (param == null) {
                showMessage("command.open.format", new Object[0]);
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
            return "command.close";
        }

        public void execute(String param) {
            if (file == null) {
                showMessage("command.close.file", new Object[0]);
                return;
            }
            file = null;
            setOrgan(null);

            showMessage("command.close.confirm", new Object[0]);
        }
    }

    /**
     * The command to save the current disposition.
     */
    private class SaveCommand extends AbstractCommand {
        public String getPrefix() {
            return "command.save";
        }

        public void execute(String param) {
            if (param != null) {
                showMessage("command.save.format", new Object[0]);
                return;
            }
            if (file == null) {
                showMessage("command.save.file", new Object[0]);
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
            return "command.help";
        }

        public void execute(String param) {
            if (param != null) {
                Command command = interpreter.getCommand(param);
                interpreter.writeln(command.getLongDescription());
            } else {
                showMessage("command.help.header", new Object[0]);
                int length = 0;
                for (int c = 0; c < interpreter.getCommandCount(); c++) {
                    Command command = interpreter.getCommand(c);
                    length = Math.max(length, command.getName().length());
                }
                for (int c = 0; c < interpreter.getCommandCount(); c++) {
                    Command command = interpreter.getCommand(c);
                    String name = pad(command.getName(), length);
                    showMessage("command.help.list", new Object[] { name,
                            command.getDescription() });
                }
                showMessage("command.help.footer", new Object[0]);
            }
        }
    }

    /**
     * The command to show/change current encoding.
     */
    private class EncodingCommand extends AbstractCommand {
        public String getPrefix() {
            return "command.encoding";
        }

        public void execute(String param) {
            String defaultEncoding = System.getProperty("file.encoding");

            if (param != null) {
                try {
                    interpreter.setEncoding(param);

                    if (defaultEncoding.equalsIgnoreCase(param)) {
                        Configuration.instance().setUseDefaultEncoding(true);
                    } else {
                        Configuration.instance().setEncoding(param);
                        Configuration.instance().setUseDefaultEncoding(false);
                    }
                    Configuration.instance().backup();
                } catch (UnsupportedEncodingException ex) {
                    showMessage("command.encoding.unsupported",
                            new Object[] { param });
                    return;
                }
            }
            if (Configuration.instance().getUseDefaultEncoding()) {
                showMessage("command.encoding.default",
                        new Object[] { defaultEncoding });
            } else {
                showMessage("command.encoding.current",
                        new Object[] { Configuration.instance().getEncoding() });
            }
        }
    }

    /**
     * The command to show recent dispositions and opening a recent disposition.
     */
    private class RecentCommand extends AbstractCommand {
        public String getPrefix() {
            return "command.recent";
        }

        public void execute(String param) {
            List recents = jorgan.io.Configuration.instance().getRecentFiles();
            if (recents.size() == 0) {
                showMessage("command.recent.none", new Object[0]);
                return;
            }

            if (param == null) {
                showMessage("command.recent.header", new Object[0]);

                for (int r = 0; r < recents.size(); r++) {
                    File recent = (File) recents.get(r);
                    showMessage("command.recent.list", new Object[] {
                            new Integer(r + 1), recent });
                }
            } else {
                try {
                    int index = Integer.parseInt(param) - 1;

                    openOrgan((File) recents.get(index));
                } catch (Exception ex) {
                    Integer from = new Integer(1);
                    Integer to = new Integer(recents.size());
                    showMessage("command.recent.format", new Object[] { from,
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
            return "command.exit";
        }

        public void execute(String param) {
            if (param != null) {
                showMessage("command.exit.format", new Object[0]);
                return;
            }

            if (organPlay != null) {
                organPlay.close();
                organPlay.dispose();
                organPlay = null;
            }

            showMessage("command.exit.confirm", new Object[0]);

            interpreter.stop();
        }
    }

    /**
     * The command used in case of a interpretation of an unknown command.
     */
    private class UnknownCommand implements Command {
        public String getName() {
            return resources.getString("command.unknown");
        }

        public String getDescription() {
            return resources.getString("command.unknown");
        }

        public String getLongDescription() {
            return resources.getString("command.unknown");
        }

        public void execute(String param) {
            showMessage("command.unknown", new Object[0]);
        }
    }

    /**
     * The abstract base class for all commands of this organ shell.
     */
    private abstract class AbstractCommand implements Command {
        protected abstract String getPrefix();

        public String getName() {
            return MessageFormat.format(resources.getString(getPrefix()
                    + ".name"), new Object[0]);
        }

        public String getDescription() {
            return MessageFormat.format(resources.getString(getPrefix()
                    + ".description"), new Object[0]);
        }

        public String getLongDescription() {
            return MessageFormat.format(resources.getString(getPrefix()
                    + ".longDescription"), new Object[0]);
        }

        public abstract void execute(String param);
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
}