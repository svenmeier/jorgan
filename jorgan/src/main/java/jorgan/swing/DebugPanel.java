package jorgan.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A panel showing debug information based on logger output.
 */
public class DebugPanel extends JPanel {

	private JTextArea textArea = new JTextArea();

	private Logger logger;

	private Handler handler = new InternalHandler();

	/**
	 * Constructor.
	 */
	public DebugPanel() {
		super(new BorderLayout());

		handler.setFormatter(new SimpleFormatter());

		textArea.setEditable(false);

		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(400, 400));
		add(scrollPane, BorderLayout.CENTER);

		setLogger(Logger.getLogger(""));
	}

	/**
	 * Set the logger.
	 * 
	 * @param logger
	 *            logger
	 */
	public void setLogger(Logger logger) {
		if (this.logger != null) {
			this.logger.removeHandler(handler);
		}

		this.logger = logger;

		if (this.logger != null) {
			this.logger.addHandler(handler);
		}
	}

	private class InternalHandler extends Handler {
		public void flush() {
		}

		public void publish(LogRecord record) {
			String msg = getFormatter().format(record);

			textArea.append(msg);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		}

		public void close() throws SecurityException {
		}
	}

	private class ClearAction extends BaseAction {
		private ClearAction() {
			putValue(AbstractAction.NAME, "Clear");
		}

		public void actionPerformed(ActionEvent e) {
			textArea.setText("");
		}
	}

	private class CopyAction extends BaseAction {
		private CopyAction() {
			putValue(AbstractAction.NAME, "Copy to Clipboard");
		}

		public void actionPerformed(ActionEvent e) {
			String string = textArea.getSelectedText();
			if (string == null) {
				string = textArea.getText();
			}
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					new StringSelection(string), null);
		}
	}

	/**
	 * Show in a dialog.
	 * 
	 * @param owner
	 *            owner of dialog
	 */
	public void showInDialog(JFrame owner) {
		StandardDialog dialog = new StandardDialog(owner, false);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.setBody(this);
		dialog.addAction(new ClearAction());
		dialog.addAction(new CopyAction());

		dialog.autoPosition();
		dialog.setVisible(true);
	}
}
