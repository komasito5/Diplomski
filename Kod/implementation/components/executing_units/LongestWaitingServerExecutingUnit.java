package implementation.components.executing_units;

import implementation.components.Event;
import implementation.components.Request;
import implementation.components.Server;
import implementation.logic.SimulationLogic;

import java.util.ArrayList;
import java.util.List;

public class LongestWaitingServerExecutingUnit extends ExecutingUnit implements Server.ServerListener {

    private List<Server> longestServers;

    public LongestWaitingServerExecutingUnit(double warmupTime, List<Server> servers) {
        this.serverNum = servers.size();
        this.servers = servers;
        this.longestServers = new ArrayList<>();
        for (Server server : servers) {
            longestServers.add(server);
            server.setListener(this);
        }
    }

    @Override
    public void serverFinished(Server server, Request request, double finishTime) {
        longestServers.add(server);
        request.endServerTime = finishTime;
        serversWorking--;

        listener.executingUnitFinished(request);
    }

    @Override
    public Event execute(Request request, double time, boolean measuring) {
        Event event = null;
        Server server = longestServers.remove(0);
        serversWorking++;
        event = new Event();
        event.componentFromId = server.getId();
        double executiongTime = server.getExecutionTime();
        event.timestamp = executiongTime + time;
        event.request = request;
        event.order = SimulationLogic.seq.incrementAndGet();

        server.execute(measuring, time, request, event.timestamp);

        return event;
    }

    @Override
    public boolean canExecuteAtTheMoment() {
        return serversWorking < serverNum;
    }

}