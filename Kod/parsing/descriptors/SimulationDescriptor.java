package parsing.descriptors;

import java.util.List;

public class SimulationDescriptor {

    public int requestNumber;
    public List<Integer> requestPriority;
    public List<String> requestStartingQueues;
    public double warmupTime;
    public double simulationTime;

}