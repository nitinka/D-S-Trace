package nitinka.dstrace.config;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventFetchConfig {
    private String fetchClientClass;
    private Map<String, Object> fetchConfig;

    public String getFetchClientClass() {
        return fetchClientClass;
    }

    public void setFetchClientClass(String fetchClientClass) {
        this.fetchClientClass = fetchClientClass;
    }

    public Map<String, Object> getFetchConfig() {
        return fetchConfig;
    }

    public void setFetchConfig(Map<String, Object> fetchConfig) {
        this.fetchConfig = fetchConfig;
    }
}
