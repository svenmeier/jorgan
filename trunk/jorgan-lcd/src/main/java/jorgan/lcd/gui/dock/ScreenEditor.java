package jorgan.lcd.gui.dock;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.AbstractEditor;
import jorgan.lcd.disposition.Screen;
import jorgan.session.OrganSession;
import spin.Spin;

public class ScreenEditor extends AbstractEditor {

	private EventHandler eventHandler = new EventHandler();

	private OrganSession session;

	private Screen screen;

	public ScreenEditor(Screen screen) {
		this.screen = screen;
	}

	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(eventHandler));

			setContent(null);
		}

		this.session = session;

		if (this.session != null) {
			updateTitle();

			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(eventHandler));
		}
	}

	private void updateTitle() {
		setTitle(Elements.getDisplayName(screen));
	}

	private class EventHandler extends OrganAdapter {
		@Override
		public void propertyChanged(Element element, String name) {
			if (element == screen) {
				updateTitle();
			}
		}
	}
}