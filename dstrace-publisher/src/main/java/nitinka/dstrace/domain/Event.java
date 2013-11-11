package nitinka.dstrace.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * User: NitinK.Agarwal@yahoo.com
 {
 "header": {
 "appName": "scp",
 "configName": "addPayment",
 profile": "oms-b2c",
 instanceId": "127.0.0.1",
 "eventId": "4",
 "timestamp": 1377842142000
 },
 "data": {
 "operation": "started",
 "traceId": "2",
 "parentEvenId": null
 }
 }
 */

public class Event {
    private Header header;
    private Map<String, Object> data;

    public Event() {
        this.data = new HashMap<String, Object>();
        this.header = new Header();
    }

    public Header getHeader() {
        return header;
    }

    public Event setHeader(Header header) {
        this.header = header;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public Event addData(String name, Object value) {
        this.data.put(name, value);
        return this;
    }
}
