package nitinka.dstrace.domain;

import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 27/11/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TraceRoute {
    private LinkedHashMap<String, Operation> operations;
    private String firstOperation;
    private String lastOperation;
    public TraceRoute() {
        this.operations = new LinkedHashMap<String, Operation>();
    }

    public LinkedHashMap<String, Operation> getOperations() {

        return operations;
    }

    public void setOperations(LinkedHashMap<String, Operation> operations) {
        this.operations = operations;
    }

    public void addOperation(Operation operation) {
        this.operations.put(operation.getOperationName(), operation);
    }

    public String getFirstOperation() {
        return firstOperation;
    }

    public void setFirstOperation(String firstOperation) {
        this.firstOperation = firstOperation;
    }

    public String getLastOperation() {
        return lastOperation;
    }

    public void setLastOperation(String lastOperation) {
        this.lastOperation = lastOperation;
    }
}
