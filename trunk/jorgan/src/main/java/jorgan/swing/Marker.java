package jorgan.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

import javax.swing.JComponent;

public class Marker {

	private boolean xor;

	private int x1;

	private int y1;

	private int x2;

	private int y2;

	private Stroke stroke;

	private Color color;

	public Marker(boolean xor, Color foreground, BasicStroke stroke,
			Point mouseFrom, Point mouseTo) {
		this(xor, foreground, stroke, mouseFrom.x, mouseFrom.y, mouseTo.x,
				mouseTo.y);
	}

	public Marker(boolean xor, Color color, Stroke stroke, int x1, int y1,
			int x2, int y2) {
		this.xor = xor;
		this.color = color;
		this.stroke = stroke;

		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
	}

	public boolean contains(int x, int y) {
		return contains(x, y, 0, 0);
	}

	public boolean contains(int x, int y, int width, int height) {
		return this.x1 < x && this.x2 > x + width && this.y1 < y
				&& this.y2 > y + height;

	}

	public void add(JComponent component) {
		mark(component);
	}

	public void remove(JComponent component) {
		mark(component);
	}

	private void mark(JComponent component) {
		if (xor) {
			Graphics2D g = (Graphics2D) component.getGraphics();
			if (g != null) {
				paint(g);

				g.dispose();
			}
		} else {
			component.repaint(x1, y1, x2 - x1, y2 - y1);
		}
	}

	public void paint(Graphics2D g) {
		Stroke originalStroke = g.getStroke();
		g.setStroke(stroke);

		g.setColor(color);
		if (xor) {
			g.setXORMode(Color.white);
		}

		g.drawRect(x1, y1, x2 - x1 - 1, y2 - y1 - 1);

		if (xor) {
			g.setPaintMode();
		}

		g.setStroke(originalStroke);
	}
}
