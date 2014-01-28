package nitinka.dstrace.sample;

/**
 * User : NitinK.Agarwal@yahoo.com
 */

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import nitinka.dstrace.Tracer;
import nitinka.dstrace.filter.jersey.JerseyTracerFilter;
import nitinka.dstrace.sample.config.SampleAppConfiguration;
import nitinka.dstrace.sample.resource.HelloResource;

public class SampleAppService extends Service<SampleAppConfiguration> {

    public SampleAppService() {}

    @Override
    public void initialize(Bootstrap<SampleAppConfiguration> bootstrap) {
        bootstrap.setName("sample-app");
    }

    @Override
    public void run(SampleAppConfiguration configuration, Environment environment) throws Exception {
        environment.addResource(new HelloResource());


        //==================================================================================
        //Code to instrument resources and initialize Tracer
        //Use this code once you have added all your resources
        environment.addFilter(new JerseyTracerFilter(configuration.getTracerFilterConfig(),
                environment.getJerseyResourceConfig(),
                ""),
                "/*");
        Tracer.initialize(configuration.getTracerConfiguration());
        //Tracer Code ends here
        //==================================================================================
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"server",args[0]};
        new SampleAppService().run(args);
    }
}


