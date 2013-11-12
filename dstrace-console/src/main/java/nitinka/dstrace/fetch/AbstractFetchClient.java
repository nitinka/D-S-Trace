package nitinka.dstrace.fetch;

import nitinka.dstrace.config.EventFetchConfig;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.domain.Span;
import nitinka.dstrace.util.ClassHelper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class AbstractFetchClient {
    private Map<String, Object> config;

    protected AbstractFetchClient(Map<String, Object> config) {
        this.config = config;
    }

    abstract public Event getEvent(String eventId) throws IOException, ExecutionException, InterruptedException;
    abstract public Span getSpanByEventId(String eventId);
    abstract public Span getTraceByEventID(String eventId);
    abstract public Span getTrace(String traceId);
    abstract public void close();

    public static AbstractFetchClient build(EventFetchConfig config) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return ClassHelper.getClassInstance(config.getFetchClientClass(), new Class[]{Map.class}, new Object[]{config.getFetchConfig()}, AbstractFetchClient.class);
    }

    public abstract List<Event> searchEvents(Map<String, String> queryParameters, int pageNo, int pageSize);
}
