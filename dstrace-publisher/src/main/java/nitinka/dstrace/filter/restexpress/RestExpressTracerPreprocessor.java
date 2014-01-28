package nitinka.dstrace.filter.restexpress;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.pipeline.Preprocessor;
import nitinka.dstrace.Tracer;
import nitinka.dstrace.conf.TracerFilterConfig;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 24/9/13
 * Time: 4:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class RestExpressTracerPreprocessor implements Preprocessor{
    private TracerFilterConfig tracerFilterConfig;
    public RestExpressTracerPreprocessor(TracerFilterConfig tracerFilterConfig) {
        this.tracerFilterConfig = tracerFilterConfig;
    }

    @Override
    public void process(Request request) {
        Tracer.sample(request.getRawHeader(tracerFilterConfig.getHttpHeaderKeyForTracerSampleRequest()));
        Tracer.setCurrentTraceId(request.getRawHeader(tracerFilterConfig.getHttpHeaderKeyForTrace()));
        Tracer.startSpan(request.getResolvedRoute().getFullPattern(),
                getTags(request),
                request.getRawHeader(tracerFilterConfig.getHttpHeaderKeyForParentSpanId()));

    }

    private Map<String, Object> getTags(Request request) {
        Map<String, Object> tags = new LinkedHashMap<String, Object>();
        String route = request.getResolvedRoute().getFullPattern();
        for(String header : request.getHeaderNames()) {
            if(route.contains(header)) {
                tags.put(header, request.getHeader(header));
            }
        }
        return tags;
    }
}
