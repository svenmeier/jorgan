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
package jorgan.swing.font;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import jorgan.util.I18N;

/**
 * A dialog for a font selection.
 */
class FontDialog extends JDialog {

	private static I18N i18n = I18N.get(FontDialog.class);

	private JPanel borderPanel = new JPanel();

	private FontPanel fontPanel = new FontPanel();

	private JPanel buttonPanel = new JPanel();

	private JButton okButton = new JButton();

	private JButton cancelButton = new JButton();

	private Font font;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 */
	public FontDialog(JFrame owner) {
		super(owner, true);

		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 */
	public FontDialog(JDialog owner) {
		super(owner, true);

		init();
	}

	private void init() {
		setTitle(i18n.getString("title"));

		borderPanel.setLayout(new BorderLayout(10, 10));
		borderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getContentPane().add(borderPanel, BorderLayout.CENTER);

		borderPanel.add(fontPanel, BorderLayout.CENTER);

		buttonPanel.setLayout(new BorderLayout());
		borderPanel.add(buttonPanel, BorderLayout.SOUTH);

		JPanel gridPanel = new JPanel(new GridLayout(1, 0, 2, 2));
		buttonPanel.add(gridPanel, BorderLayout.EAST);

		okButton.setText(i18n.getString("okButton.text"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				font = fontPanel.getSelectedFont();
				setVisible(false);
			}
		});
		getRootPane().setDefaultButton(okButton);
		gridPanel.add(okButton);

		cancelButton.setText(i18n.getString("cancelButton.text"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				setVisible(false);
			}
		});
		gridPanel.add(cancelButton);
	}

	/**
	 * Start.
	 */
	public void start() {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(getOwner());
		setVisible(true);
	}

	/**
	 * Set the selected font.
	 * 
	 * @param font
	 *            the font to select
	 */
	public void setSelectedFont(Font font) {
		this.font = font;

		fontPanel.setSelectedFont(font);
	}

	/**
	 * Get the selected font.
	 * 
	 * @return the selected font
	 */
	public Font getSelectedFont() {
		return font;
	}
}