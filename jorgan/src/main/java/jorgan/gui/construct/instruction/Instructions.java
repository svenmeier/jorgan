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
package jorgan.gui.construct.instruction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jorgan.disposition.Stop;

public class Instructions {

    private static Instructions instance = new Instructions();
    
    public String getClassName(Class clazz) {
        Map parameters = new HashMap();
        parameters.put("class"   , classWithoutPackage(clazz));
        
        return transform("className.xsl", parameters);
    }

    public String getPropertyName(Class clazz, String property) {
        Map parameters = new HashMap();
        parameters.put("class"   , classWithoutPackage(clazz));
        parameters.put("property", property);
        
        return transform("propertyName.xsl", parameters);
    }
    
    public String getInstruction(Class clazz) {
        Map parameters = new HashMap();
        parameters.put("class", classWithoutPackage(clazz));
        
        return transform("instruction.xsl", parameters);
    }

    protected String transform(String template, Map parameters) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(getClass().getResourceAsStream(template)));
            
            Iterator iterator = parameters.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                transformer.setParameter(key, parameters.get(key));           
            }
            
            Source source = new StreamSource(new File("../docs/instruction/de/instruction.xml"));

            ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
            
            transformer.transform(source, new StreamResult(byteArrayOut));
            
            return new String(byteArrayOut.toByteArray(), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    private static String classWithoutPackage(Class clazz) {
        String name = clazz.getName();
          
        int index = name.lastIndexOf('.');
        if (index != -1) {
          name = name.substring(index + 1);
        }
          
        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
          
        return name;
    }

    public static Instructions getInstance() {
        return instance;
    }
    
    public static void main(String[] args) {
        System.out.println(getInstance().getInstruction (Stop.class));
        System.out.println(getInstance().getClassName   (Stop.class));
        System.out.println(getInstance().getPropertyName(Stop.class, "program"));
    }
}