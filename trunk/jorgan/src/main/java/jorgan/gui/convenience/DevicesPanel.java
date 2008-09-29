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
package jorgan.gui.convenience;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Input;
import jorgan.disposition.Organ;
import jorgan.disposition.Output;
import jorgan.gui.img.ElementIcons;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;

/**
 * Device panel.
 */
public class DevicesPanel extends JPanel {

	private Organ organ;
	private Map<Element, JComboBox> boxes = new HashMap<Element, JComboBox>();
	private DirectionAdapter adapter;

	public DevicesPanel(Organ organ, Direction direction) {
		this.organ = organ;

		if (Direction.IN == direction) {
			adapter = new InAdapter();
		} else {
			adapter = new OutAdapter();
		}
		
		init();
	}
	
	public void write() {
		for (Element element : boxes.keySet()) {
			adapter.write(element, (String)boxes.get(element).getSelectedItem());
		}
	}
	
	private void init() {

		String[] tags = adapter.getTags();
		
		DefinitionBuilder builder = new DefinitionBuilder(this);
		Column column = builder.column();
		for (Element element : organ.getElements()) {
			if (adapter.applies(element)) {
				JLabel label = new JLabel(Elements.getDisplayName(element));
				label.setHorizontalTextPosition(SwingConstants.LEFT);
				label.setIcon(ElementIcons.getIcon(element.getClass()));
				column.term(label);

				column.definition(createComboBox(element, tags)).fillHorizontal();
			}
		}
	}

	private JComboBox createComboBox(Element element, String[] tags) {
		
		JComboBox box = new JComboBox(tags);
		
		box.setSelectedItem(adapter.read(element));

		boxes.put(element, box);
		
		return box;
	}
	
	private String[] getTags(Direction direction) {
		String[] deviceNames = DevicePool.instance().getMidiDeviceNames(
				direction);

		String[] tags = new String[1 + deviceNames.length];

		System.arraycopy(deviceNames, 0, tags, 1, deviceNames.length);
		
		return tags;
	}
	
	private interface DirectionAdapter {

		public String[] getTags();

		public boolean applies(Element element);

		public Object read(Element element);

		public void write(Element element, String string);
	}
	
	private class InAdapter implements DirectionAdapter {
		
		public boolean applies(Element element) {
			return element instanceof Input;
		}

		public String[] getTags() {
			return DevicesPanel.this.getTags(Direction.IN);
		}
		
		public Object read(Element element) {
			return ((Input)element).getInput();
		}

		public void write(Element element, String string) {
			((Input)element).setInput(string);
		}
	}
	
	private class OutAdapter implements DirectionAdapter {

		public boolean applies(Element element) {
			return element instanceof Output;
		}
		
		public String[] getTags() {
			return DevicesPanel.this.getTags(Direction.OUT);
		}
				
		public Object read(Element element) {
			return ((Output)element).getOutput();
		}

		public void write(Element element, String string) {
			((Output)element).setOutput(string);
		}
	}
}