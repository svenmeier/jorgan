package jorgan.customizer.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import jorgan.gui.OrganFrame;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.swing.BaseAction;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * The action that starts the devices configuration wizard.
 */
public class CustomizeAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			CustomizeAction.class);

	public static final int ERROR_OFFER = 0;

	public static final int ERROR_CUSTOMIZE = 1;

	public static final int ERROR_IGNORE = 2;

	private int handleErrors = ERROR_OFFER;

	private OrganSession session;

	private OrganFrame frame;

	private EventListener eventListener = new EventListener();

	public CustomizeAction(OrganSession session, OrganFrame frame) {
		this.session = session;
		this.frame = frame;
		
		session.addListener(eventListener);

		config.read(this);

		if (frame.isDisplayable()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					check();
				}
			});
		}
	}

	public void setHandleErrors(int handleErrors) {
		this.handleErrors = handleErrors;
	}

	public int getHandleErrors() {
		return handleErrors;
	}

	/**
	 * Offer customization.
	 * 
	 * @param owner
	 *            the owning component
	 * @return <code>true</code> if offer of customizazion is accepted
	 */
	public boolean offer(Component owner) {
		boolean result = false;

		if (handleErrors == ERROR_CUSTOMIZE) {
			result = true;
		} else if (handleErrors == ERROR_IGNORE) {
			result = false;
		} else if (handleErrors == ERROR_OFFER) {
			MessageBox box = new MessageBox(MessageBox.OPTIONS_YES_NO);
			config.get("confirm").read(box);

			JCheckBox rememberCheckBox = new JCheckBox("");
			config.get("confirm/remember").read(rememberCheckBox);
			box.setComponents(rememberCheckBox);

			result = (box.show(owner) == MessageBox.OPTION_YES);

			if (rememberCheckBox.isSelected()) {
				if (result) {
					handleErrors = ERROR_CUSTOMIZE;
				} else {
					handleErrors = ERROR_IGNORE;
				}
			}
		}

		return result;
	}

	public void actionPerformed(ActionEvent ev) {
		CustomizeWizard.showInDialog((JComponent) ev.getSource(), session);
	}

	private void check() {
		if (!session.isConstructing() && session.getProblems().hasErrors()) {
			if (offer(frame)) {
				CustomizeWizard.showInDialog(frame, session);
			}
			config.write(this);
		}
	}

	private class EventListener implements SessionListener {
		public void constructingChanged(boolean constructing) {
		}

		public void destroyed() {
			frame = null;
		}
	}
}