package implementation.components;

import parsing.descriptors.ComponentType;
import parsing.util.ComponentPercentage;

import java.util.List;

public class Branching extends Component {

    private List<ComponentPercentage> branches;

    // Initialize branching component
    public void init(String id, List<ComponentPercentage> branches) {
        this.id = id;
        this.type = ComponentType.BRANCHING;
        this.branches = branches;
    }

    public List<ComponentPercentage> getBranches() {
        return branches;
    }

    public int getBranchNum() {
        return branches.size();
    }

    @Override
    public void addNewRequest(Request request) {
        componentListener.componentFinished(id, request);
    }

}