package implementation.components;

import implementation.components.executing_units.ExecutingUnit;
import implementation.components.executing_units.FirstFreeServerExecutingUnit;
import implementation.components.executing_units.LongestWaitingServerExecutingUnit;
import implementation.components.executing_units.RandomServerExecutingUnit;
import implementation.queues.Queue;
import parsing.descriptors.ComponentType;
import parsing.descriptors.QueueDescriptor;

import java.util.List;

public class EquivalentServers extends Component implements ExecutingUnit.ExecutingUnitListener {

    private Queue waitingQueue;
    private ExecutingUnit executingUnit;

    private String outId;

    // Initialize component
    public void init(String id, Queue queue, List<Server> servers, QueueDescriptor descriptor) {
        this.id = id;
        this.type = ComponentType.EQUIVALENT_SERVERS;

        waitingQueue = queue;

        switch (descriptor.servingDiscipline) {
            case FIRST_FREE_SERVER: {
                executingUnit = new FirstFreeServerExecutingUnit(infoListener.getWarmupTime(), servers);
                break;
            }

            case LONGEST_FREE_SERVER: {
                executingUnit = new LongestWaitingServerExecutingUnit(infoListener.getWarmupTime(), servers);
                break;
            }

            case RANDOM_SERVER: {
                executingUnit = new RandomServerExecutingUnit(infoListener.getWarmupTime(), servers);
                break;
            }
        }
        executingUnit.setListener(this);
    }

    // Add request to waiting queue at the beginning of the simulation
    public void initWaitingQueue(Request request) {
        waitingQueue.addNewRequest(request);
    }

    // Simulation has started
    public void start() {
        while (!waitingQueue.isEmpty() && executingUnit.canExecuteAtTheMoment()) {
            Event event = executingUnit.execute(waitingQueue.getNextRequest(), infoListener.getCurrentTime(), infoListener.shouldMeasure());
            componentListener.addEvent(event);
        }
    }

    public void setOutId(String id) {
        outId = id;
    }

    public String getOutId() {
        return outId;
    }

    public int getServernNum() {
        return executingUnit.getServerNum();
    }

    public List<String> getServerIds() {
        return executingUnit.getServersIds();
    }

    @Override
    public void addNewRequest(Request request) {
        request.startServerTime = infoListener.getCurrentTime();

        if (executingUnit.canExecuteAtTheMoment()) {
            Event event = executingUnit.execute(request, infoListener.getCurrentTime(), infoListener.shouldMeasure());
            componentListener.addEvent(event);
        } else {
            waitingQueue.addNewRequest(request);
        }
    }

    @Override
    public void executingUnitFinished(Request request) {
        boolean measuring = infoListener.shouldMeasure();

        if (!waitingQueue.isEmpty() && executingUnit.canExecuteAtTheMoment()) {
            Event event = executingUnit.execute(waitingQueue.getNextRequest(), infoListener.getCurrentTime(), measuring);
            componentListener.addEvent(event);
        }

        componentListener.componentFinished(id, request);
    }

}