package implementation.queues;

import implementation.components.Request;

import java.util.Stack;

public class QueueLIFO implements Queue {

    private Stack<Request> requests = new Stack<>();

    @Override
    public void addNewRequest(Request request) {
        requests.push(request);
    }

    @Override
    public boolean isEmpty() {
        return requests.isEmpty();
    }

    @Override
    public Request getNextRequest() {
        return requests.pop();
    }

}