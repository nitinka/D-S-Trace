package nitinka.dstrace.domain;

/**
 * User: NitinK.Agarwal@yahoo.com
 {
 "appName" : "scp",
 "configName" : "addPayment",
 "profile" : "oms-b2c",
 "instanceId" :  "127.0.0.1",
 "eventId" : "4",
 "timestamp" : 1377842142000
 } */
public class Header {
    private String appName;
    private String configName;
    private String profile;
    private String instanceId;
    private String eventId;
    private long timestamp;

    public String getAppName() {
        return appName;
    }

    public Header setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getConfigName() {
        return configName;
    }

    public Header setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public String getProfile() {
        return profile;
    }

    public Header setProfile(String profile) {
        this.profile = profile;
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public Header setInstanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    public String getEventId() {
        return eventId;
    }

    public Header setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Header setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
