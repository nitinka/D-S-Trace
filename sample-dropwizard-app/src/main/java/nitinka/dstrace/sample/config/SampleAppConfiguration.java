package nitinka.dstrace.sample.config;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 25/10/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */

import com.yammer.dropwizard.config.Configuration;
import nitinka.dstrace.conf.TracerConfiguration;
import nitinka.dstrace.conf.TracerFilterConfig;

public class SampleAppConfiguration extends Configuration {
    private String appName;
    private TracerConfiguration tracerConfiguration;
    private TracerFilterConfig tracerFilterConfig;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public TracerConfiguration getTracerConfiguration() {
        return tracerConfiguration;
    }

    public void setTracerConfiguration(TracerConfiguration tracerConfiguration) {
        this.tracerConfiguration = tracerConfiguration;
    }

    public TracerFilterConfig getTracerFilterConfig() {
        return tracerFilterConfig;
    }

    public void setTracerFilterConfig(TracerFilterConfig tracerFilterConfig) {
        this.tracerFilterConfig = tracerFilterConfig;
    }
}


