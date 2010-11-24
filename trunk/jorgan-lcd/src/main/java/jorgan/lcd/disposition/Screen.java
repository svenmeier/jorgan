package jorgan.lcd.disposition;

import jorgan.disposition.Displayable;
import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.UndoableChange;

public class Screen extends Element {

	public void setWidget(Displayable element, Widget widget) {
		final WidgetReference reference = (WidgetReference) getReference(element);

		final Widget oldWidget = reference.getWidget();

		reference.setWidget(widget);

		fireChange(new WidgetChange(reference, oldWidget, widget));
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Displayable.class.isAssignableFrom(clazz);
	}

	@Override
	protected Reference<? extends Element> createReference(Element element) {
		if (element instanceof Displayable) {
			return new WidgetReference((Displayable) element);
		} else {
			return super.createReference(element);
		}
	}

	public static class WidgetReference extends Reference<Displayable> {

		private Widget widget;

		public WidgetReference(Displayable element) {
			super(element);
		}

		public void setWidget(Widget widget) {
			this.widget = widget;
		}

		public Widget getWidget() {
			return widget;
		}

		@Override
		public WidgetReference clone(Element element) {
			WidgetReference clone = (WidgetReference) super.clone(element);

			return clone;
		}
	}

	private class WidgetChange implements UndoableChange {

		private WidgetReference reference;

		private Widget oldWidget;

		private Widget newWidget;

		public WidgetChange(WidgetReference reference, Widget oldWidget,
				Widget newWidget) {
			this.reference = reference;

			this.oldWidget = oldWidget;

			this.newWidget = newWidget;
		}

		public void notify(OrganListener listener) {
			listener.indexedPropertyChanged(Screen.this, REFERENCE, reference);
		}

		public void undo() {
			setWidget(reference.getElement(), oldWidget);
		}

		public void redo() {
			setWidget(reference.getElement(), newWidget);
		}

		public boolean replaces(UndoableChange change) {
			if (change instanceof WidgetChange) {
				WidgetChange other = (WidgetChange) change;
				if (this.reference == other.reference) {
					this.newWidget = other.newWidget;

					return true;
				}
			}

			return false;
		}
	}
}