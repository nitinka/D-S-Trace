package nitinka.dstrace.factory;

import nitinka.dstrace.Tracer;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.domain.Span;

import java.util.Map;
import java.util.UUID;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class TracerFactory {
    public static Span buildSpan(String spanName, String parentSpanId, Map<String, Object> tags) {
        if(parentSpanId == null) {
            Span currentSpan = Tracer.getCurrentSpan();
            if(currentSpan != null) {
                parentSpanId = currentSpan.getSpanId();
            }
        }

        return new Span().
                setSpanId(UUID.randomUUID().toString()).
                setParentSpanId(parentSpanId).
                setName(spanName).
                addTags(tags);
    }

    public static Event buildEvent(Span span, String eventName, long eventTime) {
        return new Event().
                setBusinessUnit(Tracer.getTracerConfiguration().getBusinessUnit()).
                setOperation(span.getName()).
                setEventId(UUID.randomUUID().toString()).
                setTraceId(Tracer.getCurrentTraceId()).
                setHost(Tracer.getTracerConfiguration().getHost()).
                setApplication(Tracer.getTracerConfiguration().getApplication()).
                setTimestamp(eventTime).
                setSpanId(span.getSpanId()).
                setParentSpanId(span.getParentSpanId()).
                setEventName(eventName).
                setTags(span.getTags());
    }
}
