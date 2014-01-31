package nitinka.dstrace.consume;

import nitinka.dstrace.archive.ArchiveBuffer;
import nitinka.dstrace.config.RedisConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class RedisConsumer extends Thread{
    private final Jedis jedis;
    private boolean keepRunning;
    private static Logger logger = LoggerFactory.getLogger(RedisConsumer.class);
    private final RedisConsumerConfig config;

    public RedisConsumer(RedisConsumerConfig config) {
        this.config = config;
        jedis = new Jedis(config.getRedisHost(),
                config.getRedisPort());
    }

    public void run() {
        while(keepRunning) {
            try {
                List<String> messages = jedis.blpop(config.getListReadTimeOutSecs(), config.getLists());
                while(messages != null) {
                    for(String message : messages) {
                        ArchiveBuffer.put(message);
                    }
                    messages = jedis.blpop(config.getListReadTimeOutSecs(), config.getLists());
                }
            }
            catch(Exception e) {
                logger.error("Error while blpop",e);
            }
        }
    }

    public void stopIt() {
        this.keepRunning = false;
    }
}
