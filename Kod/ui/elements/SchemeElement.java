package ui.elements;

import java.awt.*;

public abstract class SchemeElement {

    protected String id;

    public abstract  void draw(Graphics graphics);

    public String getId() {
        return id;
    }

}