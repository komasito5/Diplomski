package implementation.logic;

import implementation.analytics.BuzenSolver;
import implementation.analytics.GordonNewellSolver;
import implementation.analytics.util.Probability;
import implementation.comparators.EventComparator;
import implementation.components.*;
import implementation.queues.*;
import implementation.queues.Queue;
import parsing.Parser;
import parsing.descriptors.*;
import parsing.util.ComponentPercentage;
import ui.Results;
import ui.util.ResultInfo;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationLogic implements Component.ComponentListener, Component.InfoListener {

    private double time;
    private double simulationTime;
    private double warmupTime;
    private int requestNumber;

    public final static AtomicInteger seq = new AtomicInteger(0);

    private boolean measuring;

    private Map<String, Component> components = new HashMap<>();
    private List<EquivalentServers> equivalentServers = new ArrayList<>();
    private Map<String, Server> servers = new HashMap<>();
    private NavigationLogic navigationLogic;

    private PriorityQueue<Event> unhandledEvents = new PriorityQueue<>(new EventComparator());

    private Map<String, Probability> probabilities;

    public void initSimulation(Map<String, Descriptor> descriptors,
                               List<Descriptor> queueDescriptors,
                               SimulationDescriptor simulationDescriptor) {
        simulationTime = simulationDescriptor.simulationTime;
        warmupTime = simulationDescriptor.warmupTime;
        requestNumber = simulationDescriptor.requestNumber;
        measuring = false;

        Map<String, Boolean> descriptorUsed = new HashMap<>();

        // Initialization of equivalent servers
        for (Descriptor descriptor : queueDescriptors) {
            EquivalentServers servers = new EquivalentServers();
            servers.setComponentListener(this);
            servers.setInfoListener(this);
            List<Server> serverList = new ArrayList<>();

            QueueDescriptor queueDescriptor = (QueueDescriptor) descriptor;
            descriptorUsed.put(queueDescriptor.id, true);
            String id = queueDescriptor.id;

            Queue queue = null;
            switch (queueDescriptor.type) {
                case FIFO_QUEUE: {
                    queue = new QueueFIFO();
                    break;
                }

                case LIFO_QUEUE: {
                    queue = new QueueLIFO();
                    break;
                }

                case PRIORITY_QUEUE: {
                    queue = new QueuePriority();
                    break;
                }

                case RANDOM_QUEUE: {
                    queue = new QueueRandom();
                    break;
                }

                default: // Big error
            }

            Descriptor out = descriptors.get(queueDescriptor.exitId);

            switch (out.componentType) {
                case SERVER: {
                    ServerDescriptor serverDescriptor = (ServerDescriptor) out;
                    descriptorUsed.put(serverDescriptor.id, true);
                    Server server;
                    serverList.add(server = new Server(
                            serverDescriptor.id,
                            serverDescriptor.distributionType,
                            serverDescriptor.averageExecutionTime,
                            serverDescriptor.minExecutionTime,
                            serverDescriptor.maxExecutionTime
                    ));
                    this.servers.put(serverDescriptor.id, server);

                    servers.setOutId(serverDescriptor.exitId);
                    break;
                }

                case BRANCHING: {
                    BranchingDescriptor branchingDescriptor = (BranchingDescriptor) out;
                    descriptorUsed.put(branchingDescriptor.id, true);
                    JoinDescriptor joinDescriptor = null;
                    for (ComponentPercentage element : branchingDescriptor.branches) {
                        ServerDescriptor serverDescriptor = (ServerDescriptor) descriptors.get(element.componentId);
                        descriptorUsed.put(serverDescriptor.id, true);
                        if (joinDescriptor == null) {
                            joinDescriptor = (JoinDescriptor) descriptors.get(serverDescriptor.exitId);
                        }

                        Server server;
                        serverList.add(server = new Server(
                                serverDescriptor.id,
                                serverDescriptor.distributionType,
                                serverDescriptor.averageExecutionTime,
                                serverDescriptor.minExecutionTime,
                                serverDescriptor.maxExecutionTime
                        ));
                        this.servers.put(serverDescriptor.id, server);
                    }

                    servers.setOutId(joinDescriptor.exitId);
                    descriptorUsed.put(joinDescriptor.id, true);
                    break;
                }

                default: // Big error
            }

            servers.init(id, queue, serverList, queueDescriptor);
            components.put(id, servers);
            equivalentServers.add(servers);
        }

        for (Descriptor descriptor : descriptors.values()) {
            boolean isUsed = descriptorUsed.getOrDefault(descriptor.id, false);

            if (!isUsed) {
                switch (descriptor.componentType) {
                    case JOIN: {
                        JoinDescriptor joinDescriptor = (JoinDescriptor) descriptor;
                        Join join = new Join();
                        join.setComponentListener(this);
                        join.init(joinDescriptor.id, joinDescriptor.exitId);

                        components.put(joinDescriptor.id, join);

                        break;
                    }

                    case BRANCHING: {
                        BranchingDescriptor branchingDescriptor = (BranchingDescriptor) descriptor;
                        Branching branching = new Branching();
                        branching.setComponentListener(this);
                        branching.init(branchingDescriptor.id, branchingDescriptor.branches);

                        components.put(branchingDescriptor.id, branching);

                        break;
                    }

                    default: // Big error
                }
            }
        }

        time = 0;

        navigationLogic = new NavigationLogic();
        navigationLogic.init(components);

        for (int i = 0; i < simulationDescriptor.requestNumber; i++) {
            Request request = new Request();
            request.requestId = i;
            request.priority = simulationDescriptor.requestPriority.get(i);

            EquivalentServers component = (EquivalentServers)components.get(simulationDescriptor.requestStartingQueues.get(i));
            component.initWaitingQueue(request);
        }

        for (Server server : servers.values()) {
            server.init(warmupTime);
        }

        for (EquivalentServers servers : equivalentServers) {
            servers.start();
        }

        if (Parser.canPerformAnalytics()) {
            handleAnalytics();
        }
    }

    private void handleAnalytics() {
        probabilities = new HashMap<>();

        for (EquivalentServers servers : equivalentServers) {
            List<Component> queue = new LinkedList<>();
            List<Double> probabilityTo = new LinkedList<>();

            queue.add(components.get(servers.getOutId()));
            probabilityTo.add(1.0);

            while (!queue.isEmpty()) {
                Component component = ((LinkedList<Component>) queue).poll();
                double probabilityToHere = ((LinkedList<Double>) probabilityTo).poll();

                switch (component.getType()) {
                    case EQUIVALENT_SERVERS: {
                        EquivalentServers serversTo = (EquivalentServers) component;

                        for (String fromId : servers.getServerIds()) {
                            for (String toId: serversTo.getServerIds()) {
                                Probability probability = new Probability();
                                probability.fromId = fromId;
                                probability.toId = toId;
                                probability.probability = probabilityToHere / serversTo.getServernNum();

                                Probability old = probabilities.getOrDefault(fromId + "-" + toId, null);
                                if (old != null) {
                                    probability.probability += old.probability;
                                }

                                probabilities.put(fromId + "-" + toId, probability);
                            }
                        }

                        break;
                    }

                    case JOIN: {
                        Join join = (Join) component;
                        queue.add(components.get(join.getOutId()));
                        probabilityTo.add(probabilityToHere);

                        break;
                    }

                    case BRANCHING: {
                        Branching branching = (Branching) component;

                        for (ComponentPercentage componentPercentage : branching.getBranches()) {
                            queue.add(components.get(componentPercentage.componentId));
                            probabilityTo.add(probabilityToHere * componentPercentage.percentage / 100);
                        }

                        break;
                    }
                }
            }
        }

        showAnalyticsResults();
    }

    public void startSimulation() {
        while(!unhandledEvents.isEmpty()) {
            Event event = unhandledEvents.poll();
            time = event.timestamp;

            if ((time > warmupTime) && (!measuring)) {
                measuring = true;
            }

            if (time > simulationTime + warmupTime && measuring) break;

            Server target = servers.get(event.componentFromId);
            target.handleEvent(event.timestamp);
        }

        for (Server server : servers.values()) {
            server.addLastTime(simulationTime + warmupTime);
        }
    }

    public void showAnalyticsResults() {
        int n = servers.size();

        double[] x = GordonNewellSolver.solveGordonNewellEquation(servers, probabilities);
        double[] g = BuzenSolver.solveBuzenAlgorithm(requestNumber, n, x);

        List<List<ResultInfo>> results = new ArrayList<>();
        results.add(new ArrayList<>());
        results.add(new ArrayList<>());

        int index = 0;
        for (Server server : servers.values()) {
            ResultInfo info1 = new ResultInfo();
            info1.componentId = server.getId();
            info1.value = g[requestNumber - 1] / g[requestNumber] * x[index++] * 100;
            info1.type = ResultInfo.ResultType.EXPLOITATION;
            results.get(0).add(info1);

            ResultInfo info2 = new ResultInfo();
            info2.componentId = server.getId();
            info2.value = info1.value / server.getAverageExecutionTime() / 100;
            info2.type = ResultInfo.ResultType.THROUGHPUT;
            results.get(1).add(info2);
        }

        Results resultsForm = new Results("Rezultati analiticke analize");
        resultsForm.init(results);
        resultsForm.pack();
        resultsForm.setLocationRelativeTo(null);
        resultsForm.setVisible(true);
    }

    public void showSimulationResults() {
        List<List<ResultInfo>> results = new ArrayList<>();
        results.add(new ArrayList<>());
        results.add(new ArrayList<>());
        results.add(new ArrayList<>());
        for (Server server : servers.values()) {
            ResultInfo info1 = new ResultInfo();
            info1.componentId = server.getId();
            info1.value = server.getExploitation(simulationTime);
            info1.type = ResultInfo.ResultType.EXPLOITATION;
            results.get(0).add(info1);

            ResultInfo info2 = new ResultInfo();
            info2.componentId = server.getId();
            info2.value = server.getThroughput(simulationTime);
            info2.type = ResultInfo.ResultType.THROUGHPUT;
            results.get(1).add(info2);

            ResultInfo info3 = new ResultInfo();
            info3.componentId = server.getId();
            info3.value = server.getResponseTime();
            info3.type = ResultInfo.ResultType.RESPONSE_TIME;
            results.get(2).add(info3);
        }

        Results resultsForm = new Results("Rezultati simulacione analize");
        resultsForm.init(results);
        resultsForm.pack();
        resultsForm.setLocationRelativeTo(null);
        resultsForm.setVisible(true);
    }

    public Map<String, Component> getComponents() {
        return components;
    }

    public int getCurrentPercentage() {
        return (int) ((time / (simulationTime + warmupTime)) * 100);
    }

    @Override
    public void componentFinished(String componentId, Request request) {
        navigationLogic.navigate(componentId, request);
    }

    @Override
    public void addEvent(Event event) {
        unhandledEvents.add(event);
    }

    @Override
    public double getCurrentTime() {
        return time;
    }

    @Override
    public double getSimulationTime() {
        return simulationTime;
    }

    @Override
    public double getWarmupTime() {
        return warmupTime;
    }

    @Override
    public boolean shouldMeasure() {
        return measuring;
    }
}