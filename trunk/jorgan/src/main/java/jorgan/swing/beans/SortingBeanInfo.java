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
package jorgan.swing.beans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Comparator;

/**
 */
public class SortingBeanInfo implements BeanInfo {

  private BeanInfo info;
  
  public SortingBeanInfo(BeanInfo info) {
    this.info = info;
  }
  
  public BeanInfo[] getAdditionalBeanInfo() {
    return info.getAdditionalBeanInfo();
  }
  
  public BeanDescriptor getBeanDescriptor() {
    return info.getBeanDescriptor();
  }
  
  public int getDefaultEventIndex() {
    return info.getDefaultEventIndex();
  }
  
  public int getDefaultPropertyIndex() {
    int index = info.getDefaultPropertyIndex();
    if (index == -1) {
      return -1;
    } else {
      return 0;
    }
  }
  
  public EventSetDescriptor[] getEventSetDescriptors() {
    return info.getEventSetDescriptors();
  }
  
  public Image getIcon(int iconKind) {
    return info.getIcon(iconKind);
  }

  public MethodDescriptor[] getMethodDescriptors() {
    return info.getMethodDescriptors();
  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

    PropertyDescriptor defaultPropertyDescriptor = null;
    
    int index = info.getDefaultPropertyIndex();
    if (index != -1) {
      defaultPropertyDescriptor = descriptors[index];    
    }
    
    descriptors = (PropertyDescriptor[])descriptors.clone();
    Arrays.sort(descriptors, new PropertyComparator(defaultPropertyDescriptor));
    
    return descriptors;
  }
  
  /**
   * Comparator for sorting of property descriptors in alphabetical ordering of
   * their short descriptions. 
   */
  protected class PropertyComparator implements Comparator {

    private PropertyDescriptor defaultPropertyDescriptor;
    
    public PropertyComparator(PropertyDescriptor defaultPropertyDescriptor) {
      this.defaultPropertyDescriptor = defaultPropertyDescriptor;
    }
    
    public int compare(Object o1, Object o2) {
      PropertyDescriptor descriptor1 = (PropertyDescriptor)o1;
      PropertyDescriptor descriptor2 = (PropertyDescriptor)o2;

      if (defaultPropertyDescriptor == descriptor1) {
        return -1;
      }
      if (defaultPropertyDescriptor == descriptor2) {
        return 1;
      }

      return compare(descriptor1, descriptor2);
    }
    
    protected int compare(PropertyDescriptor descriptor1, PropertyDescriptor descriptor2) {
      return descriptor1.getShortDescription().compareTo(descriptor2.getShortDescription());
    }
  }
}