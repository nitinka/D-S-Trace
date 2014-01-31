package nitinka.dstrace.publish;

import nitinka.dstrace.domain.Event;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class RedisPublisherImpl extends AbstractEventPublisher {

    private Jedis jedis;
    private String list;

    public static final String REDIS_HOST = "redisHost";
    public static final String REDIS_PORT = "redisPort";
    public static final String REDIS_LIST = "redisList";

    public RedisPublisherImpl(Map<String, Object> config) {
        super(config);
        jedis = new Jedis(config.get(REDIS_HOST).toString(),
                Integer.parseInt(config.get(REDIS_PORT).toString()));
        list = config.get(REDIS_LIST).toString();
    }

    @Override
    public void publish(List<Event> events) throws IOException {
        logger.info("Publishing Events :"+objectMapper.writeValueAsString(events));
        jedis.rpush(this.list, objectMapper.writeValueAsString(events));
//        jedis.publish(this.list, objectMapper.writeValueAsString(events));
    }

    @Override
    public void close() {
        jedis.quit();
    }
}
