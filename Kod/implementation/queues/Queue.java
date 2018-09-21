package implementation.queues;

import implementation.components.Request;

public interface Queue {

    void addNewRequest(Request request);
    boolean isEmpty();
    Request getNextRequest();

}