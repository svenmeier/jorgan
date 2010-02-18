package jorgan.swing.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.accessibility.AccessibleContext;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * A cell renderer that adds comments to cells.
 */
public class CommentedCellRenderer extends JPanel implements ListCellRenderer {

	private static Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);

	private ListCellRenderer renderer;

	private JComponent rendererComponent;

	private JLabel commentRenderer = new JLabel();

	/**
	 * Constructor using a default cell rendereJPanelr.
	 */
	public CommentedCellRenderer() {
		this(new DefaultListCellRenderer());
	}

	/**
	 * Constructor using the given cell renderer.
	 * 
	 * @param renderer
	 *            the renderer to add comments to
	 */
	public CommentedCellRenderer(ListCellRenderer renderer) {
		super(new BorderLayout());

		this.renderer = renderer;

		commentRenderer.setHorizontalAlignment(JLabel.TRAILING);
		commentRenderer.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(commentRenderer, BorderLayout.CENTER);
	}

	/**
	 * Set the nested renderer.
	 * 
	 * @param renderer
	 *            the renderer
	 */
	public void setRenderer(ListCellRenderer renderer) {
		this.renderer = renderer;
		this.rendererComponent = null;
	}

	@Override
	public AccessibleContext getAccessibleContext() {
		if (rendererComponent != null) {
			return rendererComponent.getAccessibleContext();
		}
		return super.getAccessibleContext();
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		JComponent component = (JComponent) renderer
				.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);

		setRendererComponent(component);

		setBorder(component.getBorder());
		setBackground(component.getBackground());
		component.setBorder(EMPTY_BORDER);

		commentRenderer.setText(getComment(value, index, isSelected));
		commentRenderer.setForeground(average(component.getForeground(),
				component.getBackground()));
		commentRenderer.setBorder(EMPTY_BORDER);

		return this;
	}

	private void setRendererComponent(JComponent component) {
		if (this.rendererComponent != component) {
			if (this.rendererComponent != null) {
				remove(this.rendererComponent);
			}
			this.rendererComponent = component;

			add(component, BorderLayout.WEST);
			revalidate();
		}
	}

	private Color average(Color foreground, Color background) {

		int red = (foreground.getRed() + background.getRed()) / 2;
		int green = (foreground.getGreen() + background.getGreen()) / 2;
		int blue = (foreground.getBlue() + background.getBlue()) / 2;
		return new Color(red, green, blue);
	}

	/**
	 * Get the comment for the given value.
	 * 
	 * @param value
	 *            value to get comment for
	 * @param index
	 *            index of value
	 * @param isSelected
	 *            is the value currently selected
	 * @return the comment
	 */
	protected String getComment(Object value, int index, boolean isSelected) {
		if (index == -1) {
			// used in comboBox
			return "";
		} else {
			return Integer.toString(index + 1);
		}
	}
}
