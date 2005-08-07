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
import java.awt.event.*;
import java.util.ResourceBundle;

import javax.swing.*;

import jorgan.swing.border.*;

/**
 * A standard dialog.
 */
public class StandardDialog extends JDialog {

  /**
   * The resource bundle.
   */
  private static ResourceBundle resources = ResourceBundle.getBundle("jorgan.swing.resources");
  
  /**
   * The panel holding the description of the content.
   */
  private DescriptionPane descriptionPane = new DescriptionPane();
  
  /**
   * The panel holding the current content.
   */
  private ContentPane contentPane = new ContentPane();
  
  /**
   * The panel holding the buttons.
   */
  private ButtonPane buttonPane = new ButtonPane();
  
  private boolean canceled = false;

  /**
   * Constructor.
   * 
   * @param owner   the owner of this dialog
   */
  public StandardDialog(Frame owner) {
    super(owner, true);

    setContentPane(contentPane);
    
    contentPane.add(descriptionPane, BorderLayout.NORTH);

    contentPane.add(buttonPane, BorderLayout.SOUTH);

    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent ev) {
        cancel();
      }
    });
  }

  /**
   * Set the content. Only one content can be displayed.
   * 
   * @param content content to be displayed
   */
  public void setContent(JComponent content) {
    contentPane.setContent(content);
  }
  
  /**
   * Get the current content.
   * 
   * @return    the content
   */
  public JComponent getContent() {
    return contentPane.getContent();
  }
  
  /**
   * Set the description.
   * 
   * @param description   the description
   */
  public void setDescription(String description) {
    descriptionPane.setDescription(description);
  }
  
  /**
   * Get the current description.
   * 
   * @return    the description
   */
  public String getDescription() {
    return descriptionPane.getDescription();
  }
  
  /**
   * Add an action for OK.
   */
  public void addOKAction() {
    addAction(new OKAction(), false);    
  }

  /**
   * Add an action for cancel.
   */
  public void addCancelAction() {
    addAction(new CancelAction(), false);    
  }

  /**
   * Add an action for OK.
   * 
   * @param isDefault  should the button of the action be the
   *                   default action
   */
  public void addOKAction(boolean isDefault) {
    addAction(new OKAction(), isDefault);    
  }

  /**
   * Add an action for cancel.
   * 
   * @param isDefault  should the button of the action be the
   *                   default action
   */
  public void addCancelAction(boolean isDefault) {
    addAction(new CancelAction(), isDefault);    
  }

  /**
   * Add an action.
   * 
   * @param action    action to add
   */
  public void addAction(Action action) {
    addAction(action, false);    
  }
  
  /**
   * Add an action and optionally set its button as the default button of
   * this dialog.
   *  
   * @param action     action to add
   * @param isDefault  should the button of the action be the
   *                   default action
   */
  public void addAction(Action action, boolean isDefault) {
    JButton button = buttonPane.add(action);
    
    if (isDefault) {    
      getRootPane().setDefaultButton(button);
    }
  }

  public void start() {
    pack();
    setLocationRelativeTo(getOwner());
    setVisible(true);
  }

  public void cancel() {
    canceled = true;
    
    setVisible(false);
  }

  public void ok() {
    canceled = false;
      
    setVisible(false);
  }

  public boolean wasCanceled() {
    return canceled;
  }
  
  /**
   * The content panel.
   */
  private class ContentPane extends JPanel {

    private ComponentListener listener = new Listener();
    
    private JComponent borderPane = new JPanel();
    private JComponent content;
    
    public ContentPane() {
      setLayout(new BorderLayout());
      
      borderPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      borderPane.setLayout(new BorderLayout());
      add(borderPane, BorderLayout.CENTER);
      
      getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CANCEL");
      getActionMap().put("CANCEL", new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
          cancel();
        }
      });
    }
    
    public JComponent getContent() {
      return content;
    }

    public void setContent(JComponent content) {
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
      Dimension size        = container.getSize();
      if (size.width < minimumSize.width || size.height < minimumSize.height) {
        Dimension newSize = new Dimension(Math.max(minimumSize.width,  size.width),
                                          Math.max(minimumSize.height, size.height));
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

    public ButtonPane() {
      setLayout(new BorderLayout());
      setBorder(new RuleBorder(RuleBorder.TOP));

      gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      add(gridPanel, BorderLayout.EAST);
    }
    
    public JButton add(Action action) {
      JButton button = new JButton(action);
      
      gridPanel.add(button);
      gridPanel.revalidate();
      gridPanel.repaint();
      
      return button;
    }
  }
  
  /**
   * The panel for the description.
   */
  private class DescriptionPane extends JPanel {

    private JTextArea textArea = new JTextArea(2, 0);

    public DescriptionPane() {
      setLayout(new BorderLayout());
      setBorder(new RuleBorder(RuleBorder.BOTTOM));

      textArea.setFont(new JTextField().getFont());
      textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.setEnabled(false);
      textArea.setDisabledTextColor(Color.black);
      textArea.setBackground(Color.white);
      add(textArea, BorderLayout.CENTER);

      setVisible(false); 
    }
       
    public String getDescription() {
      return textArea.getText();
    }

    public void setDescription(String description) {
      textArea.setText(description);
      
      if (description == null) {
        setVisible(false); 
      } else {
        setVisible(true); 
      }
    }
  }
  
  private class CancelAction extends AbstractAction {

    public CancelAction() {
      putValue(Action.NAME, resources.getString("dialog.cancel"));
    }

    public void actionPerformed(ActionEvent ev) {
      cancel();
    }
  }
  
  private class OKAction extends AbstractAction {

    public OKAction() {
      putValue(Action.NAME, resources.getString("dialog.ok"));
    }

    public void actionPerformed(ActionEvent ev) {
      ok();
    }
  }    
}