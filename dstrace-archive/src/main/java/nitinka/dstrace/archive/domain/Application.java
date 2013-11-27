package nitinka.dstrace.archive.domain;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Application {
    private String name;
    private List<Operation> operations;

    public Application() {
        operations = new ArrayList<Operation>();
    }

    public String getName() {
        return name;
    }

    public Application setName(String name) {
        this.name = name;
        return this;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public Application setOperations(List<Operation> operations) {
        this.operations = operations;
        return this;
    }

    public Application addOperation(Operation operation) {
        this.operations.add(operation);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Application that = (Application) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
