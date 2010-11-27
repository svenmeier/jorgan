package jorgan.lcd.disposition;

import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.UndoableChange;

public class Screen extends Element {

	public void setStyle(Displayable element, String style) {
		final LocationReference reference = (LocationReference) getReference(element);

		final String oldStyle = reference.getStyle();

		reference.setStyle(style);

		fireChange(new LocationChange(reference, oldStyle, style));
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Displayable.class.isAssignableFrom(clazz);
	}

	@Override
	protected Reference<? extends Element> createReference(Element element) {
		if (element instanceof Displayable) {
			return new LocationReference((Displayable) element);
		} else {
			return super.createReference(element);
		}
	}

	public static class LocationReference extends Reference<Displayable> {

		private String style;

		public LocationReference(Displayable element) {
			super(element);
		}

		public void setStyle(String style) {
			this.style = style;
		}

		public String getStyle() {
			return style;
		}

		@Override
		public LocationReference clone(Element element) {
			LocationReference clone = (LocationReference) super.clone(element);

			return clone;
		}
	}

	private class LocationChange implements UndoableChange {

		private LocationReference reference;

		private String oldStyle;

		private String newStyle;

		public LocationChange(LocationReference reference, String oldStyle,
				String newStyle) {
			this.reference = reference;

			this.oldStyle = oldStyle;

			this.newStyle = newStyle;
		}

		public void notify(OrganListener listener) {
			listener.indexedPropertyChanged(Screen.this, REFERENCE, reference);
		}

		public void undo() {
			setStyle(reference.getElement(), oldStyle);
		}

		public void redo() {
			setStyle(reference.getElement(), newStyle);
		}

		public boolean replaces(UndoableChange change) {
			if (change instanceof LocationChange) {
				LocationChange other = (LocationChange) change;
				if (this.reference == other.reference) {
					this.newStyle = other.newStyle;

					return true;
				}
			}

			return false;
		}
	}
}