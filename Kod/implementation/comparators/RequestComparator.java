package implementation.comparators;

import implementation.components.Request;

import java.util.Comparator;

public class RequestComparator implements Comparator<Request> {

    @Override
    public int compare(Request o1, Request o2) {
        if (o1.priority < o2.priority) return 1;
        else if (o1.priority > o2.priority) return -1;
        else return o2.order - o1.order;
    }

}