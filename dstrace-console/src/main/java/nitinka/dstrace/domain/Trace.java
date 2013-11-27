package nitinka.dstrace.domain;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.*;

/**
 * User: nitinka
 * Date: 2/9/13
 * Time: 12:58 PM
 *
 * A trace object represents the sequence of spans performed in a distributed environment.
 */
public class Trace {
    private String traceId;
    private long startTime;
    private long endTime;
    private long durationMS;
    private String firstSpan;
    private String lastSpan;
    private LinkedHashMap<String, Span> spans;

    public Trace() {
        this.spans = new LinkedHashMap<String, Span>();
    }

    public String getTraceId() {
        return traceId;
    }

    public Trace setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public long getStartTime() {
        return startTime;
    }

    public Trace setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    public long getEndTime() {
        return endTime;
    }

    public Trace setEndTime(long endTime) {
        this.endTime = endTime;
        this.durationMS = this.endTime - this.startTime;
        return this;
    }

    public long getDurationMS() {
        return durationMS;
    }

    public void setDurationMS(long durationMS) {
        this.durationMS = durationMS;
    }

    public String getFirstSpan() {
        return firstSpan;
    }

    public void setFirstSpan(String firstSpan) {
        this.firstSpan = firstSpan;
    }

    public String getLastSpan() {
        return lastSpan;
    }

    public void setLastSpan(String lastSpan) {
        this.lastSpan = lastSpan;
    }

    public Map<String, Span> getSpans() {
        return spans;
    }

    public Trace setSpans(LinkedHashMap<String, Span> spans) {
        this.spans = spans;
        return this;
    }

    public Span span(String operationId) {
        Span span = this.spans.get(operationId);
        if (span == null) {
            span = new Span().setSpanId(operationId);
            this.addOperation(span);
        }
        return span;
    }

    private void addOperation(Span span) {
        this.spans.put(span.getSpanId(), span);
    }

    public static Trace build(List<Event> traceEvents, String traceId) {
        Trace trace = new Trace().
                setTraceId(traceId);

        // Tmp OperationId -> OperationMap
        Map<String, Span> tmpSpanMap = new HashMap<String, Span>();
        long oldestTimestampInTrace = -1;

        for(int i=0; i<traceEvents.size(); i++) {
            Event event = traceEvents.get(i);

            if(i == 0)
                trace.startTime = event.getTimestamp();

            Span currentSpan = null;
            String parentSpanId = event.getParentSpanId();
            if(parentSpanId != null) {
                // Find the parent Span
                Span parentSpan = tmpSpanMap.get(parentSpanId);

                // Get Span for Current Event. Would create new childOperation if doesn't exist
                currentSpan = parentSpan.childSpan(event.getSpanId());
            }
            else {
                currentSpan = trace.span(event.getSpanId());
            }

            currentSpan.addEvent(event);
            if(trace.firstSpan == null)
                trace.firstSpan = currentSpan.getSpanName();

            tmpSpanMap.put(currentSpan.getSpanId(), currentSpan);
            oldestTimestampInTrace = event.getTimestamp();
            trace.lastSpan = currentSpan.getSpanName();
        }

        trace.setEndTime(oldestTimestampInTrace);
        tmpSpanMap.clear();

        return trace;
    }

    public static void main(String[] args) throws IOException {
        String eventsStr1 = "[{\"header\":{\"appName\":\"scp\",\"configName\":\"GET_/mappings\",\"profile\":\"fulfillment-b2c\",\"instanceId\":\"flo-ci-warehouse-1.nm.flipkart.com:35401\",\"eventId\":\"edcd2cfa-67b2-4088-8368-fdf27299c953\",\"timestamp\":1379591862416},\"data\":{\"tags\":{\"DEPLOY_ENV\":\"smoke-warehouse-1\",\"PADRINO_ENV\":\"production\"},\"traceId\":\"TXN-83f170d4-50bc-4d2f-82f9-89347a04fdac\",\"spanId\":\"1091c10e-6b7a-4fa5-9aa1-6c73d2408998\",\"parentSpanId\":null,\"eventName\":\"Start\"}},{\"header\":{\"appName\":\"scp\",\"configName\":\"ERROR_(?-mix:)\",\"profile\":\"fulfillment-b2c\",\"instanceId\":\"flo-ci-warehouse-1.nm.flipkart.com:35401\",\"eventId\":\"4b68b666-0e7c-4342-a44c-d37d02ed2ca0\",\"timestamp\":1379591862416},\"data\":{\"tags\":{\"DEPLOY_ENV\":\"smoke-warehouse-1\",\"PADRINO_ENV\":\"production\",\"status\":\"success\"},\"traceId\":\"TXN-83f170d4-50bc-4d2f-82f9-89347a04fdac\",\"spanId\":\"7cfcfda4-3e19-4a66-b39f-358011a7c482\",\"parentSpanId\":\"1091c10e-6b7a-4fa5-9aa1-6c73d2408998\",\"eventName\":\"Start\"}},{\"header\":{\"appName\":\"scp\",\"configName\":\"ERROR_(?-mix:)\",\"profile\":\"fulfillment-b2c\",\"instanceId\":\"flo-ci-warehouse-1.nm.flipkart.com:35401\",\"eventId\":\"c91a5970-95ee-4f5f-aedf-2bd61fb691dd\",\"timestamp\":1379591863181},\"data\":{\"tags\":{\"DEPLOY_ENV\":\"smoke-warehouse-1\",\"PADRINO_ENV\":\"production\",\"status\":\"success\"},\"traceId\":\"TXN-83f170d4-50bc-4d2f-82f9-89347a04fdac\",\"spanId\":\"7cfcfda4-3e19-4a66-b39f-358011a7c482\",\"parentSpanId\":\"1091c10e-6b7a-4fa5-9aa1-6c73d2408998\",\"eventName\":\"End\"}}]";
  //      String eventsStr1 = "[{\"header\":{\"appName\":\"scp\",\"configName\":\"ERROR_(?-mix:)\",\"profile\":\"fulfillment-b2c\",\"instanceId\":\"flo-ci-warehouse-1.nm.flipkart.com:35401\",\"eventId\":\"4b68b666-0e7c-4342-a44c-d37d02ed2ca0\",\"timestamp\":1379591862416},\"data\":{\"tags\":{\"DEPLOY_ENV\":\"smoke-warehouse-1\",\"PADRINO_ENV\":\"production\",\"status\":\"success\"},\"traceId\":\"TXN-83f170d4-50bc-4d2f-82f9-89347a04fdac\",\"spanId\":\"7cfcfda4-3e19-4a66-b39f-358011a7c482\",\"parentSpanId\":\"1091c10e-6b7a-4fa5-9aa1-6c73d2408998\",\"eventName\":\"Start\"}},{\"header\":{\"appName\":\"scp\",\"configName\":\"ERROR_(?-mix:)\",\"profile\":\"fulfillment-b2c\",\"instanceId\":\"flo-ci-warehouse-1.nm.flipkart.com:35401\",\"eventId\":\"c91a5970-95ee-4f5f-aedf-2bd61fb691dd\",\"timestamp\":1379591863181},\"data\":{\"tags\":{\"DEPLOY_ENV\":\"smoke-warehouse-1\",\"PADRINO_ENV\":\"production\",\"status\":\"success\"},\"traceId\":\"TXN-83f170d4-50bc-4d2f-82f9-89347a04fdac\",\"spanId\":\"7cfcfda4-3e19-4a66-b39f-358011a7c482\",\"parentSpanId\":\"1091c10e-6b7a-4fa5-9aa1-6c73d2408998\",\"eventName\":\"End\"}},{\"header\":{\"appName\":\"scp\",\"configName\":\"GET_/mappings\",\"profile\":\"fulfillment-b2c\",\"instanceId\":\"flo-ci-warehouse-1.nm.flipkart.com:35401\",\"eventId\":\"edcd2cfa-67b2-4088-8368-fdf27299c953\",\"timestamp\":1379591862416},\"data\":{\"tags\":{\"DEPLOY_ENV\":\"smoke-warehouse-1\",\"PADRINO_ENV\":\"production\"},\"traceId\":\"TXN-83f170d4-50bc-4d2f-82f9-89347a04fdac\",\"spanId\":\"1091c10e-6b7a-4fa5-9aa1-6c73d2408998\",\"parentSpanId\":null,\"eventName\":\"Start\"}}]";
        ObjectMapper mapper = new ObjectMapper();
        List rawEvents = mapper.readValue(eventsStr1,List.class);
        List<Event> events = new ArrayList<Event>();
        for(Object rawEvent : rawEvents) {
            events.add(mapper.readValue(mapper.writeValueAsString(rawEvent), Event.class));
        }

        //System.out.println(mapper.defaultPrettyPrintingWriter().writeValueAsString(rawEvents));
        Trace trace = Trace.build(events, "1");
        System.out.println(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(trace));
/*
        List<Event> events = new ArrayList<Event>();
        Event event = new Event();
        event.getHeader().setEventId("1").setAppName("scp").setConfigName("ApproveOrder").setProfile("oms").setInstanceId("localhost").setTimestamp(System.currentTimeMillis());
        event.addData("operation","started").addData("spanId", "1").addData("parentSpanId", null).addData("traceId", "1");

        Event event2 = new Event();
        event2.getHeader().setEventId("2").setAppName("scp").setConfigName("ApproveOrder").setProfile("payment").setInstanceId("localhost").setTimestamp(System.currentTimeMillis() + 200);
        event2.addData("operation","started").addData("spanId", "2").addData("parentSpanId", "1").addData("traceId", "1");

        Event event21 = new Event();
        event21.getHeader().setEventId("3").setAppName("scp").setConfigName("ABC").setProfile("XYZ").setInstanceId("localhost").setTimestamp(System.currentTimeMillis() + 200);
        event21.addData("operation","started").addData("spanId", "3").addData("parentSpanId", "2").addData("traceId", "1");

        Event event22 = new Event();
        event22.getHeader().setEventId("4").setAppName("scp").setConfigName("ABC").setProfile("XYZ").setInstanceId("localhost").setTimestamp(System.currentTimeMillis() + 400);
        event22.addData("operation","ended").addData("spanId", "3").addData("parentSpanId", "2").addData("traceId", "1");


        Event event3 = new Event();
        event3.getHeader().setEventId("5").setAppName("scp").setConfigName("ApproveOrder").setProfile("payment").setInstanceId("localhost").setTimestamp(System.currentTimeMillis() + 400);
        event3.addData("operation","ended").addData("spanId", "2").addData("parentSpanId", "1").addData("traceId", "1");

        Event event4 = new Event();
        event4.getHeader().setEventId("6").setAppName("scp").setConfigName("ApproveOrder").setProfile("oms").setInstanceId("localhost").setTimestamp(System.currentTimeMillis() + 600);
        event4.addData("operation","ended").addData("spanId", "1").addData("parentSpanId", null).addData("traceId", "1");

        events.add(event);
        events.add(event2);
        events.add(event21);
        events.add(event22);
        events.add(event3);
        events.add(event4);
        Trace trace = Trace.buildTrace(events, "1");
        System.out.println(new ObjectMapper().defaultPrettyPrintingWriter().writeValueAsString(trace));
*/
    }

}
