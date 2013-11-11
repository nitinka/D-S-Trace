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

public class DsTraceArchiveConfiguration extends Configuration {
    private String appName;
    private RedisConsumerConfig redisConsumerConfig;
    private EventArchiveConfig eventArchiveConfig;
    private JMetricConfig jMetricConfig;

    private static DsTraceArchiveConfiguration instance;

    public DsTraceArchiveConfiguration() {
        instance = this;
    }

    public static DsTraceArchiveConfiguration instance() {
        return instance;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public RedisConsumerConfig getRedisConsumerConfig() {
        return redisConsumerConfig;
    }

    public void setRedisConsumerConfig(RedisConsumerConfig redisConsumerConfig) {
        this.redisConsumerConfig = redisConsumerConfig;
    }

    public JMetricConfig getjMetricConfig() {
        return jMetricConfig;
    }

    public void setjMetricConfig(JMetricConfig jMetricConfig) {
        this.jMetricConfig = jMetricConfig;
    }

    public EventArchiveConfig getEventArchiveConfig() {
        return eventArchiveConfig;
    }

    public void setEventArchiveConfig(EventArchiveConfig eventArchiveConfig) {
        this.eventArchiveConfig = eventArchiveConfig;
    }
}


