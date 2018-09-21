package implementation.components;

import parsing.descriptors.ComponentType;

public class Join extends Component {

    private String outId;

    // Initialize join component
    public void init(String id, String outId) {
        this.id = id;
        this.type = ComponentType.JOIN;
        this.outId = outId;
    }

    public String getOutId() {
        return outId;
    }

    @Override
    public void addNewRequest(Request request) {
        componentListener.componentFinished(id, request);
    }

}