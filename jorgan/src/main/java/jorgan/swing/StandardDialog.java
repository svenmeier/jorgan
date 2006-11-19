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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.CompoundBorder;

import jorgan.swing.border.RuleBorder;
import jorgan.swing.text.MultiLineLabel;

/**
 * A standard dialog.
 */
public class StandardDialog extends JDialog {

	/**
	 * The resource bundle.
	 */
	private static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.swing.resources");

	/**
	 * The label holding the description.
	 */
	private MultiLineLabel descriptionLabel = new MultiLineLabel(2);

	/**
	 * The panel holding the current content.
	 */
	private ContentPane contentPane = new ContentPane();

	/**
	 * The panel holding the buttons.
	 */
	private ButtonPane buttonPane = new ButtonPane();

	private boolean cancelled = false;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 */
	public StandardDialog(Frame owner) {
		super(owner, true);

		setContentPane(contentPane);

		descriptionLabel.setBackground(Color.white);
		descriptionLabel.setBorder(new CompoundBorder(new RuleBorder(
				RuleBorder.BOTTOM), BorderFactory.createEmptyBorder(10, 10, 10,
				10)));
		descriptionLabel.setVisible(false);
		contentPane.add(descriptionLabel, BorderLayout.NORTH);

		contentPane.add(buttonPane, BorderLayout.SOUTH);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent ev) {
				onCancel();
			}
		});
	}

	/**
	 * Set the content. Only one content can be displayed.
	 * 
	 * @param content
	 *            content to be displayed
	 */
	public void setContent(JComponent content) {
		contentPane.setContent(content);
	}

	/**
	 * Get the current content.
	 * 
	 * @return the content
	 */
	public JComponent getContent() {
		return contentPane.getContent();
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
		addAction(new OKAction(), true);
	}

	/**
	 * Add an action for cancel.
	 */
	public void addCancelAction() {
		addAction(new CancelAction(), false);
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
	}

	/**
	 * Start with the preferred size.
	 */
	public void start() {
		Dimension dim = getPreferredSize();
		start(dim.width, dim.height);
	}

	/**
	 * Start with the given size.
	 * 
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 */
	public void start(int width, int height) {
		setSize(width, height);
		setLocationRelativeTo(getOwner());
		setVisible(true);
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

	/**
	 * The content panel.
	 */
	private class ContentPane extends JPanel {

		private ComponentListener listener = new Listener();

		private JComponent borderPane = new JPanel();

		private JComponent content;

		private ContentPane() {
			setLayout(new BorderLayout());

			borderPane.setBorder(BorderFactory
					.createEmptyBorder(10, 10, 10, 10));
			borderPane.setLayout(new BorderLayout());
			add(borderPane, BorderLayout.CENTER);

			getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
			getActionMap().put("CANCEL", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onCancel();
				}
			});
		}

		private JComponent getContent() {
			return content;
		}

		private void setContent(JComponent content) {
			if (this.content != null) {
				borderPane.remove(this.content);
				borderPane.revalidate();
				borderPane.repaint();
			}

			this.content = content;

			if (this.content != null) {
				borderPane.add(content, BorderLayout.CENTER);
				borderPane.revalidate();
				borderPane.repaint();
			}
		}

		/**
		 * Add componentListener to container.
		 */
		public void addNotify() {
			super.addNotify();

			Container container = getTopLevelAncestor();
			container.addComponentListener(listener);
		}

		/**
		 * Remove componentListener from container.
		 */
		public void removeNotify() {
			super.removeNotify();

			Container container = getTopLevelAncestor();

			container.removeComponentListener(listener);
		}

		/**
		 * DoLayout overriden for check of minimum size.
		 */
		public void doLayout() {
			super.doLayout();

			checkMinimumSize();
		}

		/**
		 * Check the minimum size.
		 */
		protected void checkMinimumSize() {
			Container container = getTopLevelAncestor();

			Dimension minimumSize = container.getMinimumSize();
			Dimension size = container.getSize();
			if (size.width < minimumSize.width
					|| size.height < minimumSize.height) {
				Dimension newSize = new Dimension(Math.max(minimumSize.width,
						size.width), Math.max(minimumSize.height, size.height));
				container.setSize(newSize);
			}
		}

		/**
		 * ComponentListener.
		 */
		private class Listener extends ComponentAdapter {
			public void componentResized(ComponentEvent e) {
				checkMinimumSize();
			}
		}
	}

	/**
	 * The panel for the buttons.
	 */
	private class ButtonPane extends JPanel {

		private JPanel gridPanel = new JPanel(new GridLayout(1, 0, 2, 2));

		private ButtonPane() {
			setLayout(new BorderLayout());
			setBorder(new RuleBorder(RuleBorder.TOP));

			gridPanel
					.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			add(gridPanel, BorderLayout.EAST);
		}

		private JButton add(Action action) {
			JButton button = new JButton(action);

			gridPanel.add(button);
			gridPanel.revalidate();
			gridPanel.repaint();

			return button;
		}
	}

	private class CancelAction extends AbstractAction {

		private CancelAction() {
			putValue(Action.NAME, resources.getString("dialog.cancel"));
		}

		public void actionPerformed(ActionEvent ev) {
			onCancel();
		}
	}

	private class OKAction extends AbstractAction {

		private OKAction() {
			putValue(Action.NAME, resources.getString("dialog.ok"));
		}

		public void actionPerformed(ActionEvent ev) {
			onOK();
		}
	}
}