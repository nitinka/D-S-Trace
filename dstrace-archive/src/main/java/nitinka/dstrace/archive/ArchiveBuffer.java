package nitinka.dstrace.archive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 9:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArchiveBuffer {
    private static LinkedBlockingQueue<String> bufferQueue = new LinkedBlockingQueue<String>(100000);
    private static Logger logger = LoggerFactory.getLogger(ArchiveBuffer.class);
    public static void put(String content) throws InterruptedException {
        bufferQueue.put(content);
        logger.info("Buffer Size :"+bufferQueue.size());
    }

    public static String take() throws InterruptedException {
        String content = bufferQueue.take();
        logger.info("Buffer Size :"+bufferQueue.size());
        return content;
    }
}
