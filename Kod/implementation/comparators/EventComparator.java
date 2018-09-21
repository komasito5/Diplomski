package implementation.comparators;

import implementation.components.Event;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {

    @Override
    public int compare(Event o1, Event o2) {
        if (o1.timestamp < o2.timestamp) return -1;
        else if (o1.timestamp > o2.timestamp) return 1;
        else return o1.order - o2.order;
    }

}