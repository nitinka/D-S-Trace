package nitinka.dstrace.conf;

/**
 * User: NitinK.Agarwal@yahoo.com
 * Configuration required to initialize Tracer
 */
public class TracerConfiguration {
    private boolean enabled = false;
    private String host;
    private String businessUnit;
    private String application;
    private int samplePercentage;
    private int eventQueueSize = 100000;

    private EventPublishConfiguration publishConfiguration;
    private TracerFilterConfig tracerFilterConfig;

    public boolean isEnabled() {
        return enabled;
    }

    public TracerConfiguration setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getHost() {
        return host;
    }

    public TracerConfiguration setHost(String host) {
        this.host = host;
        return this;
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public TracerConfiguration setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public TracerConfiguration setApplication(String application) {
        this.application = application;
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

    public TracerFilterConfig getTracerFilterConfig() {
        return tracerFilterConfig;
    }

    public void setTracerFilterConfig(TracerFilterConfig tracerFilterConfig) {
        this.tracerFilterConfig = tracerFilterConfig;
    }
}
