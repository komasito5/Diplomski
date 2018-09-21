package parsing.descriptors;

public class QueueDescriptor extends Descriptor {

    public enum QueueType {
        FIFO_QUEUE,
        LIFO_QUEUE,
        PRIORITY_QUEUE,
        RANDOM_QUEUE
    }

    public enum ServingDiscipline {
        FIRST_FREE_SERVER,
        LONGEST_FREE_SERVER,
        RANDOM_SERVER
    }

    public QueueType type;
    public ServingDiscipline servingDiscipline = ServingDiscipline.FIRST_FREE_SERVER;
    public String exitId;

}