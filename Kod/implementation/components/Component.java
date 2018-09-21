package implementation.components;

import parsing.descriptors.ComponentType;

public abstract class Component {

    protected String id;
    protected ComponentType type;

    protected ComponentListener componentListener;
    protected InfoListener infoListener;

    public void setComponentListener(ComponentListener listener) {
        this.componentListener = listener;
    }

    public void setInfoListener(InfoListener listener) {
        this.infoListener = listener;
    }

    public ComponentType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public abstract void addNewRequest(Request request);

    public interface ComponentListener {
        void componentFinished(String componentId, Request request);
        void addEvent(Event event);
    }

    public interface InfoListener {
        double getCurrentTime();
        double getSimulationTime();
        double getWarmupTime();
        boolean shouldMeasure();
    }

}