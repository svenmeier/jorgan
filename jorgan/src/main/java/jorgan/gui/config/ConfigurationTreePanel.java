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
package jorgan.gui.config;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

import jorgan.swing.CardPanel;
import jorgan.swing.Header;

import jorgan.config.AbstractConfiguration;

/**
 * A panel for editing of a tree of configurations.
 */
public class ConfigurationTreePanel extends JPanel {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private JTree tree = new JTree();
  private JPanel contentPanel = new JPanel();
  private CardPanel cardPanel = new CardPanel();
  private Header header = new Header();
  private JPanel buttonPanel = new JPanel();

  private Action resetAction = new ResetAction();

  private AbstractConfiguration configuration;

  /**
   * Constructor.
   */
  public ConfigurationTreePanel() {
    super(new BorderLayout(10, 10));

    tree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    tree.setShowsRootHandles(true);
    tree.setModel(new ConfigurationTreeModel());
    tree.setCellRenderer(new ConfigurationRenderer());
    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent ev) {
        AbstractConfiguration config;

        TreePath path = tree.getSelectionPath();
        if (path == null) {
          config = configuration;
        } else {
          config = (AbstractConfiguration)path.getLastPathComponent();
        }

        showConfiguration(config);
      }
    });
    add(new JScrollPane(tree), BorderLayout.WEST);

    contentPanel.setLayout(new BorderLayout(4, 4));
    add(contentPanel, BorderLayout.CENTER);

      header.setHeader(Color.white);
      contentPanel.add(header, BorderLayout.NORTH);

      contentPanel.add(cardPanel, BorderLayout.CENTER);

        cardPanel.addCard(new JOrganConfigPanel()   , jorgan.Configuration.class);
        cardPanel.addCard(new GUIConfigPanel()    , jorgan.gui.Configuration.class);
        cardPanel.addCard(new GUIConsoleConfigPanel()     , jorgan.gui.console.Configuration.class);
        cardPanel.addCard(new GUIConstructConfigPanel(), jorgan.gui.construct.Configuration.class);
        cardPanel.addCard(new MidiLogConfigPanel(), jorgan.midi.log.Configuration.class);
        cardPanel.addCard(new MidiConfigPanel()     , jorgan.midi.Configuration.class);
        cardPanel.addCard(new MidiMergeConfigPanel(), jorgan.midi.merge.Configuration.class);
        cardPanel.addCard(new PlayConfigPanel()     , jorgan.play.Configuration.class);
        cardPanel.addCard(new IOConfigPanel()       , jorgan.io.Configuration.class);
        cardPanel.addCard(new ShellConfigPanel()    , jorgan.shell.Configuration.class);

      buttonPanel.setLayout(new BorderLayout());
      buttonPanel.add(new JButton(resetAction), BorderLayout.EAST);
      contentPanel.add(buttonPanel, BorderLayout.SOUTH);
  }

  public void write() {
    for (int c = 0; c < cardPanel.getComponentCount(); c++) {
      ConfigurationPanel panel = (ConfigurationPanel)cardPanel.getComponent(c);
      if (panel.getConfiguration() != null) {
        panel.write();
      }
    }
  }

  public void setConfiguration(AbstractConfiguration configuration, boolean showRoot) {
    this.configuration = configuration;

    tree.setRootVisible(showRoot);
    tree.setModel(new ConfigurationTreeModel());

    showConfiguration(configuration);
  }

  public AbstractConfiguration getConfiguration() {
    return configuration;
  }

  protected void showConfiguration(AbstractConfiguration config) {
    ConfigurationPanel panel = (ConfigurationPanel)cardPanel.getCard(config.getClass());
    if (panel != null) {
      if (panel.getConfiguration() == null) {
        panel.setConfiguration(config);
        panel.read();
      }
      
      cardPanel.selectCard(config.getClass());
      header.setText(panel.getName());
    }
  }

  private class ResetAction extends AbstractAction {

    public ResetAction() {
      putValue(Action.NAME, resources.getString("config.reset"));
    }

    public void actionPerformed(ActionEvent ev) {
      ConfigurationPanel configPanel = (ConfigurationPanel)cardPanel.getSelectedCard();
      if (configPanel != null) {
        configPanel.getConfiguration().reset();
        configPanel.read();
      }
    }
  }

  private class ConfigurationTreeModel implements TreeModel {

    protected AbstractConfiguration cast(Object object) {
      return (AbstractConfiguration)object;
    }

    public Object getRoot() {
      return configuration;
    }

    public Object getChild(Object parent, int index) {
      return cast(parent).getChild(index);
    }

    public int getChildCount(Object parent) {
      return cast(parent).getChildCount();
    }

    public boolean isLeaf(Object node) {
      return cast(node).getChildCount() == 0;
    }

    public void valueForPathChanged(TreePath path, Object newValue) {}

    public int getIndexOfChild(Object parent, Object child) {
      return cast(parent).getChildIndex(cast(child));
    }

    public void addTreeModelListener(TreeModelListener l) {}

    public void removeTreeModelListener(TreeModelListener l) {}
  }

  private class ConfigurationRenderer extends DefaultTreeCellRenderer {

    public ConfigurationRenderer() {
      setOpenIcon(null);
      setClosedIcon(null);
      setLeafIcon(null);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
      String text = "???";
      JPanel panel = (JPanel)cardPanel.getCard(value.getClass());
      if (panel != null) {
        text = panel.getName();
      }

      Component component = super.getTreeCellRendererComponent(tree, text, selected, expanded, leaf, row, hasFocus);

      return component;
    }
  }
}