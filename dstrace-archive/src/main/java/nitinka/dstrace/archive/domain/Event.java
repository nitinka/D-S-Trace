package nitinka.dstrace.archive.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * User: NitinK.Agarwal@yahoo.com
 {
   "traceId": "2",
   "eventId" : "4",
   "parentEventId" : "4",
   "businessUnit" : "scp",
   "application" : "oms-b2c",
   "host" :  "127.0.0.1",
   "span" : "addPayment",
   "eventName" : "Started",
   "timestamp" : 1377842142000,
   "tags": {
     "order_id": "1"
   }
 }
 */

public class Event {
    private String businessUnit;
    private String application;
    private String host;
    private String spanName;
    private String traceId;
    private String eventId;
    private String spanId;
    private String parentSpanId;
    private String eventName;
    private long timestamp;
    private Map<String, Object> tags;

    public Event() {
        this.tags = new HashMap<String, Object>();
    }

    public String getBusinessUnit() {
        return businessUnit;
    }

    public Event setBusinessUnit(String businessUnit) {
        this.businessUnit = businessUnit;
        return this;
    }

    public String getSpanName() {
        return spanName;
    }

    public Event setSpanName(String spanName) {
        this.spanName = spanName;
        return this;
    }

    public String getApplication() {
        return application;
    }

    public Event setApplication(String application) {
        this.application = application;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Event setHost(String host) {
        this.host = host;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public Event setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public String getEventId() {
        return eventId;
    }

    public Event setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Event setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public Event setTags(Map<String, Object> tags) {
        this.tags = tags;
        return this;
    }

    public Event addTag(String name, Object value) {
        this.tags.put(name, value);
        return this;
    }

    public String getSpanId() {
        return spanId;
    }

    public Event setSpanId(String spanId) {
        this.spanId = spanId;
        return this;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public Event setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public Event setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }
}
