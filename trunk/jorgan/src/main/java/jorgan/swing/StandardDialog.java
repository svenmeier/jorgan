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
package jorgan.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;

import jorgan.swing.border.RuleBorder;
import jorgan.swing.text.MultiLineLabel;
import bias.Configuration;

/**
 * A standard dialog.
 */
public class StandardDialog extends JDialog {

	private static Configuration config = Configuration.getRoot().get(
			StandardDialog.class);

	/**
	 * The label holding the description.
	 */
	private MultiLineLabel descriptionLabel = new MultiLineLabel(2);

	/**
	 * The panel wrapping the current body.
	 */
	private JPanel bodyWrapper = new JPanel();

	private JComponent body;

	/**
	 * The panel holding the buttons.
	 */
	private ButtonPane buttonPane = new ButtonPane();

	private boolean cancelled = false;

	/**
	 * Constructor for modal dialogs.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 */
	public StandardDialog(JDialog owner) {
		this(owner, true);
	}

	/**
	 * Constructor for modal dialogs.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 * @param modal
	 *            modal
	 */
	public StandardDialog(JDialog owner, boolean modal) {
		super(owner, true);

		init();
	}

	/**
	 * Constructor for modal dialogs.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 */
	public StandardDialog(JFrame owner) {
		this(owner, true);
	}

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 * @param modal
	 *            modal
	 */
	public StandardDialog(JFrame owner, boolean modal) {
		super(owner, modal);

		init();
	}

	private void init() {

		JPanel contentPane = new JPanel(new BorderLayout());
		CancelAction cancel = new CancelAction();
		contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancel);
		contentPane.getActionMap().put(cancel, cancel);
		setContentPane(contentPane);

		descriptionLabel.setBackground(Color.white);
		descriptionLabel.setBorder(new CompoundBorder(new RuleBorder(
				RuleBorder.BOTTOM), BorderFactory.createEmptyBorder(10, 10, 10,
				10)));
		descriptionLabel.setVisible(false);
		contentPane.add(descriptionLabel, BorderLayout.NORTH);

		bodyWrapper.setLayout(new BorderLayout());
		contentPane.add(bodyWrapper, BorderLayout.CENTER);

		contentPane.add(buttonPane, BorderLayout.SOUTH);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent ev) {
				onCancel();
			}
		});
	}

	/**
	 * Set the body. Only one body can be displayed.
	 * 
	 * @param body
	 *            body to be displayed
	 */
	public void setBody(JComponent body) {
		if (this.body != null) {
			bodyWrapper.remove(this.body);
			bodyWrapper.revalidate();
			bodyWrapper.repaint();
		}

		this.body = body;

		if (this.body != null) {
			bodyWrapper.add(body, BorderLayout.CENTER);
			bodyWrapper.revalidate();
			bodyWrapper.repaint();
		}

		guaranteePreferredSize();
	}

	/**
	 * Get the current body.
	 * 
	 * @return the body
	 */
	public JComponent getBody() {
		return body;
	}

	/**
	 * Set the description.
	 * 
	 * @param description
	 *            the description
	 */
	public void setDescription(String description) {
		descriptionLabel.setText(description);

		descriptionLabel.setVisible(description != null);
	}

	/**
	 * Get the current description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return descriptionLabel.getText();
	}

	/**
	 * Add an action for OK.
	 */
	public void addOKAction() {
		OKAction okAction = new OKAction();
		config.get("ok").read(okAction);
		addAction(okAction, true);
	}

	/**
	 * Add an action for cancel.
	 */
	public void addCancelAction() {
		CancelAction cancelAction = new CancelAction();
		config.get("cancel").read(cancelAction);
		addAction(cancelAction, false);
	}

	/**
	 * Add an action.
	 * 
	 * @param action
	 *            action to add
	 */
	public void addAction(Action action) {
		addAction(action, false);
	}

	/**
	 * Add an action and optionally set its button as the default button of this
	 * dialog.
	 * 
	 * @param action
	 *            action to add
	 * @param isDefault
	 *            should the button of the action be the default action
	 */
	public void addAction(Action action, boolean isDefault) {
		JButton button = buttonPane.add(action);

		if (isDefault) {
			getRootPane().setDefaultButton(button);
		}

		bodyWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	/**
	 * Position automatically.
	 */
	public void autoPosition() {
		Dimension dim = getPreferredSize();

		setSize(dim.width, dim.height);

		setLocationRelativeTo(getOwner());
	}

	/**
	 * Invoked when the cancel action was choosen.
	 * 
	 * @see #addCancelAction()
	 */
	public void onCancel() {
		cancelled = true;

		setVisible(false);
	}

	/**
	 * Invoked when the ok action was choosen.
	 * 
	 * @see #addOKAction()
	 */
	public void onOK() {
		cancelled = false;

		setVisible(false);
	}

	/**
	 * Was this dialog cancelled.
	 * 
	 * @return <code>true</code> if cancelled
	 * @see #onCancel()
	 */
	public boolean wasCancelled() {
		return cancelled;
	}

	@Override
	public void setBounds(Rectangle bounds) {
		if (bounds == null) {
			pack();
			setLocationRelativeTo(getOwner());
		} else {
			super.setBounds(bounds);
		}
	}

	/**
	 * Guarantee that the size of this dialog is greater than the preferred
	 * size.
	 */
	private void guaranteePreferredSize() {
		Dimension preferredSize = getPreferredSize();
		Dimension size = getSize();

		setSize(Math.max(preferredSize.width, size.width), Math.max(
				preferredSize.height, size.height));
		validate();
	}

	/**
	 * The panel for the buttons.
	 */
	private class ButtonPane extends JPanel {

		private JPanel gridPanel = new JPanel(new GridLayout(1, 0, 2, 2));

		private ButtonPane() {
			setLayout(new BorderLayout());
			setBorder(new RuleBorder(RuleBorder.TOP));
			setVisible(false);

			gridPanel
					.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			add(gridPanel, BorderLayout.EAST);
		}

		private JButton add(Action action) {
			setVisible(true);

			JButton button = new JButton(action);

			gridPanel.add(button);
			gridPanel.revalidate();
			gridPanel.repaint();

			return button;
		}
	}

	private class CancelAction extends BaseAction {

		public void actionPerformed(ActionEvent ev) {
			onCancel();
		}
	}

	private class OKAction extends BaseAction {

		public void actionPerformed(ActionEvent ev) {
			onOK();
		}
	}

	/**
	 * Utility method to get the containing window of a component.
	 * 
	 * @param component
	 *            the component to get window for
	 * @return containing window
	 */
	public static Window getWindow(Component component) {
		if (component instanceof Window) {
			return (Window) component;
		}

		return SwingUtilities.getWindowAncestor(component);
	}

	public static StandardDialog create(Component owner) {
		Window window = getWindow(owner);
		if (window instanceof JDialog) {
			return new StandardDialog((JDialog) window);
		} else {
			return new StandardDialog((JFrame) window);
		}
	}
}