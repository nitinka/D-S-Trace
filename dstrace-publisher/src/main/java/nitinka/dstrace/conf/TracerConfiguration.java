package nitinka.dstrace.conf;

import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.JsonGenerationException;

import java.io.IOException;

/**
 * User: NitinK.Agarwal@yahoo.com
 * Configuration required to initialize Tracer
 */
public class TracerConfiguration {
    private boolean enabled = false;
    private String instanceId;
    private String businessUnit;
    private String appName;
    private int samplePercentage;
    private int eventQueueSize = 100000;

    private EventPublishConfiguration publishConfiguration;

    public boolean isEnabled() {
        return enabled;
    }

    public TracerConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public TracerConfiguration setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public TracerConfiguration setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public TracerConfiguration setAppName(String appName) {
        this.appName = appName;
        return this;
    }
    public int getEventQueueSize() {
        return eventQueueSize;
    }

    public TracerConfiguration setEventQueueSize(int eventQueueSize) {
        this.eventQueueSize = eventQueueSize;
        return this;
    }

    public EventPublishConfiguration getPublishConfiguration() {
        return publishConfiguration;
    }

    public TracerConfiguration setPublishConfiguration(EventPublishConfiguration publishConfiguration) {
        this.publishConfiguration = publishConfiguration;
        return this;
    }

    public int getSamplePercentage() {
        return samplePercentage;
    }

    public TracerConfiguration setSamplePercentage(int samplePercentage) {
        this.samplePercentage = samplePercentage;
        return this;
    }
}
