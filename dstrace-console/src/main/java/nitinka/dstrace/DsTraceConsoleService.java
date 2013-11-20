package nitinka.dstrace;

/**
 * User : NitinK.Agarwal@yahoo.com
 */

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import nitinka.dstrace.config.DsTraceConsoleConfiguration;
import nitinka.dstrace.resource.EventResource;
import nitinka.dstrace.fetch.AbstractFetchClient;
import nitinka.dstrace.resource.SpanResource;
import nitinka.dstrace.resource.TraceResource;
import nitinka.jmetrics.JMetric;
import nitinka.jmetrics.controller.dropwizard.JMetricController;

public class DsTraceConsoleService extends Service<DsTraceConsoleConfiguration> {

    public DsTraceConsoleService() {}

    @Override
    public void initialize(Bootstrap<DsTraceConsoleConfiguration> bootstrap) {
        bootstrap.setName("loader-server");
        bootstrap.addBundle(new AssetsBundle("/assets","/","/index.html"));
//        logger.info("Hit http://localhost:5555/index.htm?hyperionSearchUrl=stage-hyperion-api.digital.ch.flipkart.com:8401&traceId=TXN-26d68f3c-579b-4904-80f1-1070464df114");
    }

    @Override
    public void run(DsTraceConsoleConfiguration configuration, Environment environment) throws Exception {
        JMetric.initialize(configuration.getjMetricConfig());
        environment.addResource(new JMetricController());
        environment.addResource(new EventResource(AbstractFetchClient.build(configuration.getEventFetchConfig())));
        environment.addResource(new SpanResource(AbstractFetchClient.build(configuration.getEventFetchConfig())));
        environment.addResource(new TraceResource(AbstractFetchClient.build(configuration.getEventFetchConfig())));
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"server",args[0]};
        new DsTraceConsoleService().run(args);
    }
}


