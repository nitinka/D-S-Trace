package nitinka.dstrace.config;

public class RedisConsumerConfig {
    private int maxActive = 1;
    private int maxIdle = 1;
    private String redisHost;
    private int redisPort;
    private String[] lists;

    private int listReadTimeOutSecs;

    public int getConsumerPoolSize() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public String[] getLists() {
        return lists;
    }

    public void setLists(String[] lists) {
        this.lists = lists;
    }

    public int getListReadTimeOutSecs() {
        return listReadTimeOutSecs;
    }

    public void setListReadTimeOutSecs(int listReadTimeOutSecs) {
        this.listReadTimeOutSecs = listReadTimeOutSecs;
    }
}
