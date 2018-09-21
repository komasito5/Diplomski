package parsing.descriptors;

public class ServerDescriptor extends Descriptor {

    public enum DistributionType {
        EXPONENTIAL,
        UNIFORM
    }

    public DistributionType distributionType;
    public double averageExecutionTime;
    public double minExecutionTime;
    public double maxExecutionTime;
    public String exitId;

}