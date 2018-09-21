package implementation.components.executing_units;

import implementation.components.Event;
import implementation.components.Request;
import implementation.components.Server;
import implementation.logic.SimulationLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomServerExecutingUnit extends ExecutingUnit implements Server.ServerListener {

    private List<Server> availableServers;
    private Random random = new Random();

    public RandomServerExecutingUnit(double warmupTime, List<Server> servers) {
        this.serverNum = servers.size();
        this.servers = servers;
        this.availableServers = new ArrayList<>();
        for (Server server : servers) {
            availableServers.add(server);
            server.setListener(this);
        }
    }

    @Override
    public void serverFinished(Server server, Request request, double finishTime) {
        availableServers.add(server);
        request.endServerTime = finishTime;
        serversWorking--;

        listener.executingUnitFinished(request);
    }

    @Override
    public Event execute(Request request, double time, boolean measuring) {
        int index = random.nextInt(availableServers.size());

        Event event = null;
        Server server = availableServers.remove(index);
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