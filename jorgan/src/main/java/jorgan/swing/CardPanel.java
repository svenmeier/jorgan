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

import java.util.*;
import java.awt.*;
import javax.swing.*;

/**
 * A simpler alternative to a JPanel with a CardLayout.
 */
public class CardPanel extends JPanel {

  private Layout layout = new Layout();

  /**
   * Constructor.
   */
  public CardPanel() {
    setLayout(layout);
  }

  /**
   * Return the selected card.
   *
   * @return    selected card
   */
  public Component getSelectedCard() {

    return layout.getSelectedCard();
  }

  /**
   * Select the card with the specified constraint.
   *
   * @param constraint   constraint of card to select
   * @return             the component with the specified constraint
   */
  public Component selectCard(Object constraint) {

    Component component = layout.selectCard(constraint);

    revalidate();
    repaint();

    return component;
  }

  /**
   * Get the card with the specified constraint.
   *
   * @param constraint   constraint of card to get
   */
  public Component getCard(Object constraint) {

    return layout.getCard(constraint);
  }

  /**
   * Add a card.
   *
   * @param card         card to add
   * @param constraint   constraint of card to add
   */
  public void addCard(Component card, Object constraint) {

    this.add(card, constraint);
    
    revalidate();
    repaint();
  }

  /**
   * Layout.
   */
  private static class Layout implements LayoutManager2 {

    private Map       cards = new HashMap();
    private Component current;


    /**
     * Select the card with the specified constraint.
     *
     * @param constraint   constraint of card to select
     * @return             the component with the specified constraint
     */
    public Component selectCard(Object constraint) {
      if (current != null) {
        current.setVisible(false);
      }
      if (constraint == null) {
        current = null;
      } else {
        current = (Component)cards.get(constraint);
        if (current != null) {
          current.setVisible(true);
        }
      }
      return current;
    }

    /**
     * Get the card with the specified constraint.
     *
     * @param constraint   constraint of card to get
     */
    public Component getCard(Object constraint) {
      return (Component)cards.get(constraint);
    }

    /**
     * Get the selected card.
     *
     * @return    the selected card
     */
    public Component getSelectedCard() {
      return current;
    }

    /**
     * @see LayoutManager
     */
    public void addLayoutComponent(String name, Component comp) {
      addLayoutComponent(comp, name);
    }

    /**
     * @see LayoutManager
     */
    public void addLayoutComponent(Component comp, Object constraint) {
      cards.put(constraint, comp);

      if (current != null) {
        current.setVisible(false);
      }
      current = comp;
      comp.setVisible(true);
    }

    /**
     * @see LayoutManager
     */
    public void removeLayoutComponent(Component comp) {
      Iterator entries = cards.entrySet().iterator();
      while (entries.hasNext()) {
        Map.Entry entry = (Map.Entry)entries.next();
        if (entry.getValue() == comp) {
          cards.remove(entry.getKey());
          break;
        }
      }

      if (current == comp) {
        Iterator values = cards.values().iterator();
        if (values.hasNext()) {
          current = (Component)values.next();
          current.setVisible(true);
        } else {
          current = null;
        }
      }

      comp.setVisible(true);
    }

    /**
     * @see LayoutManager
     */
    public Dimension preferredLayoutSize(Container parent) {
      int nChildren = parent.getComponentCount();
      Insets insets = parent.getInsets();
      int width = insets.left + insets.right;
      int height = insets.top + insets.bottom;

      for (int i = 0; i < nChildren; i++) {
        Dimension d = parent.getComponent(i).getPreferredSize();
        if (d.width > width) {
          width = d.width;
        }
        if (d.height > height) {
            height = d.height;
        }
      }

      return new Dimension(width, height);
    }

    /**
     * @see LayoutManager
     */
    public Dimension minimumLayoutSize(Container parent) {
      int nChildren = parent.getComponentCount();
      Insets insets = parent.getInsets();
      int width = insets.left + insets.right;
      int height = insets.top + insets.bottom;

      for (int i = 0; i < nChildren; i++) {
        Dimension d = parent.getComponent(i).getMinimumSize();
        if (d.width > width) {
            width = d.width;
        }
        if (d.height > height) {
            height = d.height;
        }
      }

      return new Dimension(width, height);
    }

    /**
     * @see LayoutManager
     */
    public Dimension maximumLayoutSize(Container target) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * @see LayoutManager
     */
    public float getLayoutAlignmentX(Container target) {
      return 0.5f;
    }

    /**
     * @see LayoutManager
     */
    public float getLayoutAlignmentY(Container target) {
      return 0.5f;
    }

    /**
     * @see LayoutManager
     */
    public void invalidateLayout(Container target) {
    }

    /**
     * @see LayoutManager
     */
    public void layoutContainer(Container parent) {
      if (current != null) {
        Rectangle rect   = parent.getBounds();
        Insets    insets = parent.getInsets();

        int width  = rect.width  - insets.left + insets.right;
        int height = rect.height - insets.top  + insets.bottom;

        current.setBounds(insets.left, insets.top, width, height);
      }
    }
  }
}

