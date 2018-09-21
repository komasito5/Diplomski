package implementation.queues;

import implementation.components.Request;
import implementation.logic.SimulationLogic;
import implementation.comparators.RequestComparator;

import java.util.PriorityQueue;

public class QueuePriority implements Queue {

    private PriorityQueue<Request> requests = new PriorityQueue<>(new RequestComparator());

    @Override
    public void addNewRequest(Request request) {
        request.order = SimulationLogic.seq.incrementAndGet();
        requests.add(request);
    }

    @Override
    public boolean isEmpty() {
        return requests.isEmpty();
    }

    @Override
    public Request getNextRequest() {
        return requests.poll();
    }

}