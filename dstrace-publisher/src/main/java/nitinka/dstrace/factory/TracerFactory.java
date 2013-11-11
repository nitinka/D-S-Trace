package nitinka.dstrace.factory;

import nitinka.dstrace.Tracer;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.domain.Header;
import nitinka.dstrace.domain.Span;

import java.util.Map;
import java.util.UUID;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class TracerFactory {

    public static final String DATA_KEY_TRACE_ID = "traceId";
    public static final String DATA_KEY_SPAN_ID = "spanId";
    public static final String DATA_KEY_PARENT_SPAN_ID = "parentSpanId";
    public static final String DATA_KEY_EVENT_NAME = "eventName";
    public static final String DATA_KEY_TAGS = "tags";

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
                setHeader(new Header().
                        setAppName(Tracer.getTracerConfiguration().getBusinessUnit()).
                        setConfigName(span.getName()).
                        setEventId(UUID.randomUUID().toString()).
                        setInstanceId(Tracer.getTracerConfiguration().getInstanceId()).
                        setProfile(Tracer.getTracerConfiguration().getAppName()).
                        setTimestamp(eventTime)).
                addData(DATA_KEY_TRACE_ID, Tracer.getCurrentTraceId()).
                addData(DATA_KEY_SPAN_ID, span.getSpanId()).
                addData(DATA_KEY_PARENT_SPAN_ID, span.getParentSpanId()).
                addData(DATA_KEY_EVENT_NAME, eventName).
                addData(DATA_KEY_TAGS, span.getTags());
    }
}
