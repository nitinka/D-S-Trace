package nitinka.dstrace.publish;

import nitinka.dstrace.Tracer;
import nitinka.dstrace.conf.EventPublishConfiguration;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class EventPublisherThread extends Thread{

    private EventPublishConfiguration conf;
    private boolean keepRunning = true;
    private boolean disabled = false;

    private AbstractEventPublisher eventPublisher;
    private Logger logger = LoggerFactory.getLogger(EventPublisherThread.class);
    private ObjectMapper objectMapper = ObjectMapperUtil.instance();
    private static EventPublisherThread instance;

    private EventPublisherThread(EventPublishConfiguration conf) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        this.conf = conf;
        this.eventPublisher = AbstractEventPublisher.build(conf);
    }

    public void run() {
        logger.info("Event Publisher thread started");
        while(keepRunning) {
            if(disabled)
                continue;

            List<Event> eventsToPublish = new ArrayList<Event>();

            try {
                eventsToPublish = Tracer.dequeueEvents(conf.getEventPublishSize());
                if(eventsToPublish.size() > 0) {
                    logger.info("Publishing Events : "+eventsToPublish.size());
                    eventPublisher.publish(eventsToPublish);
                }
            }
            catch(Exception e) {
                logger.error("Error while publishing messages", e);
                if(conf.isEnqueueFailedMessages()) {
                    logger.info("Enqueing failed events back to queue");
                    Tracer.enqueueEvents(eventsToPublish);
                }
            }
            finally {
                try {
                    Thread.sleep(conf.getEventPublishDelay());
                } catch (InterruptedException e) {
                    logger.error("Error while Interval", e);
                }
            }
        }
        try {
            eventPublisher.close();
        } catch (Exception e) {
            logger.error("Error closing event publisher", e);
        }
    }

    public void disable() {
        this.disabled = true;
    }

    public void enable() {
        this.disabled = false;
    }

    public static EventPublisherThread initialize(EventPublishConfiguration publishConfiguration) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if(instance == null) {
            instance = new EventPublisherThread(publishConfiguration);
            instance.start();
        }
        return instance;
    }

    public void stopIt() {
        this.keepRunning = false;
    }
}
