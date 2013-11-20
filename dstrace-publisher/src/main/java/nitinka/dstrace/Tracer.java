package nitinka.dstrace;

import nitinka.dstrace.conf.TracerConfiguration;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.domain.Span;
import nitinka.dstrace.factory.TracerFactory;
import nitinka.dstrace.publish.EventPublisherThread;
import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * User: NitinK.Agarwal@yahoo.com
 * Single Class to publish various tracing events from within the application
 */
public class Tracer {
    private static Map<Thread, String> threadTraceIds;
    private static Map<Thread, Boolean> threadSampleTrace;
    private static Map<Thread, Stack<Span>> threadSpanStacks;
    private static LinkedBlockingDeque<Event> eventsQueue;
    private static EventPublisherThread eventPublisherThread;
    private static Random rand;
    private static TracerConfiguration conf;

    private static boolean initialized;
    private static boolean enabled;

    private static Logger logger = LoggerFactory.getLogger(Tracer.class);
    private static ObjectMapper objectMapper = ObjectMapperUtil.instance();

    static {
        threadSpanStacks = new HashMap<Thread, Stack<Span>>();
        threadTraceIds = new HashMap<Thread, String>();
        threadSampleTrace = new HashMap<Thread, Boolean>();
        rand = new Random();
    }

    public static void initialize(TracerConfiguration tracerConfiguration)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if(initialized)
            return;

        conf = tracerConfiguration;
        enabled = tracerConfiguration.isEnabled();
        eventsQueue = new LinkedBlockingDeque<Event>(conf.getEventQueueSize());

        // Few Configurations can be over ridden using Environment Variables
        String enabledEnvParam = System.getenv("ENABLE_TRACER");
        if(enabledEnvParam != null) {
            enabled = enabledEnvParam.equalsIgnoreCase("TRUE")
                    || enabledEnvParam.equalsIgnoreCase("YES")
                    || enabledEnvParam.equalsIgnoreCase("ON");
        }
        initialized = true;

        eventPublisherThread = EventPublisherThread.initialize(tracerConfiguration.getPublishConfiguration());
    }

    public static void sample(Object enable_tracer_header_value) {
      if(enable_tracer_header_value != null){
          threadSampleTrace.put(Thread.currentThread(), Boolean.parseBoolean(enable_tracer_header_value.toString()));
      }
       else {
          threadSampleTrace.put(Thread.currentThread(), rand.nextInt(100) < conf.getSamplePercentage());
      }
    }

    /**
     * Start a new span and publishes a start event
     * @param spanName
     * @param tags
     * @param parentSpanId
     */
    public static void startSpan(String spanName, Map<String, Object> tags, String parentSpanId) {
        if(isEnabled()) {
            try {
                Span span = TracerFactory.buildSpan(spanName, parentSpanId, tags);
                Event event = TracerFactory.buildEvent(span, "Start", System.currentTimeMillis());
                enqueueEvent(event);
                pushSpan(span);
            } catch (Exception e) {
                logger.error("Error in publishing Start event", e);
            }
        }
    }

    public static void publishEvent(String eventName) {
        publishEvent(eventName, null);
    }

    /**
     * Publish event for current span
     * It can be used to mark any point in current span
     * @param eventName
     * @param tags
     */
    public static void publishEvent(String eventName, Map<String, Object> tags) {
        if(isEnabled()) {
            try {
                Span currentSpan = getCurrentSpan();
                if(currentSpan != null) {
                    currentSpan.addTags(tags);
                    Event event = TracerFactory.buildEvent(currentSpan, eventName, System.currentTimeMillis());
                    enqueueEvent(event);
                }
                else {
                    logger.error("No span to add current event. Ignoring the Event '"+eventName+"'");
                }
            }
            catch (Exception e) {
                logger.error("Error in publishing evebt '"+eventName+"'", e);
            }
        }
    }

    public static void endSpan() {
        endSpan(null);
    }

    /**
     * Send an event ,arking end of span.
     * This also removes the current span from current threads span stack
     * @param tags
     */
    public static void endSpan(Map<String, Object> tags) {
        if(isEnabled()) {
            try {
                Span currentSpan = popCurrentSpan();
                if(currentSpan != null) {
                    currentSpan.addTags(tags);
                    Event event = TracerFactory.buildEvent(currentSpan, "End", System.currentTimeMillis());
                    enqueueEvent(event);
                }
                else {
                    logger.error("No span to add End event. Ignoring the End Event");
                }
            }
            catch (Exception e) {
                logger.error("Error in publishing End event", e);
            }
        }
    }

    /**
     * Add tags to current span in the stag.
     * These tags would be published with every subsequent event in this span
     * @param tags
     */
    public static void addTags(Map<String, Object> tags) {
        if(isEnabled()) {
            try {
                Span currentSpan = popCurrentSpan();
                if(currentSpan != null) {
                    currentSpan.addTags(tags);
                }
                else {
                    logger.error("No span to add Tags. Ignoring tags");
                }
            }
            catch (Exception e) {
                logger.error("Error in publishing End event", e);
            }
        }
    }

    public static void addTag(String tagName, Object tagValue) {
        if(isEnabled()) {
            try {
                Span currentSpan = popCurrentSpan();
                if(currentSpan != null) {
                    currentSpan.getTags().put(tagName, tagValue);
                }
                else {
                    logger.error("No span to add Tag. Ignoring tag");
                }
            }
            catch (Exception e) {
                logger.error("Error in Add tag to current Span", e);
            }
        }
    }

    /**
     * Clear span stack for current Thread.
     */
    public static void reset() {
        if(isEnabled()) {
            try {
                Stack<Span> currentThreadSpanStack = threadSpanStacks.get(Thread.currentThread());
                if(currentThreadSpanStack != null)
                    currentThreadSpanStack.clear();

                threadSpanStacks.remove(Thread.currentThread());
                threadSampleTrace.remove(Thread.currentThread());
            }
            catch (Exception e) {
                logger.error("Error in Reseting Current Thread Stack", e);
            }
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static boolean shouldSample() {
        return threadSampleTrace.get(Thread.currentThread());
    }

    public static Span getCurrentSpan() {
        Span currentSpan = null;
        Stack<Span> currentThreadSpanStack = threadSpanStacks.get(Thread.currentThread());
        if(currentThreadSpanStack != null) {
            if(!currentThreadSpanStack.empty()) {
                currentSpan = threadSpanStacks.get(Thread.currentThread()).peek();
            }
        }
        return currentSpan;
    }

    private static void pushSpan(Span span) {
        Stack<Span> currentThreadSpanStack = threadSpanStacks.get(Thread.currentThread());
        if(currentThreadSpanStack == null) {
            currentThreadSpanStack = new Stack<Span>();
            threadSpanStacks.put(Thread.currentThread(), currentThreadSpanStack);
        }
        currentThreadSpanStack.push(span);
    }

    private static Span popCurrentSpan() {
        Span currentSpan = null;
        Stack<Span> currentThreadSpanStack = threadSpanStacks.get(Thread.currentThread());
        if(currentThreadSpanStack != null) {
            if(!currentThreadSpanStack.empty()) {
                currentSpan = threadSpanStacks.get(Thread.currentThread()).pop();
            }
        }
        return currentSpan;
    }

    public static TracerConfiguration getTracerConfiguration() {
        return conf;
    }

    public static void setCurrentTraceId(String traceId) {
        threadTraceIds.put(Thread.currentThread(), traceId);
    }

    public static String getCurrentTraceId() {
        return threadTraceIds.get(Thread.currentThread());
    }

    synchronized public static List<Event> dequeueEvents(int howMany) {
        List<Event> events = new ArrayList<Event>();

        // since size computation expensive, Not using size to terminate the loop
        for( ; howMany>0 ; howMany--){
            if(eventsQueue.peek() == null)
                break;
            events.add(eventsQueue.poll());
        }
        logger.info("After dequeueing events yet to be published : "+eventsQueue.size());
        return events;
    }

    synchronized private static void enqueueEvent(Event event) {
        if(!eventsQueue.offer(event)) {
            try {
                logger.warn("Event Queue full. Dropping event : "+ objectMapper.writeValueAsString(event));
            } catch (JsonMappingException e) {
                logger.error("Error in Dropping Event", e);
            } catch (JsonGenerationException e) {
                logger.error("Error in Dropping Event", e);
            } catch (IOException e) {
                logger.error("Error in Dropping Event", e);
            }
        }
        logger.info("After enqueueing events yet to be published : "+eventsQueue.size());
    }

    public static void enqueueEvents(List<Event> eventsToPublish) {
        for(Event e : eventsToPublish)
            enqueueEvent(e);
    }

    public static void enable() {
        enabled = true;
        eventPublisherThread.enable();

    }

    public static void disable() {
        enabled = false;
        eventPublisherThread.disable();
    }

    /**
     * Stops All Threads related to Tracer. You will have to restart application to enable it again
     */
    private static void stop() {
        disable();
        eventPublisherThread.stopIt();
        logger.info("Tracer stopped. You will have to restart application to start tracer again");
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, InterruptedException {
        TracerConfiguration configuration = ObjectMapperUtil.instance().readValue(new FileInputStream("./dstrace-publisher/config/tracer-config.json"), TracerConfiguration.class);
        Tracer.initialize(configuration);
        Tracer.setCurrentTraceId(UUID.randomUUID().toString());
        Tracer.startSpan("span1",null, null);
        Tracer.startSpan("span1.2",null, null);
        Tracer.endSpan();
        Tracer.endSpan();
        Thread.sleep(5000);
        Tracer.stop();
    }
}
