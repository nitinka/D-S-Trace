package nitinka.dstrace.publish;

import nitinka.dstrace.conf.EventPublishConfiguration;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.util.ClassHelper;
import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
abstract public class AbstractEventPublisher {
    private Map<String, Object> config;
    protected ObjectMapper objectMapper = ObjectMapperUtil.instance();
    protected Logger logger = LoggerFactory.getLogger(AbstractEventPublisher.class);

    protected AbstractEventPublisher(Map<String, Object> config) {
        this.config = config;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void publish(Event event) throws Exception {
        publish(Arrays.asList(new Event[]{event}));
    }

    abstract public void publish(List<Event> events) throws Exception;
    abstract public void close() throws Exception;

    public static AbstractEventPublisher build(EventPublishConfiguration conf) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return ClassHelper.getClassInstance(conf.getPublisherClass(), new Class[]{Map.class}, new Object[]{conf.getPublisherConfig()}, AbstractEventPublisher.class);
    }
}
