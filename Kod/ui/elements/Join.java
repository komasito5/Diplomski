package ui.elements;

import ui.util.Constants;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Join extends SchemeElement {

    private Point2D firstPoint;
    private Point2D secondPoint;
    private Point2D thirdPoint;
    private Point2D fourthPoint;

    public void init(String id, int x, int y) {
        this.id = id;

        firstPoint = new Point2D.Double(x - Constants.CROSS_DIMENSION / 2, y - Constants.CROSS_DIMENSION / 2);
        secondPoint = new Point2D.Double(x - Constants.CROSS_DIMENSION / 2, y + Constants.CROSS_DIMENSION / 2);
        thirdPoint = new Point2D.Double(x + Constants.CROSS_DIMENSION / 2, y - Constants.CROSS_DIMENSION / 2);
        fourthPoint = new Point2D.Double(x + Constants.CROSS_DIMENSION / 2, y + Constants.CROSS_DIMENSION / 2);
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);

        g.draw(new Line2D.Double(firstPoint, fourthPoint));
        g.draw(new Line2D.Double(secondPoint, thirdPoint));
    }

}
