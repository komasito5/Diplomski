package implementation.logic;

import implementation.components.*;
import parsing.util.ComponentPercentage;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class NavigationLogic {

    private Random random = new Random();

    private Map<String, Component> components;

    public void init(Map<String, Component> components) {
        this.components = components;
    }

    // Move request
    public void navigate(String componentId, Request request) {
        Component component = components.get(componentId);

        switch (component.getType()) {
            case EQUIVALENT_SERVERS: {
                EquivalentServers servers = (EquivalentServers) component;
                Component exitComponent = components.get(servers.getOutId());
                exitComponent.addNewRequest(request);

                break;
            }

            case JOIN: {
                Join join = (Join) component;
                Component exitComponent = components.get(join.getOutId());
                exitComponent.addNewRequest(request);

                break;
            }

            case BRANCHING: {
                Branching branching = (Branching) component;
                String u;
                Component exitComponent = components.get(u = getComponentIdForBranching(branching));
                exitComponent.addNewRequest(request);

                break;
            }

            default: // Big error
        }
    }

    // Get component from branching
    private String getComponentIdForBranching(Branching branching) {
        double sum = 0;
        for (ComponentPercentage branch : branching.getBranches()) {
            sum += branch.percentage;
        }

        double randomNum = 1 + (sum - 1) * random.nextDouble();

        List<ComponentPercentage> branches = branching.getBranches();
        double cumulativeProbability = 0.0;
        for (ComponentPercentage componentPercentage : branches) {
            cumulativeProbability += componentPercentage.percentage;
            if (randomNum <= cumulativeProbability) return componentPercentage.componentId;
        }

        // Big error
        return null;
    }

}