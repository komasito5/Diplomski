package ui;

import implementation.components.Component;
import ui.elements.Branching;
import ui.elements.EquivalentServers;
import ui.elements.Join;
import ui.elements.SchemeElement;
import ui.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Map;

public class Surface extends JPanel {

    private boolean canBeDrawn = true;

    private int width = 0;
    private int height = 0;

    private Map<String, SchemeElement> elements = new HashMap<>();
    private Map<String, Component> components;

    public boolean init(Map<String, Component> components) {
        this.components = components;

        // Create graphical components
        for (Component component : components.values()) {
            switch (component.getType()) {
                case EQUIVALENT_SERVERS: {
                    implementation.components.EquivalentServers c = (implementation.components.EquivalentServers) component;
                    EquivalentServers servers = new EquivalentServers(c.getId(), c.getServernNum());
                    servers.setServerIds(c.getServerIds());

                    Dimension dimension = servers.getDimension();
                    width += dimension.width;
                    if (dimension.height > height) {
                        height = dimension.height;
                    }

                    elements.put(c.getId(), servers);

                    break;
                }

                case JOIN: {
                    canBeDrawn = false;

                    implementation.components.Join c = (implementation.components.Join) component;
                    Join join = new Join();
                    elements.put(component.getId(), join);

                    break;
                }

                case BRANCHING: {
                    canBeDrawn = false;

                    implementation.components.Branching c = (implementation.components.Branching) component;
                    Branching branching = new Branching();
                    elements.put(c.getId(), branching);

                    break;
                }

                default: // Big error; should not happen
            }
        }

        return canBeDrawn;
    }

    // Determine positions of graphical elements
    private void initDrawingSurface(Graphics g) {
        boolean changeNeeded = false;
        int prefferedWidth = -1;
        int prefferedHeight = -1;

        if (width > getWidth()) {
            prefferedWidth = width + Constants.WIDTH_ADDITION;
            changeNeeded = true;
        }

        if (height > getHeight()) {
            prefferedHeight = height + Constants.HEIGHT_ADDITION;
            changeNeeded = true;
        }

        if (changeNeeded) {
            setPreferredSize(new Dimension(prefferedWidth, prefferedHeight));
        }

        String startId = elements.values().iterator().next().getId();
        String currentId = startId;

        int x = (getWidth() - width) / 2;
        int y = (getHeight() - height) / 2 + height / 2;

        int startX = x;
        int startY = y;

        int endX;
        int endY;
        while (true) {
            EquivalentServers servers = (EquivalentServers) elements.get(currentId);
            servers.init(x, y);
            x += servers.getDimension().width;

            implementation.components.EquivalentServers c = (implementation.components.EquivalentServers) components.get(currentId);
            if (c.getOutId().equals(startId)) {
                endX = x;
                endY = y;
                break;
            }
            currentId = c.getOutId();
        }

        connect(g, startX, startY, endX, endY);
    }

    private void connect(Graphics graphics, int startX, int startY, int endX, int endY) {
        Graphics2D g = (Graphics2D) graphics;

        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);

        g.draw(new Line2D.Double(
                startX,
                startY,
                startX,
                (getHeight() - height) / 2 - Constants.CONNECT_CONSTANT
        ));

        g.draw(new Line2D.Double(
                startX,
                (getHeight() - height) / 2 - Constants.CONNECT_CONSTANT,
                endX,
                (getHeight() - height) / 2 - Constants.CONNECT_CONSTANT
        ));

        g.draw(new Line2D.Double(
                endX,
                (getHeight() - height) / 2 - Constants.CONNECT_CONSTANT,
                endX,
                endY
        ));
    }

    public static void drawCenteredString(String s, int x1, int y1, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = x1 + w / 2 - fm.stringWidth(s) / 2;
        int y = (y1 + fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(s, x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (canBeDrawn) {
            initDrawingSurface(g);
            for (SchemeElement element : elements.values()) {
                element.draw(g);
            }
        }
    }
}