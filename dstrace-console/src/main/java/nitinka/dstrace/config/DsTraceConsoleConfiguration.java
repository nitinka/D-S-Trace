package nitinka.dstrace.config;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 25/10/12
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */

import com.yammer.dropwizard.config.Configuration;
import nitinka.jmetrics.JMetricConfig;

public class DsTraceConsoleConfiguration extends Configuration {
    private String appName;
    private EventFetchConfig eventFetchConfig;
    private JMetricConfig jMetricConfig;

    private static DsTraceConsoleConfiguration instance;

    public DsTraceConsoleConfiguration() {
        instance = this;
    }

    public static DsTraceConsoleConfiguration instance() {
        return instance;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public JMetricConfig getjMetricConfig() {
        return jMetricConfig;
    }

    public void setjMetricConfig(JMetricConfig jMetricConfig) {
        this.jMetricConfig = jMetricConfig;
    }

    public EventFetchConfig getEventFetchConfig() {
        return eventFetchConfig;
    }

    public void setEventFetchConfig(EventFetchConfig eventFetchConfig) {
        this.eventFetchConfig = eventFetchConfig;
    }
}


