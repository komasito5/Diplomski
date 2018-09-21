package ui.elements;

import ui.util.Constants;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Branching extends SchemeElement {

    private int branchNumber;

    private List<Point2D> firstLine = new ArrayList<>();
    private List<List<Point2D>> secondLine = new ArrayList<>();

    public void init(String id, int branchNumber, int x, int y) {
        this.id = id;
        this.branchNumber = branchNumber;

        for (int i = 0; i < branchNumber; i++) {
            secondLine.add(new ArrayList<>());
        }

        Point2D temp;
        firstLine.add(temp = new Point2D.Double(x, y));
        firstLine.add(temp = new Point2D.Double(temp.getX() + Constants.FIRST_LINE_LENGTH, temp.getY()));

        if (branchNumber % 2 == 1) {
            secondLine.get(branchNumber / 2).add(temp);
            secondLine.get(branchNumber / 2).add(new Point2D.Double(temp.getX() + Constants.SECOND_LINE_LENGTH, temp.getY()));

            int up = (int) temp.getY() - Constants.BRANCH_VERTICAL_SPACE;
            int down = (int) temp.getY() + Constants.BRANCH_VERTICAL_SPACE;

            for (int i = branchNumber / 2 - 1; i >= 0; i--) {
                secondLine.get(i).add(new Point2D.Double(temp.getX(), up));
                secondLine.get(i).add(new Point2D.Double(temp.getX() + Constants.SECOND_LINE_LENGTH, up));

                up -= Constants.BRANCH_VERTICAL_SPACE;
            }

            for (int i = branchNumber / 2 + 1; i < branchNumber; i++) {
                secondLine.get(i).add(new Point2D.Double(temp.getX(), down));
                secondLine.get(i).add(new Point2D.Double(temp.getX() + Constants.SECOND_LINE_LENGTH, down));

                down += Constants.BRANCH_VERTICAL_SPACE;
            }
        } else {
            int up = (int) temp.getY() - Constants.BRANCH_VERTICAL_SPACE / 2;
            int down = (int) temp.getY() + Constants.BRANCH_VERTICAL_SPACE / 2;

            for (int i = branchNumber / 2 - 1; i >= 0; i--) {
                secondLine.get(i).add(new Point2D.Double(temp.getX(), up));
                secondLine.get(i).add(new Point2D.Double(temp.getX() + Constants.SECOND_LINE_LENGTH, up));

                up -= Constants.BRANCH_VERTICAL_SPACE;
            }

            for (int i = branchNumber / 2; i < branchNumber; i++) {
                secondLine.get(i).add(new Point2D.Double(temp.getX(), down));
                secondLine.get(i).add(new Point2D.Double(temp.getX() + Constants.SECOND_LINE_LENGTH, down));

                down += Constants.BRANCH_VERTICAL_SPACE;
            }
        }
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);

        g.draw(new Line2D.Double(firstLine.get(0), firstLine.get(1)));

        for (int i = 0; i < branchNumber; i++) {
            g.draw(new Line2D.Double(secondLine.get(i).get(0), secondLine.get(i).get(1)));
        }
        g.draw(new Line2D.Double(secondLine.get(0).get(0), secondLine.get(branchNumber - 1).get(0)));
    }

}
