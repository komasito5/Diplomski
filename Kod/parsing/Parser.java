package parsing;

import parsing.descriptors.*;
import parsing.util.ComponentPercentage;
import parsing.util.Constants;

import java.io.*;
import java.util.*;

public class Parser {

    private static File selectedFile;
    private static Scanner fileScanner;
    private static StringBuilder errorLog;

    private static Map<String, Descriptor> descriptors;
    private static List<Descriptor> queueDescriptors;
    private static SimulationDescriptor simulationDescriptor;

    private static List<String> alreadyDefinedIds;
    private static boolean simulationAlreadyDefined;

    private static int descriptorOrdinal;
    private static boolean canPerformAnalytics;

    private static ComponentType type;
    private static String id;
    private static QueueDescriptor.QueueType queueType;
    private static QueueDescriptor.ServingDiscipline servingDiscipline;
    private static String exitId;
    private static List<ComponentPercentage> branches;
    private static ServerDescriptor.DistributionType distributionType;
    private static double minExecutionTime;
    private static double maxExecutionTime;
    private static double executionTime;
    private static int requestNumber;
    private static List<Integer> requestPriority;
    private static List<String> requestStartingQueue;
    private static double warmupTime;
    private static double simulationTime;
    private static boolean localError;

    private static boolean errorOccured;

    private static boolean finishedWithStars;

    public static void init(File file) throws FileNotFoundException {
        selectedFile = file;
        fileScanner = new Scanner(selectedFile);

        descriptors = new HashMap<>();
        queueDescriptors = new ArrayList<>();
        simulationDescriptor = null;
        alreadyDefinedIds = new ArrayList<>();
        simulationAlreadyDefined = false;
        descriptorOrdinal = 0;
        canPerformAnalytics = true;
        errorOccured = false;
        resetValues();
    }

    private static void resetValues() {
        type = null;
        id = null;
        queueType = null;
        servingDiscipline = null;
        exitId = null;
        branches = new ArrayList<>();
        distributionType = null;
        minExecutionTime = -1;
        maxExecutionTime = -1;
        executionTime = -1;
        requestNumber = -1;
        requestPriority = new ArrayList<>();
        requestStartingQueue = new ArrayList<>();
        warmupTime = -1;
        simulationTime = -1;
        localError = false;

        finishedWithStars = false;
    }

    private static String getNextLine() {
        if (fileScanner.hasNext()) {
            return fileScanner.nextLine().replaceAll(Constants.WHITESPACE_REGEX, Constants.EMPTY_STRING);
        }

        return null;
    }

    private static void proccessToken(String line, boolean firstLine) {
        // ***
        if (Constants.COMPONENT_START.equals(line)) {
            if (!firstLine) {
                Descriptor descriptor = produceDescriptor();
                if (descriptor != null) {
                    Descriptor old = descriptors.getOrDefault(id, null);
                    if (old != null) {
                        System.out.println("Id " + id + " je vec definisan!");
                        errorOccured = true;
                        if (!alreadyDefinedIds.contains(old.id)) {
                            alreadyDefinedIds.add(old.id);
                        }
                    } else {
                        descriptors.put(id, descriptor);
                        if (descriptor.componentType == ComponentType.QUEUE) {
                            queueDescriptors.add(descriptor);
                        }
                    }
                }
            }

            descriptorOrdinal++;
            errorLog.append("***Deskriptor broj " + descriptorOrdinal + "***");
            errorLog.append(System.getProperty("line.separator"));
            resetValues();
            finishedWithStars = true;
            return;
        }

        finishedWithStars = false;

        String[] lineParts = line.split(Constants.FIELD_VALUE_SEPARATOR);
        String field = lineParts[Constants.FIELD_INDEX];
        String value = lineParts[Constants.VALUE_INDEX];

        switch (field) {
            case Constants.TYPE: {
                if (type == null) {
                    switch (value) {
                        case Constants.QUEUE: {
                            type = ComponentType.QUEUE;
                            break;
                        }

                        case Constants.SERVER: {
                            type = ComponentType.SERVER;
                            break;
                        }

                        case Constants.JOIN: {
                            type = ComponentType.JOIN;
                            break;
                        }

                        case Constants.BRANCHING: {
                            type = ComponentType.BRANCHING;
                            break;
                        }

                        case Constants.SIMULATION: {
                            type = ComponentType.SIMULATION;
                            break;
                        }

                        default:
                            errorOccured = true;
                    }
                } else {
                    addToErrorLog(Constants.TYPE_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.ID: {
                if (id == null) {
                    id = value;
                } else {
                    addToErrorLog(Constants.ID_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.PRINCIPLE: {
                if (queueType == null) {
                    switch (value) {
                        case Constants.FIFO: {
                            queueType = QueueDescriptor.QueueType.FIFO_QUEUE;
                            break;
                        }

                        case Constants.LIFO: {
                            queueType = QueueDescriptor.QueueType.LIFO_QUEUE;
                            break;
                        }

                        case Constants.PRIORITY_QUEUE: {
                            queueType = QueueDescriptor.QueueType.PRIORITY_QUEUE;
                            break;
                        }

                        case Constants.RANDOM: {
                            queueType = QueueDescriptor.QueueType.RANDOM_QUEUE;
                            break;
                        }

                        default: {
                            addToErrorLog(Constants.UNKNOWN_QUEUE_PRINCIPLE + ": " + value);
                            errorOccured = true;
                            localError = true;
                        }
                    }
                } else {
                    addToErrorLog(Constants.QUEUE_PRINCIPLE_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.SERVER_PRINCIPLE: {
                if (servingDiscipline == null) {
                    switch (value) {
                        case Constants.FIRST_FREE: {
                            servingDiscipline = QueueDescriptor.ServingDiscipline.FIRST_FREE_SERVER;
                            break;
                        }

                        case Constants.LONGEST_FREE: {
                            servingDiscipline = QueueDescriptor.ServingDiscipline.LONGEST_FREE_SERVER;
                            break;
                        }

                        case Constants.RANDOM: {
                            servingDiscipline = QueueDescriptor.ServingDiscipline.RANDOM_SERVER;
                            break;
                        }

                        default: {
                            addToErrorLog(Constants.UNKNOWN_SERVING_DISCIPLINE + ": " + value);
                            errorOccured = true;
                            localError = true;
                        }
                    }
                } else {
                    addToErrorLog(Constants.SERVING_DISCIPLINE_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.EXIT: {
                if (exitId == null) {
                    exitId = value;
                } else {
                    addToErrorLog(Constants.EXIT_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.BRANCH: {
                String[] componentProbability = value.split(Constants.OUTGOING_TRAFFIC_SEPARATOR);

                String componentId = componentProbability[Constants.OUTGOING_TRAFFIC_ID_INDEX];
                ComponentPercentage componentPercentage = new ComponentPercentage();
                componentPercentage.componentId = componentId;

                if (componentProbability.length == 2) {
                    Double percentage = Double.parseDouble(componentProbability[Constants.OUTGOING_TRAFFIC_PROBABILITY_INDEX]);
                    componentPercentage.percentage = percentage;
                }

                for (ComponentPercentage branch : branches) {
                    if (branch.componentId == componentId) {
                        addToErrorLog(Constants.BRANCH_ALREADY_DEFINED + componentId + Constants.DEFINED);
                        errorOccured = true;
                        localError = true;
                        return;
                    }
                }

                branches.add(componentPercentage);

                break;
            }

            case Constants.DISTRIBUTION: {
                if (distributionType == null) {
                    switch (value) {
                        case Constants.UNIFORM_DISTRIBUTION: {
                            distributionType = ServerDescriptor.DistributionType.UNIFORM;
                            canPerformAnalytics = false;
                            break;
                        }

                        case Constants.EXPONENTIAL_DISTRIBUTION: {
                            distributionType = ServerDescriptor.DistributionType.EXPONENTIAL;
                            break;
                        }

                        default: {
                            addToErrorLog(Constants.UNKNOWN_DISTRIBUTION + ": " + value);
                            errorOccured = true;
                            localError = true;
                        }
                    }
                } else {
                    addToErrorLog(Constants.DISTRIBUTION_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.MIN_EXECUTION_TIME: {
                if (minExecutionTime == -1) {
                    minExecutionTime = Double.parseDouble(value);
                } else {
                    addToErrorLog(Constants.MIN_EXECUTION_TIME_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.MAX_EXECUTION_TIME: {
                if (maxExecutionTime == -1) {
                    maxExecutionTime = Double.parseDouble(value);
                } else {
                    addToErrorLog(Constants.MAX_EXECUTION_TIME_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.EXECUTION_TIME: {
                if (executionTime == -1) {
                    executionTime = Double.parseDouble(value);
                } else {
                    addToErrorLog(Constants.EXECUTION_TIME_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.REQUEST_NUMBER: {
                if (requestNumber == -1) {
                    requestNumber = Integer.parseInt(value);
                } else {
                    addToErrorLog(Constants.REQUEST_NUMBER_ALREADY_DEFINED_IN_COMPONENT_DESCRIPTOR);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.REQUEST_PRIORITY: {
                if (requestPriority.isEmpty()) {
                    String[] priorities = value.split(Constants.REQUEST_PRIORITY_SEPARATOR);
                    for (String priority : priorities) {
                        requestPriority.add(Integer.parseInt(priority));
                    }
                } else {
                    addToErrorLog(Constants.REQUEST_PRIORITY_ALREADY_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.REQUEST_STARTING_QUEUE: {
                if (requestStartingQueue.isEmpty()) {
                    String[] queueIds = value.split(Constants.REQUEST_STARTING_QUEUE_SEPARATOR);
                    for (String id : queueIds) {
                        requestStartingQueue.add(id);
                    }
                } else {
                    addToErrorLog(Constants.REQUEST_START_POINT_ALREADY_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.WARMUP_TIME: {
                if (warmupTime == -1) {
                    warmupTime = Double.parseDouble(value);
                } else {
                    addToErrorLog(Constants.WARMUP_TIME_ALREADY_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case Constants.SIMULATION_TIME: {
                if (simulationTime == -1) {
                    simulationTime = Double.parseDouble(value);
                } else {
                    addToErrorLog(Constants.SIMULATION_TIME_ALREADY_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            default: {
                addToErrorLog(Constants.UNKNOWN_TOKEN + ": " + field);
                errorOccured = true;
                localError = true;
            }
        }
    }

    private static Descriptor produceDescriptor() {
        if (type == null) {
            addToErrorLog(Constants.TYPE_NOT_DEFINED);
            errorOccured = true;
            localError = true;
        }

        if (id == null && type != ComponentType.SIMULATION) {
            addToErrorLog(Constants.ID_NOT_DEFINED);
            errorOccured = true;
            localError = true;
        }

        if (type != null) {
            checkIncompatibleTokens(type);
        }

        if (localError) {
            return null;
        }

        switch (type) {
            case QUEUE: {
                QueueDescriptor descriptor = new QueueDescriptor();
                descriptor.id = id;
                descriptor.componentType = ComponentType.QUEUE;

                if (queueType == null) {
                    descriptor.type = QueueDescriptor.QueueType.FIFO_QUEUE;
                } else {
                    descriptor.type = queueType;
                }

                if (servingDiscipline != null) {
                    descriptor.servingDiscipline = servingDiscipline;
                }

                if (exitId != null) {
                    descriptor.exitId = exitId;
                } else {
                    addToErrorLog(Constants.EXIT_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                if (localError) {
                    return null;
                }

                addToErrorLog(Constants.NO_ERRORS);
                return descriptor;
            }

            case SERVER: {
                ServerDescriptor descriptor = new ServerDescriptor();
                descriptor.id = id;
                descriptor.componentType = ComponentType.SERVER;

                if (distributionType == null) {
                    descriptor.distributionType = ServerDescriptor.DistributionType.EXPONENTIAL;
                } else {
                    descriptor.distributionType = distributionType;
                }

                if (executionTime == -1 && descriptor.distributionType == ServerDescriptor.DistributionType.EXPONENTIAL) {
                    addToErrorLog(Constants.EXECUTION_TIME_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                } else {
                    descriptor.averageExecutionTime = executionTime;
                }

                if (minExecutionTime == -1 && descriptor.distributionType == ServerDescriptor.DistributionType.UNIFORM) {
                    addToErrorLog(Constants.MIN_EXECUTION_TIME_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                } else {
                    descriptor.minExecutionTime = minExecutionTime;
                }

                if (maxExecutionTime == -1 && descriptor.distributionType == ServerDescriptor.DistributionType.UNIFORM) {
                    addToErrorLog(Constants.MAX_EXECUTION_TIME_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                } else {
                    descriptor.maxExecutionTime = maxExecutionTime;
                }

                if (minExecutionTime != -1 && maxExecutionTime != -1) {
                    if (minExecutionTime > maxExecutionTime) {
                        addToErrorLog(Constants.MIN_EXECUTION_TIME_GREATER_THAN_MAX);
                        errorOccured = true;
                        localError = true;
                    }
                }

                if (exitId != null) {
                    descriptor.exitId = exitId;
                } else {
                    addToErrorLog(Constants.EXIT_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                if (localError) {
                    return null;
                }

                addToErrorLog(Constants.NO_ERRORS);
                return descriptor;
            }

            case JOIN: {
                JoinDescriptor descriptor = new JoinDescriptor();
                descriptor.id = id;
                descriptor.componentType = ComponentType.JOIN;

                if (exitId != null) {
                    descriptor.exitId = exitId;
                } else {
                    addToErrorLog(Constants.EXIT_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                if (localError) {
                    return null;
                }

                addToErrorLog(Constants.NO_ERRORS);
                return descriptor;
            }

            case BRANCHING: {
                BranchingDescriptor descriptor = new BranchingDescriptor();
                descriptor.id = id;
                descriptor.componentType = ComponentType.BRANCHING;

                if (!branches.isEmpty()) {
                    descriptor.branches = branches;
                } else {
                    addToErrorLog(Constants.BRANCHES_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                }

                if (localError) {
                    return null;
                }

                addToErrorLog(Constants.NO_ERRORS);
                return descriptor;
            }

            case SIMULATION: {
                if (simulationDescriptor != null) {
                    simulationAlreadyDefined = true;
                    errorOccured = true;
                    localError = true;
                }

                simulationDescriptor = new SimulationDescriptor();
                if (requestNumber == -1) {
                    addToErrorLog(Constants.REQUEST_NUMBER_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                } else {
                    simulationDescriptor.requestNumber = requestNumber;
                }

                if (!requestStartingQueue.isEmpty()) {
                    if (requestStartingQueue.size() == requestNumber) {
                        simulationDescriptor.requestStartingQueues = requestStartingQueue;
                    } else {
                        addToErrorLog(Constants.REQUEST_STARTING_QUEUE_ERROR);
                        errorOccured = true;
                        localError = true;
                    }
                }

                if (requestPriority.isEmpty()) {
                    simulationDescriptor.requestPriority = new ArrayList<>();
                    for (int i = 0; i < simulationDescriptor.requestNumber; i++) {
                        simulationDescriptor.requestPriority.add(1);
                    }
                } else {
                    if (requestPriority.size() == requestNumber) {
                        simulationDescriptor.requestPriority = requestPriority;
                    } else {
                        addToErrorLog(Constants.REQUEST_PRIORITY_ERROR);
                        errorOccured = true;
                        localError = true;
                    }
                }

                if (warmupTime == -1) {
                    addToErrorLog(Constants.WARMUP_TIME_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                } else {
                    simulationDescriptor.warmupTime = warmupTime;
                }

                if (simulationTime == -1) {
                    addToErrorLog(Constants.SIMULATION_TIME_NOT_DEFINED);
                    errorOccured = true;
                    localError = true;
                } else {
                    simulationDescriptor.simulationTime = simulationTime;
                }

                addToErrorLog(Constants.NO_ERRORS);
            }
        }

        return null;
    }

    private static void checkIncompatibleTokens(ComponentType type) {
        switch (type) {
            case QUEUE: {
                if (!branches.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Grane");
                    errorOccured = true;
                    localError = true;
                }

                if (distributionType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Raspodela");
                    errorOccured = true;
                    localError = true;
                }

                if (executionTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "ProsecnoVremeObrade");
                    errorOccured = true;
                    localError = true;
                }

                if (requestNumber != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "BrojZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestPriority.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrioritetZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestStartingQueue.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "InicijalniRedZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (warmupTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeZagrevanja");
                    errorOccured = true;
                    localError = true;
                }

                if (simulationTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeSimulacije");
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case SERVER: {
                if (!branches.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Grane");
                    errorOccured = true;
                    localError = true;
                }

                if (distributionType == ServerDescriptor.DistributionType.EXPONENTIAL) {
                    if (minExecutionTime != -1) {
                        addToErrorLog(Constants.INVALID_TOKEN + ": " + "MinimalnoVremeObrade");
                        errorOccured = true;
                        localError = true;
                    }

                    if (maxExecutionTime != -1) {
                        addToErrorLog(Constants.INVALID_TOKEN + ": " + "MaksimalnoVremeObrade");
                        errorOccured = true;
                        localError = true;
                    }
                }

                if (distributionType == ServerDescriptor.DistributionType.UNIFORM) {
                    if (executionTime != -1) {
                        addToErrorLog(Constants.INVALID_TOKEN + ": " + "ProsecnoVremeObrade");
                        errorOccured = true;
                        localError = true;
                    }
                }

                if (servingDiscipline != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipOdabiraServera");
                    errorOccured = true;
                    localError = true;
                }

                if (queueType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipRada");
                    errorOccured = true;
                    localError = true;
                }

                if (requestNumber != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "BrojZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestPriority.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrioritetZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestStartingQueue.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "InicijalniRedZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (warmupTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeZagrevanja");
                    errorOccured = true;
                    localError = true;
                }

                if (simulationTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeSimulacije");
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case JOIN: {
                if (!branches.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Grane");
                    errorOccured = true;
                    localError = true;
                }

                if (servingDiscipline != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipOdabiraServera");
                    errorOccured = true;
                    localError = true;
                }

                if (distributionType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Raspodela");
                    errorOccured = true;
                    localError = true;
                }

                if (executionTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "ProsecnoVremeObrade");
                    errorOccured = true;
                    localError = true;
                }

                if (queueType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipRada");
                    errorOccured = true;
                    localError = true;
                }

                if (requestNumber != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "BrojZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestPriority.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrioritetZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestStartingQueue.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "InicijalniRedZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (warmupTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeZagrevanja");
                    errorOccured = true;
                    localError = true;
                }

                if (simulationTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeSimulacije");
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case BRANCHING: {
                if (exitId != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Izlaz");
                    errorOccured = true;
                    localError = true;
                }

                if (servingDiscipline != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipOdabiraServera");
                    errorOccured = true;
                    localError = true;
                }

                if (distributionType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Raspodela");
                    errorOccured = true;
                    localError = true;
                }

                if (executionTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "ProsecnoVremeObrade");
                    errorOccured = true;
                    localError = true;
                }

                if (queueType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipRada");
                    errorOccured = true;
                    localError = true;
                }

                if (requestNumber != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "BrojZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestPriority.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrioritetZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (!requestStartingQueue.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "InicijalniRedZahteva");
                    errorOccured = true;
                    localError = true;
                }

                if (warmupTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeZagrevanja");
                    errorOccured = true;
                    localError = true;
                }

                if (simulationTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "VremeSimulacije");
                    errorOccured = true;
                    localError = true;
                }

                break;
            }

            case SIMULATION: {
                if (exitId != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Izlaz");
                    errorOccured = true;
                    localError = true;
                }

                if (servingDiscipline != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipOdabiraServera");
                    errorOccured = true;
                    localError = true;
                }

                if (distributionType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Raspodela");
                    errorOccured = true;
                    localError = true;
                }

                if (executionTime != -1) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "ProsecnoVremeObrade");
                    errorOccured = true;
                    localError = true;
                }

                if (queueType != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "PrincipRada");
                    errorOccured = true;
                    localError = true;
                }

                if (id != null) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Id");
                    errorOccured = true;
                    localError = true;
                }

                if (!branches.isEmpty()) {
                    addToErrorLog(Constants.INVALID_TOKEN + ": " + "Grane");
                    errorOccured = true;
                    localError = true;
                }

                break;
            }
        }
    }

    private static void globalAnalisys() {
        addToErrorLog(Constants.GLOBAL_ANALYSIS);

        if (simulationDescriptor == null) {
            addToErrorLog(Constants.SIMULATION_PARAMETERS_NOT_DEFINED);
            errorOccured = true;
        }

        if (simulationAlreadyDefined) {
            addToErrorLog(Constants.SIMULATION_PARAMETERS_ALREADY_DEFINED);
        }

        Map<String, Boolean> visited = new HashMap<>();
        Map<String, Boolean> alreadyWritten = new HashMap<>();
        Map<String, Boolean> equivalentServersBranches = new HashMap<>();

        for (String id : descriptors.keySet()) {
            Descriptor descriptor = descriptors.get(id);

            if (descriptor.componentType == ComponentType.QUEUE) {
                QueueDescriptor queueDescriptor = (QueueDescriptor) descriptor;
                Descriptor outDescriptor = descriptors.getOrDefault(queueDescriptor.exitId, null);

                if (outDescriptor == null) {
                    addToErrorLog(componentNotDefined(queueDescriptor.exitId));
                    errorOccured = true;
                } else {
                    if (outDescriptor.componentType != ComponentType.SERVER && outDescriptor.componentType != ComponentType.BRANCHING) {
                        addToErrorLog(componentWithExitError(queueDescriptor.id));
                        errorOccured = true;
                    } else {
                        String joinId = null;

                        if (outDescriptor.componentType == ComponentType.BRANCHING) {
                            BranchingDescriptor branchingDescriptor = (BranchingDescriptor) outDescriptor;
                            equivalentServersBranches.put(branchingDescriptor.id, true);
                            for (ComponentPercentage componentPercentage : branchingDescriptor.branches) {
                                Descriptor branchDescriptor = descriptors.getOrDefault(componentPercentage.componentId, null);
                                if (branchDescriptor != null) {
                                    if (branchDescriptor.componentType != ComponentType.SERVER) {
                                        addToErrorLog("Ekvivalentni server sa redom " + queueDescriptor.id + " nije definisan ispravno");
                                        errorOccured = true;
                                        break;
                                    }

                                    ServerDescriptor serverDescriptor = (ServerDescriptor) branchDescriptor;
                                    Descriptor serverOutDescriptor = descriptors.getOrDefault(serverDescriptor.exitId, null);
                                    if (serverOutDescriptor != null) {
                                        if (serverOutDescriptor.componentType != ComponentType.JOIN) {
                                            addToErrorLog("Ekvivalentni server sa redom " + queueDescriptor.id + " nije definisan ispravno");
                                            errorOccured = true;
                                            break;
                                        }

                                        if (joinId == null) {
                                            joinId = serverOutDescriptor.id;
                                        } else {
                                            if (!joinId.equals(serverOutDescriptor.id)) {
                                                addToErrorLog("Ekvivalentni server sa redom " + queueDescriptor.id + " nije definisan ispravno");
                                                errorOccured = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        visited.put(outDescriptor.id, true);
                    }
                }
            }
        }

        for (String id : descriptors.keySet()) {
            Descriptor descriptor = descriptors.get(id);

            switch (descriptor.componentType) {
                case SERVER: {
                    ServerDescriptor server = (ServerDescriptor) descriptor;
                    Descriptor outDescriptor = descriptors.getOrDefault(server.exitId, null);

                    if (outDescriptor == null) {
                        addToErrorLog(componentNotDefined(server.exitId));
                        errorOccured = true;
                    } else {
                        if (outDescriptor.componentType != ComponentType.JOIN) {
                            if (visited.getOrDefault(outDescriptor.id, false) && !alreadyWritten.getOrDefault(outDescriptor.id, false)) {
                                addToErrorLog(componentHasMultiplePredecessors(outDescriptor.id));
                                errorOccured = true;
                                alreadyWritten.put(outDescriptor.id, true);
                            } else {
                                visited.put(outDescriptor.id, true);
                            }
                        } else {
                            visited.put(outDescriptor.id, true);
                        }
                    }

                    break;
                }

                case JOIN: {
                    JoinDescriptor joinDescriptor = (JoinDescriptor) descriptor;
                    Descriptor outDescriptor = descriptors.getOrDefault(joinDescriptor.exitId, null);

                    if (outDescriptor == null) {
                        addToErrorLog(componentNotDefined(joinDescriptor.exitId));
                        errorOccured = true;
                    } else {
                        if (outDescriptor.componentType != ComponentType.JOIN) {
                            if (visited.getOrDefault(outDescriptor.id, false) && !alreadyWritten.getOrDefault(outDescriptor.id, false)) {
                                addToErrorLog(componentHasMultiplePredecessors(outDescriptor.id));
                                errorOccured = true;
                                alreadyWritten.put(outDescriptor.id, true);
                            } else {
                                visited.put(outDescriptor.id, true);
                            }
                        } else {
                            visited.put(outDescriptor.id, true);
                        }
                    }

                    break;
                }

                case BRANCHING: {
                    BranchingDescriptor branchingDescriptor = (BranchingDescriptor) descriptor;
                    double sum = 0;
                    boolean shouldCheckSum = !equivalentServersBranches.getOrDefault(branchingDescriptor.id, false);
                    for (ComponentPercentage componentPercentage : branchingDescriptor.branches) {
                        if (shouldCheckSum) {
                            sum += componentPercentage.percentage;
                        }

                        Descriptor branch = descriptors.getOrDefault(componentPercentage.componentId, null);
                        if (branch == null) {
                            addToErrorLog(componentNotDefined(componentPercentage.componentId));
                            errorOccured = true;
                        } else {
                            if (branch.componentType != ComponentType.JOIN) {
                                if (visited.getOrDefault(branch.id, false) && !alreadyWritten.getOrDefault(branch.id, false)) {
                                    addToErrorLog(componentHasMultiplePredecessors(componentPercentage.componentId));
                                    errorOccured = true;
                                    alreadyWritten.put(branch.id, true);
                                } else {
                                    visited.put(branch.id, true);
                                }
                            } else {
                                visited.put(branch.id, true);
                            }
                        }
                    }

                    if (shouldCheckSum) {
                        if (Math.abs(100 - sum) > 0.1) {
                            addToErrorLog(componentWithBranchError(branchingDescriptor.id));
                            errorOccured = true;
                        }
                    }

                    break;
                }
            }
        }

        for (String id : descriptors.keySet()) {
            if (!visited.getOrDefault(id, false)) {
                addToErrorLog(componentHanging(id));
            }
        }

        for (String id : alreadyDefinedIds) {
            addToErrorLog(multipleDefinitions(id));
        }

        if (!errorOccured && requestStartingQueue.isEmpty()) {
            Random random = new Random();
            simulationDescriptor.requestStartingQueues = new ArrayList<>();
            for (int i = 0; i < simulationDescriptor.requestNumber; i++) {
                simulationDescriptor.requestStartingQueues.add(queueDescriptors.get(random.nextInt(queueDescriptors.size())).id);
            }
        }
    }

    public static boolean parseFile() {
        errorLog = new StringBuilder();

        String line;
        boolean firstLine = true;
        while ((line = getNextLine()) != null) {
            proccessToken(line, firstLine);
            firstLine = false;
        }

        if (!finishedWithStars) {
            Descriptor descriptor = produceDescriptor();
            if (descriptor != null) {
                Descriptor old = descriptors.getOrDefault(id, null);
                if (old != null) {
                    errorOccured = true;
                    if (!alreadyDefinedIds.contains(old.id)) {
                        alreadyDefinedIds.add(old.id);
                    }
                } else {
                    descriptors.put(id, descriptor);
                    if (descriptor.componentType == ComponentType.QUEUE) {
                        queueDescriptors.add(descriptor);
                    }
                }
            }
        }

        globalAnalisys();

        if (errorOccured) {
            File errorFile = new File(selectedFile.getPath().substring(0, selectedFile.getPath().length() - 4) + "-ErrorLog.txt");
            try {
                if (errorFile.createNewFile()) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile));
                    writer.write(errorLog.toString());
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        } else {
            return true;
        }
    }

    private static void addToErrorLog(String error) {
        errorLog.append(error + System.getProperty("line.separator"));
    }

    private static String componentNotDefined(String id) {
        return Constants.COMPONENT_WITH_ID + id + Constants.NOT_DEFINED;
    }

    private static String componentHasMultiplePredecessors(String id) {
        return Constants.COMPONENT_WITH_ID + id + Constants.MORE_THAN_ONE_PREDECESSOR;
    }

    private static String componentWithExitError(String id) {
        return Constants.COMPONENT_WITH_ID + id + Constants.EXIT_ERROR;
    }

    private static String componentWithBranchError(String id) {
        return Constants.COMPONENT_WITH_ID + id + Constants.BRANCHES_SUM_ERROR;
    }

    private static String componentHanging(String id) {
        return Constants.COMPONENT_WITH_ID + id + Constants.COMPONENT_HANGING;
    }

    private static String multipleDefinitions(String id) {
        return id + Constants.MULTIPLE_DEFINITIONS;
    }

    public static Map<String, Descriptor> getDescriptors() {
        return descriptors;
    }

    public static List<Descriptor> getQueueDescriptors() {
        return queueDescriptors;
    }

    public static SimulationDescriptor getSimulationDescriptor() {
        return simulationDescriptor;
    }

    public static boolean canPerformAnalytics() {
        return canPerformAnalytics;
    }

}