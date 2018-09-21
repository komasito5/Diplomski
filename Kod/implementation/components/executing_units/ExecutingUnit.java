package implementation.components.executing_units;

import implementation.components.Event;
import implementation.components.Request;
import implementation.components.Server;

import java.util.ArrayList;
import java.util.List;

public abstract class ExecutingUnit {

    protected List<Server> servers;
    protected ExecutingUnitListener listener;
    protected int serverNum;

    protected int serversWorking = 0;

    public void setListener(ExecutingUnitListener listener) {
        this.listener = listener;
    }

    public List<String> getServersIds() {
        List<String> result = new ArrayList<>();
        for (Server server : servers) {
            result.add(server.getId());
        }

        return result;
    }

    public abstract Event execute(Request request, double time, boolean measuring);

    public abstract boolean canExecuteAtTheMoment();

    public int getServerNum() {
        return serverNum;
    }

    public interface ExecutingUnitListener {
        void executingUnitFinished(Request request);
    }

}