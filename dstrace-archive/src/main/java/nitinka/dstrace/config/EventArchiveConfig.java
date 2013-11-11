package nitinka.dstrace.config;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventArchiveConfig {
    private String archiverClass;
    private Map<String, Object> archiverConfig;
    private int archiverThreads = 1;

    public String getArchiverClass() {
        return archiverClass;
    }

    public void setArchiverClass(String archiverClass) {
        this.archiverClass = archiverClass;
    }

    public Map<String, Object> getArchiverConfig() {
        return archiverConfig;
    }

    public void setArchiverConfig(Map<String, Object> archiverConfig) {
        this.archiverConfig = archiverConfig;
    }

    public int getArchiverThreads() {
        return archiverThreads;
    }

    public void setArchiverThreads(int archiverThreads) {
        this.archiverThreads = archiverThreads;
    }
}
