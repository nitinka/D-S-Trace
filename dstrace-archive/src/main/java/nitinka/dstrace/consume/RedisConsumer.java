package nitinka.dstrace.consume;

import nitinka.dstrace.archive.AbstractEventArchiver;
import nitinka.dstrace.config.RedisConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class RedisConsumer{
    private final Jedis jedis;
    private final String[] channels;
    private ExecutorService executorService;
    private static Logger logger = LoggerFactory.getLogger(RedisConsumer.class);

    public RedisConsumer(RedisConsumerConfig config) {
        jedis = new Jedis(config.getRedisHost(),
                config.getRedisPort());
        this.channels = config.getChannels();
    }

    public void start() {
        executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    logger.info("Starting Redis Consumer");
                    jedis.subscribe(new RedisSubscriber(), channels);
                    logger.info("Redis Consumer Ended");
                }
            });
    }

    public void stop() {
        this.executorService.shutdown();
    }
}
