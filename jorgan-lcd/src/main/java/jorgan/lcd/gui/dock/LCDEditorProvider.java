package jorgan.lcd.gui.dock;

import jorgan.disposition.Element;
import jorgan.gui.dock.AbstractEditor;
import jorgan.gui.dock.spi.EditorProvider;
import jorgan.lcd.disposition.Display;

public class LCDEditorProvider implements EditorProvider {

	@Override
	public AbstractEditor getEditor(Element element) {
		if (element instanceof Display) {
			return new ScreenEditor((Display) element);
		}
		return null;
	}
}