package nitinka.dstrace.util;

import org.codehaus.jackson.map.ObjectMapper;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class ObjectMapperUtil {
    private static ObjectMapper instance = new ObjectMapper();
    public static ObjectMapper instance() {
        return instance;
    }
}
