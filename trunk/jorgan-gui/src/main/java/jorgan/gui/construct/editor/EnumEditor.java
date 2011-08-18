package jorgan.gui.construct.editor;

import java.beans.PropertyEditorSupport;

import jorgan.util.Generics;

public abstract class EnumEditor<T extends Enum<T>> extends
		PropertyEditorSupport {

	@SuppressWarnings("unchecked")
	private Class<T> getEnumType() {
		return (Class<T>) Generics.getTypeParameter(this.getClass());
	}

	@Override
	public String[] getTags() {
		T[] values = getEnumType().getEnumConstants();

		String[] tags = new String[values.length];
		for (int t = 0; t < tags.length; t++) {
			tags[t] = values[t].name();
		}

		return tags;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getAsText() {
		T value = (T) getValue();
		if (value == null) {
			return null;
		} else {
			return value.name();
		}
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		super.setValue(Enum.valueOf(getEnumType(), text));
	}
}