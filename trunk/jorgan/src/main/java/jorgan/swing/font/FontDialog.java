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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * A dialog for a font selection.
 */
class FontDialog extends JDialog {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.swing.resources");

  private JPanel borderPanel = new JPanel();
  private FontPanel fontPanel = new FontPanel();
  private JPanel buttonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();

  private Font font;

  public FontDialog(JFrame owner) {
    super(owner, true);

    init();
  }

  public FontDialog(JDialog owner) {
    super(owner, true);

    init();
  }

  private void init() {

    setTitle(resources.getString("font.title"));

    borderPanel.setLayout(new BorderLayout(10, 10));
    borderPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    getContentPane().add(borderPanel, BorderLayout.CENTER);

    borderPanel.add(fontPanel, BorderLayout.CENTER);

    buttonPanel.setLayout(new BorderLayout());
    borderPanel.add(buttonPanel, BorderLayout.SOUTH);

      JPanel gridPanel = new JPanel(new GridLayout(1, 0, 2, 2));
      buttonPanel.add(gridPanel, BorderLayout.EAST);

        okButton.setText(resources.getString("font.ok"));
        okButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
            font = fontPanel.getSelectedFont();
            setVisible(false);
          }
        });
        getRootPane().setDefaultButton(okButton);
        gridPanel.add(okButton);

        cancelButton.setText(resources.getString("font.cancel"));
        cancelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ev) {
            setVisible(false);
          }
        });
        gridPanel.add(cancelButton);
  }

  public void start() {
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    pack();
    setLocationRelativeTo(getOwner());
    setVisible(true);
  }

  public void setSelectedFont(Font font) {
    this.font = font;

    fontPanel.setSelectedFont(font);
  }

  public Font getSelectedFont() {
    return font;
  }
}