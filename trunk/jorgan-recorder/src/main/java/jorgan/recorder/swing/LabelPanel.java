package jorgan.recorder.swing;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import javax.swing.JPanel;

public abstract class LabelPanel extends JPanel {

	public LabelPanel() {
		setFont(new Font("Monospaced", Font.PLAIN, 16));
	}

	protected abstract String getText();

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		String text = getText();

		Insets insets = getInsets();
		int width = getWidth();
		int height = getHeight();

		float size = (float) (height - insets.top - insets.bottom);
		g.setFont(getFont().deriveFont(size));

		FontMetrics metrics = g.getFontMetrics();
		int textWidth = metrics.stringWidth(text);

		float factor = textWidth / (float)(width - insets.left - insets.right);
		if (factor > 1.0f) {
			size = size / factor;

			g.setFont(getFont().deriveFont(size));

			metrics = g.getFontMetrics();
			textWidth = metrics.stringWidth(text);
		}

		int textAscent = metrics.getAscent();
		int textDescent = metrics.getDescent();

		g2d.drawString(text, width / 2 - textWidth / 2, height / 2
				+ (textAscent + textDescent) / 2 - textDescent);
	}
}
