package nitinka.dstrace.conf;

/**
 * Created with IntelliJ IDEA.
 * User: NitinK.Agarwal@yahoo.com
 * Date: 6/12/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TracerFilterConfig {
    private String httpHeaderKeyForTrace;
    private String httpHeaderKeyForParentSpanId;
    private String httpHeaderKeyForTracerSampleRequest;

    public String getHttpHeaderKeyForTrace() {
        return httpHeaderKeyForTrace;
    }

    public TracerFilterConfig setHttpHeaderKeyForTrace(String httpHeaderKeyForTrace) {
        this.httpHeaderKeyForTrace = httpHeaderKeyForTrace;
        return this;
    }

    public String getHttpHeaderKeyForParentSpanId() {
        return httpHeaderKeyForParentSpanId;
    }

    public TracerFilterConfig setHttpHeaderKeyForParentSpanId(String httpHeaderKeyForParentSpanId) {
        this.httpHeaderKeyForParentSpanId = httpHeaderKeyForParentSpanId;
        return this;
    }

    public String getHttpHeaderKeyForTracerSampleRequest() {
        return httpHeaderKeyForTracerSampleRequest;
    }

    public TracerFilterConfig setHttpHeaderKeyForTracerSampleRequest(String httpHeaderKeyForTracerSampleRequest) {
        this.httpHeaderKeyForTracerSampleRequest = httpHeaderKeyForTracerSampleRequest;
        return this;
    }
}
