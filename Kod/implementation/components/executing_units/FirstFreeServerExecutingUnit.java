package implementation.components.executing_units;

import implementation.components.Event;
import implementation.components.Request;
import implementation.components.Server;
import implementation.logic.SimulationLogic;

import java.util.List;

public class FirstFreeServerExecutingUnit extends ExecutingUnit implements Server.ServerListener {

    public FirstFreeServerExecutingUnit(double warmupTime, List<Server> servers) {
        this.serverNum = servers.size();
        this.servers = servers;
        for (Server server : servers) server.setListener(this);
    }

    @Override
    public Event execute(Request request, double time, boolean measuring) {
        Event event = null;
        for (Server server : servers) {
            if (!server.isBusy()) {
                serversWorking++;
                event = new Event();
                event.componentFromId = server.getId();
                double averageTime = server.getExecutionTime();
                event.timestamp = averageTime + time;
                event.request = request;
                event.order = SimulationLogic.seq.incrementAndGet();

                server.execute(measuring, time, request, event.timestamp);
                break;
            }
        }

        return event;
    }

    @Override
    public boolean canExecuteAtTheMoment() {
        return serversWorking < serverNum;
    }

    @Override
    public void serverFinished(Server server, Request request, double finishTime) {
        request.endServerTime = finishTime;
        serversWorking--;
        listener.executingUnitFinished(request);
    }

}