package nitinka.dstrace.consume;
import nitinka.dstrace.archive.AbstractEventArchiver;
import nitinka.dstrace.archive.ArchiveBuffer;
import nitinka.dstrace.archive.ElasticSearchEventArchiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

public class RedisSubscriber extends JedisPubSub {

    private static Logger logger = LoggerFactory.getLogger(RedisSubscriber.class);

    /**
     *
     * @param channel
     * @param message list of events
     */
    @Override
    public void onMessage(String channel, String message) {
        try {
            ArchiveBuffer.put(message);
        } catch (InterruptedException e) {
            logger.error("Error in moving message in inmemory buffer", e);
        }
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {

    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {

    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {

    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {

    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {

    }
}
