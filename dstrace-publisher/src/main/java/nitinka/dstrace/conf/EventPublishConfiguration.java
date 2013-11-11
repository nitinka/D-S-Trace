package nitinka.dstrace.conf;

import java.util.Map;

/**
 * User: NitinK.Agarwal@yahoo.com
 */

public class EventPublishConfiguration {
    private int eventPublishDelay = 5000; // Default 5000 ms
    private int eventPublishSize = 100;  // Default 100 messages per publish delay
    private boolean enqueueFailedMessages = false; // In case message publish fails enqueue them again
    private String publisherClass;
    private Map<String, Object> publisherConfig; // configuration specific to Publisher chosen

    public int getEventPublishDelay() {
        return eventPublishDelay;
    }

    public EventPublishConfiguration setEventPublishDelay(int eventPublishDelay) {
        this.eventPublishDelay = eventPublishDelay;
        return this;
    }

    public int getEventPublishSize() {
        return eventPublishSize;
    }

    public EventPublishConfiguration setEventPublishSize(int eventPublishSize) {
        this.eventPublishSize = eventPublishSize;
        return this;
    }

    public boolean isEnqueueFailedMessages() {
        return enqueueFailedMessages;
    }

    public EventPublishConfiguration setEnqueueFailedMessages(boolean enqueueFailedMessages) {
        this.enqueueFailedMessages = enqueueFailedMessages;
        return this;
    }

    public Map<String, Object> getPublisherConfig() {
        return publisherConfig;
    }

    public void setPublisherConfig(Map<String, Object> publisherConfig) {
        this.publisherConfig = publisherConfig;
    }

    public String getPublisherClass() {
        return publisherClass;
    }

    public void setPublisherClass(String publisherClass) {
        this.publisherClass = publisherClass;
    }
}
