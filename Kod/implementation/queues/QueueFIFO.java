package implementation.queues;

import implementation.components.Request;

import java.util.ArrayList;
import java.util.List;

public class QueueFIFO implements Queue {

    private List<Request> requests = new ArrayList<>();

    @Override
    public void addNewRequest(Request request) {
        requests.add(request);
    }

    @Override
    public boolean isEmpty() {
        return requests.isEmpty();
    }

    @Override
    public Request getNextRequest() {
        return requests.remove(0);
    }

}