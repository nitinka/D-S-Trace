package nitinka.dstrace;

/**
 * User : NitinK.Agarwal@yahoo.com
 */

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import nitinka.dstrace.config.DsTraceConsoleConfiguration;
import nitinka.dstrace.resource.EventResource;
import nitinka.dstrace.fetch.AbstractFetchClient;
import nitinka.jmetrics.JMetric;
import nitinka.jmetrics.controller.dropwizard.JMetricController;

public class DsTraceConsoleService extends Service<DsTraceConsoleConfiguration> {

    public DsTraceConsoleService() {}

    @Override
    public void initialize(Bootstrap<DsTraceConsoleConfiguration> bootstrap) {
        bootstrap.setName("loader-server");
    }

    @Override
    public void run(DsTraceConsoleConfiguration configuration, Environment environment) throws Exception {
        JMetric.initialize(configuration.getjMetricConfig());
        environment.addResource(new JMetricController());
        environment.addResource(new EventResource(AbstractFetchClient.build(configuration.getEventFetchConfig())));
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"server",args[0]};
        new DsTraceConsoleService().run(args);
    }

}
