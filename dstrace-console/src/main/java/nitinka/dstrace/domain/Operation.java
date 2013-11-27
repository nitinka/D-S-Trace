package nitinka.dstrace.domain;

import java.util.LinkedHashMap;

public class Operation {
    private String businessUnit;
    private String applicationName;
    private String operationName;
    private boolean multipleOccurrence;
    private LinkedHashMap<String, Operation> childOperations;

    public Operation() {
        this.childOperations = new LinkedHashMap<String, Operation>();
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public Operation setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
        return this;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Operation setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public String getOperationName() {
        return operationName;
    }

    public Operation setOperationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public boolean isMultipleOccurrence() {
        return multipleOccurrence;
    }

    public Operation setMultipleOccurrence(boolean multipleOccurrence) {
        this.multipleOccurrence = multipleOccurrence;
        return this;
    }

    public LinkedHashMap<String, Operation> getChildOperations() {
        return childOperations;
    }

    public Operation setChildOperations(LinkedHashMap<String, Operation> childOperations) {
        this.childOperations = childOperations;
        return this;
    }

    public Operation addChildOperation(Operation childOperation) {
        this.childOperations.put(childOperation.getOperationName(), childOperation);
        return this;
    }

    public static Operation build(Span span) {
        Operation operation = new Operation().
                setOperationName(span.getSpanName()).
                setApplicationName(span.getEvents().get(0).getApplication()).
                setBusinessUnit(span.getEvents().get(0).getBusinessUnit());

        for(Span childSpan : span.getChildSpans().values()) {
            Operation existingChildOperation = operation.getChildOperations().get(childSpan.getSpanName());
            if(existingChildOperation != null) {
                existingChildOperation.setMultipleOccurrence(true);
            }
            else {
                operation.addChildOperation(Operation.build(childSpan));
            }
        }
        return operation;
    }
}
