package implementation.queues;

import implementation.components.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QueueRandom implements Queue {

    private List<Request> requests = new ArrayList<>();
    private Random random = new Random();

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
        int index = random.nextInt(requests.size());
        return requests.remove(index);
    }

}