package ui.util;

public class ResultInfo {

    public enum ResultType {
        EXPLOITATION,
        THROUGHPUT,
        RESPONSE_TIME
    }

    public String componentId;
    public double value;
    public ResultType type;

}