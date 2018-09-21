package ui.elements;

import ui.Surface;
import ui.util.Constants;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class EquivalentServers extends SchemeElement {

    private int serverNum;

    private String queueId;
    private List<String> serverIds;

    private List<Point2D> firstLine = new ArrayList<>();
    private List<Point2D> secondLine = new ArrayList<>();
    private List<List<Point2D>> thirdLine = new ArrayList<>();
    private List<List<Point2D>> fourthLine = new ArrayList<>();
    private List<Point2D> fifthLine = new ArrayList<>();

    public EquivalentServers(String id, int serverNum) {
        this.id = id;
        this.queueId = id;
        this.serverNum = serverNum;
    }

    public void setServerIds(List<String> serverIds) {
        this.serverIds = serverIds;
    }

    private void resetParameters() {
        firstLine = new ArrayList<>();
        secondLine = new ArrayList<>();
        thirdLine = new ArrayList<>();
        fourthLine = new ArrayList<>();
        fifthLine = new ArrayList<>();

        for (int i = 0; i < serverNum; i++) {
            thirdLine.add(new ArrayList<>());
            fourthLine.add(new ArrayList<>());
        }
    }

    public void init(int x, int y) {
        resetParameters();

        Point2D temp;
        firstLine.add(temp = new Point2D.Double(x, y));
        firstLine.add(temp = new Point2D.Double(temp.getX() + Constants.FIRST_LINE_LENGTH, temp.getY()));

        secondLine.add(temp = new Point2D.Double(temp.getX() + Constants.QUEUE_WIDTH, temp.getY()));
        secondLine.add(temp = new Point2D.Double(temp.getX() + Constants.SECOND_LINE_LENGTH, temp.getY()));

        if (serverNum % 2 == 1) {
            thirdLine.get(serverNum / 2).add(temp);
            thirdLine.get(serverNum / 2).add(new Point2D.Double(temp.getX() + Constants.THIRD_LINE_LENGTH, temp.getY()));

            int up = (int) temp.getY() - Constants.SERVER_RADIUS - Constants.SERVER_VERTICAL_SPACE;
            int down = (int) temp.getY() + Constants.SERVER_RADIUS + Constants.SERVER_VERTICAL_SPACE;

            for (int i = serverNum / 2 - 1; i >= 0; i--) {
                thirdLine.get(i).add(new Point2D.Double(temp.getX(), up));
                thirdLine.get(i).add(new Point2D.Double(temp.getX() + Constants.THIRD_LINE_LENGTH, up));

                up -= (Constants.SERVER_RADIUS + Constants.SERVER_VERTICAL_SPACE);
            }

            for (int i = serverNum / 2 + 1; i < serverNum; i++) {
                thirdLine.get(i).add(new Point2D.Double(temp.getX(), down));
                thirdLine.get(i).add(new Point2D.Double(temp.getX() + Constants.THIRD_LINE_LENGTH, down));

                down += Constants.SERVER_RADIUS + Constants.SERVER_VERTICAL_SPACE;
            }
        } else {
            int up = (int) temp.getY() - (Constants.SERVER_RADIUS + Constants.SERVER_VERTICAL_SPACE) / 2;
            int down = (int) temp.getY() + (Constants.SERVER_RADIUS + Constants.SERVER_VERTICAL_SPACE) / 2;

            for (int i = serverNum / 2 - 1; i >= 0; i--) {
                thirdLine.get(i).add(new Point2D.Double(temp.getX(), up));
                thirdLine.get(i).add(new Point2D.Double(temp.getX() + Constants.THIRD_LINE_LENGTH, up));

                up -= (Constants.SERVER_RADIUS + Constants.SERVER_VERTICAL_SPACE);
            }

            for (int i = serverNum / 2; i < serverNum; i++) {
                thirdLine.get(i).add(new Point2D.Double(temp.getX(), down));
                thirdLine.get(i).add(new Point2D.Double(temp.getX() + Constants.THIRD_LINE_LENGTH, down));

                down += Constants.SERVER_RADIUS + Constants.SERVER_VERTICAL_SPACE;
            }
        }

        for (int i = 0; i < serverNum; i++) {
            temp = thirdLine.get(i).get(1);
            fourthLine.get(i).add(temp = new Point2D.Double(temp.getX() + Constants.SERVER_RADIUS, temp.getY()));
            fourthLine.get(i).add(new Point2D.Double(temp.getX() + Constants.FOURTH_LINE_LENGTH, temp.getY()));
        }

        if (serverNum % 2 == 1) {
            temp = fourthLine.get(serverNum / 2).get(1);
            fifthLine.add(temp);
            fifthLine.add(new Point2D.Double(temp.getX() + Constants.FIFTH_LINE_LENGTH, temp.getY()));
        } else {
            temp = fourthLine.get(0).get(1);
            fifthLine.add(temp = new Point2D.Double(temp.getX(), secondLine.get(1).getY()));
            fifthLine.add(new Point2D.Double(temp.getX() + Constants.FIFTH_LINE_LENGTH, temp.getY()));
        }
    }

    @Override
    public void draw(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);

        g.draw(new Line2D.Double(firstLine.get(0), firstLine.get(1)));

        g.drawRect((int) firstLine.get(1).getX(), (int) firstLine.get(1).getY() - Constants.QUEUE_HEIGHT / 2, Constants.QUEUE_WIDTH, Constants.QUEUE_HEIGHT);
        Surface.drawCenteredString(queueId, (int) firstLine.get(1).getX(), (int) firstLine.get(1).getY() - Constants.QUEUE_HEIGHT / 2, Constants.QUEUE_WIDTH, Constants.QUEUE_HEIGHT, graphics);

        g.draw(new Line2D.Double(secondLine.get(0), secondLine.get(1)));

        for (int i = 0; i < serverNum; i++) {
            g.draw(new Line2D.Double(thirdLine.get(i).get(0), thirdLine.get(i).get(1)));
        }
        g.draw(new Line2D.Double(thirdLine.get(0).get(0), thirdLine.get(serverNum - 1).get(0)));

        for (int i = 0; i < serverNum; i++) {
            g.drawOval((int) thirdLine.get(i).get(1).getX(), (int) thirdLine.get(i).get(1).getY() - Constants.SERVER_RADIUS / 2, Constants.SERVER_RADIUS, Constants.SERVER_RADIUS);
            Surface.drawCenteredString(serverIds.get(i), (int) thirdLine.get(i).get(1).getX(), (int) thirdLine.get(i).get(1).getY() - Constants.SERVER_RADIUS / 2, Constants.SERVER_RADIUS, Constants.SERVER_RADIUS, graphics);
        }

        for (int i = 0; i < serverNum; i++) {
            g.draw(new Line2D.Double(fourthLine.get(i).get(0), fourthLine.get(i).get(1)));
        }
        g.draw(new Line2D.Double(fourthLine.get(0).get(1), fourthLine.get(serverNum - 1).get(1)));

        g.draw(new Line2D.Double(fifthLine.get(0), fifthLine.get(1)));
    }

    public Dimension getDimension() {
        return new Dimension(
                Constants.FIRST_LINE_LENGTH + Constants.QUEUE_WIDTH + Constants.SECOND_LINE_LENGTH
                + Constants.THIRD_LINE_LENGTH + Constants.SERVER_RADIUS + Constants.FOURTH_LINE_LENGTH
                + Constants.FIFTH_LINE_LENGTH,
                (serverNum - 1) * Constants.SERVER_VERTICAL_SPACE + serverNum * Constants.SERVER_RADIUS + 100
        );
    }

}