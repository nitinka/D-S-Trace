package nitinka.dstrace.domain;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Span {
    private String spanId;
    private String spanName;
    private List<Event> events;
    private LinkedHashMap<String,Span> childSpans;

    public Span() {
        this.events = new LinkedList<Event>();
        this.childSpans = new LinkedHashMap<String, Span>();
    }

    public List<Event> getEvents() {
        return events;
    }

    public Span setEvents(List<Event> events) {
        this.events = events;
        if(events != null && events.size() > 0)
            this.spanName = events.get(0).getSpanName();
        return this;
    }

    public Span addEvent(Event event) {
        this.events.add(event);
        this.spanName = event.getSpanName();
        return this;
    }

    public LinkedHashMap<String, Span> getChildSpans() {
        return childSpans;
    }

    public Span setChildSpans(LinkedHashMap<String, Span> childSpans) {
        this.childSpans = childSpans;
        return this;
    }

    public Span addChildSpan(Span span) {
        this.childSpans.put(span.getSpanId(), span);
        return this;
    }

    public Span childSpan(String childSpanId) {
        Span childSpan = this.childSpans.get(childSpanId);
        if (childSpan == null) {
            childSpan = new Span().setSpanId(childSpanId);
            this.addChildSpan(childSpan);
        }
        return childSpan;
    }

    public Span setSpanId(String spanId) {
        this.spanId = spanId;
        return this;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getSpanName() {
        return spanName;
    }

    public Span setSpanName(String spanName) {
        this.spanName = spanName;
        return this;
    }

    public long getDuration() {
        long duration = -1;
        if(this.events.size() > 1) {
            duration = this.events.get(1).getTimestamp() - this.events.get(0).getTimestamp();
        }
        return duration;
    }

    public static Span build(List<Event> spanEvents) {
        Map<String, Span> tmpSpanMap = new LinkedHashMap<String, Span>();
        Span topSpan = null;

        for(int i=0; i<spanEvents.size();i++) {
            Event event = spanEvents.get(i);

            Span currentSpan = null;
            String parentSpanId = event.getParentSpanId();
            if(parentSpanId != null) {
                // Find the parent Span
                Span parentSpan = tmpSpanMap.get(parentSpanId);

                // parentSpan can be null if user has asked about child span directly
                if(parentSpan != null) {
                    // Get Span for Current Event. Would create new childOperation if doesn't exist
                    currentSpan = parentSpan.childSpan(event.getSpanId());
                }
            }
            if(currentSpan == null) {
                currentSpan = tmpSpanMap.get(event.getSpanId());
                if(currentSpan == null) {
                    currentSpan = new Span().
                            setSpanId(event.getSpanId()).
                            setSpanName(event.getSpanName());
                }
            }

            currentSpan.addEvent(event);
            tmpSpanMap.put(currentSpan.getSpanId(), currentSpan);
            if(topSpan == null)
                topSpan = currentSpan;
        }
        tmpSpanMap.clear();
        return topSpan;
    }
}

