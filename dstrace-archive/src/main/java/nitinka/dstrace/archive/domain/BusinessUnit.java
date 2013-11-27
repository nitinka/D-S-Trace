package nitinka.dstrace.archive.domain;

import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.JsonGenerationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 20/11/13
 * Time: 7:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class BusinessUnit {
    private String name;
    private List<Application> applications;

    public BusinessUnit() {
        applications = new ArrayList<Application>();
    }

    public String getName() {
        return name;
    }

    public BusinessUnit setName(String name) {
        this.name = name;
        return this;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public BusinessUnit setApplications(List<Application> applications) {
        this.applications = applications;
        return this;
    }

    public BusinessUnit addApplication(Application application) {
        this.applications.add(application);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusinessUnit that = (BusinessUnit) o;

        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public static void main(String[] args) throws IOException {
        BusinessUnit bu = new BusinessUnit().setName("BU1");
        List<Application> apps = new ArrayList<Application>();
        List<Operation> operations = new ArrayList<Operation>();
        operations.add(new Operation().setName("op1"));
        apps.add(new Application().setName("app1").setOperations(operations));

        bu.setApplications(apps);
        System.out.println(ObjectMapperUtil.instance().defaultPrettyPrintingWriter().writeValueAsString(bu));
    }

}
