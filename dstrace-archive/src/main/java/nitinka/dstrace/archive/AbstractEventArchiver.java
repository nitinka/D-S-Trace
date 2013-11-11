package nitinka.dstrace.archive;

import nitinka.dstrace.config.EventArchiveConfig;
import nitinka.dstrace.util.ClassHelper;
import org.codehaus.jackson.JsonParseException;
import sun.applet.resources.MsgAppletViewer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
abstract public class AbstractEventArchiver {
    private Map<String, Object> config;

    protected AbstractEventArchiver(Map<String, Object> config) {
        this.config = config;
    }

    abstract public void archive(String events) throws Exception;
    abstract public void close() throws Exception;

    public static AbstractEventArchiver build(EventArchiveConfig config) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return ClassHelper.getClassInstance(config.getArchiverClass(), new Class[]{Map.class}, new Object[]{config.getArchiverConfig()}, AbstractEventArchiver.class);
    }
}
