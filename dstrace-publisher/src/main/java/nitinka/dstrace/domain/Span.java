package nitinka.dstrace.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class Span {
    private String spanId;
    private String name;
    private String parentSpanId;
    private Map<String, Object> tags;

    public Span() {
        this.tags = new HashMap<String, Object>();
    }

    public Span addTags(Map<String, Object> tags) {
        if(tags != null)
            this.tags.putAll(tags);
        return this;
    }

    public String getSpanId() {
        return spanId;
    }

    public Span setSpanId(String spanId) {
        this.spanId = spanId;
        return this;
    }

    public String getName() {
        return name;
    }

    public Span setName(String name) {
        this.name = name;
        return this;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public Span setParentSpanId(String parentSpanId) {
        this.parentSpanId = parentSpanId;
        return this;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public Span setTags(Map<String, Object> tags) {
        this.tags = tags;
        return this;
    }
}
