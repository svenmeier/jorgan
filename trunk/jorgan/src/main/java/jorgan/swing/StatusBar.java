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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Bar for displaying status information.
 */
public class StatusBar extends JPanel {

  private Insets insets = new Insets(1, 1, 1, 1);
  
  private GridBagConstraints constraints = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0, 0.0
                                                                 ,GridBagConstraints.CENTER, GridBagConstraints.BOTH
                                                                 ,insets, 0, 0);
  
  private JLabel label = new JLabel();

  public StatusBar() {
    setLayout(new GridBagLayout());

    setBorder(new EmptyBorder(0, 2, 0, 2));

    label.setText(" ");
    add(label, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
  }

  public void addStatus(JComponent status) {
      
    JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
    status.putClientProperty(this, separator);
    
    this.add(separator, constraints);
    this.add(status, constraints);
            
    repaint();
    revalidate();
  }

  public void removeStatus(JComponent status) {
    this.remove(status);
    
    this.remove((JSeparator)status.getClientProperty(this));
    
    repaint();
    revalidate();
  }

  public String getStatus() {
    return label.getText();
  }

  public void setStatus(String status) {
    setStatus(status, null);
  }

  public void setStatus(String status, Icon icon) {
    if (status == null || "".equals(status)) {
      label.setText(" ");
    } else {
      label.setText(status);
    }
    label.setIcon(icon);    
  }
}