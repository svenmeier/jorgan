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
package jorgan.gui.midi;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class MidiFilterPanel extends JPanel {
      
    private Insets standardInsets = new Insets(0,0,0,0);
      
    private JList inList = new JList();
    private JList outList = new JList();
    
    public MidiFilterPanel() {
      super(new GridBagLayout());
      
      JLabel inLabel = new JLabel();
      inLabel.setText(MidiLog.resources.getString("log.input"));
      add(inLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));
      add(new JScrollPane(inList), new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));
      
      JLabel outLabel = new JLabel();
      outLabel.setText(MidiLog.resources.getString("log.output"));
      add(outLabel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));
      add(new JScrollPane(outList), new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));
    }
  }