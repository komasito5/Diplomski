package implementation.components;

import parsing.descriptors.ServerDescriptor.DistributionType;

import java.util.Random;

public class Server {

    private ServerListener listener;

    private String id;
    private DistributionType distributionType;
    private double averageExecutionTime;
    private double minExecutionTime;
    private double maxExecutionTime;

    private Request currentRequest;
    private double finishTime;

    private double idleTime = 0;
    private double lastWorkingTime = 0;
    private double warmupTime;

    private int completed = 0;
    private double requestTime = 0;

    private Random random = new Random();

    public Server(String id, DistributionType distributionType, double averageExecutionTime, double minExecutionTime, double maxExecutionTime) {
        this.id = id;
        this.distributionType = distributionType;
        this.averageExecutionTime = averageExecutionTime;
        this.minExecutionTime = minExecutionTime;
        this.maxExecutionTime = maxExecutionTime;
    }

    public void init(double warmupTime) {
        lastWorkingTime = warmupTime;
        this.warmupTime = warmupTime;
    }

    public String getId() {
        return id;
    }

    public double getAverageExecutionTime() {
        return averageExecutionTime;
    }

    public double getExecutionTime() {
        switch (distributionType) {
            case EXPONENTIAL: {
                double value = Math.log(1 - random.nextDouble()) / (-1.0 / averageExecutionTime);
                return value;
            }

            case UNIFORM: {
                double value = random.nextDouble() * (maxExecutionTime - minExecutionTime + 1) + minExecutionTime;
                return value;
            }

            default: // Big error
        }

        return -1;
    }

    public void setListener(ServerListener listener) {
        this.listener = listener;
    }

    public void handleEvent(double time) {
        Request old = currentRequest;
        currentRequest = null;
        if (time > lastWorkingTime) {
            lastWorkingTime = time;
        }

        if (time > warmupTime) {
            completed++;
            requestTime += (time - old.startServerTime);
        }

        listener.serverFinished(this, old, finishTime);
    }

    public void execute(boolean measuring, double currentTime, Request request, double finishTime) {
        this.currentRequest = request;
        this.finishTime = finishTime;

        if (measuring) {
            idleTime += (currentTime - lastWorkingTime);
        }
    }

    public boolean isBusy() {
        return currentRequest != null;
    }

    public double getExploitation(double simulationTime) {
        return (simulationTime - idleTime) / simulationTime * 100;
    }

    public double getThroughput(double simulationTime) {
        return (double) completed / simulationTime;
    }

    public double getResponseTime() {
        return (double) requestTime / completed;
    }

    public void addLastTime(double currentTime) {
        if (currentRequest == null) {
            idleTime += (currentTime - lastWorkingTime);
        }
    }

    public interface ServerListener {
        void serverFinished(Server server, Request request, double finishTime);
    }

}