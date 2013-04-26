package org.jnorthr.menus.support;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

public class BottomBorder extends AbstractBorder {
  protected int thickness;

  protected Color lineColor;

  protected int gap;

  // constructor: color + default(thickness + gap)
  public BottomBorder(Color color) {
    this(color, 1, 1);
  }

  // constructor: color + thickness + default(gap)
  public BottomBorder(Color color, int thickness) {
    this(color, thickness, thickness);
  }

  // constructor: color + thickness + gap
  public BottomBorder(Color color, int thickness, int gap) {
    lineColor = color;
    this.thickness = thickness;
    this.gap = gap;
  }

  // -------------------------------------------------------
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
    Color oldColor = g.getColor();
    int i;

    g.setColor(lineColor);
    for (i = 0; i < thickness; i++) {
      g.drawLine(x, y + height - i - 1, x + width, y + height - i - 1);
    }
    g.setColor(oldColor);
  }

  public Insets getBorderInsets(Component c) {
    return new Insets(0, 0, gap, 0);
  }

  public Insets getBorderInsets(Component c, Insets insets) {
    insets.left = 0;
    insets.top = 0;
    insets.right = 0;
    insets.bottom = gap;
    return insets;
  }

  /**
   * Returns the color of the border.
   */
  public Color getLineColor() {
    return lineColor;
  }

  /**
   * Returns the thickness of the border.
   */
  public int getThickness() {
    return thickness;
  }

  /**
   * Returns whether or not the border is opaque.
   */
  public boolean isBorderOpaque() {
    return false;
  }

  public int getGap() {
    return gap;
  }

  public static void main(String[] args) 
  {
      System.out.println("Hello from BottomBorder.");
	  Color c = new Color(255);
	  BottomBorder b = new  BottomBorder(c);
      System.out.println("isBorderOpaque() ? "+b.isBorderOpaque());
      System.out.println("getThickness() ? "+b.getThickness());
	  c = b.getLineColor();
	  String d = c.toString();
      System.out.println("Color from BottomBorder ? "+d );
      System.out.println("getGap() ? "+b.getGap());
  }

} // end of border
