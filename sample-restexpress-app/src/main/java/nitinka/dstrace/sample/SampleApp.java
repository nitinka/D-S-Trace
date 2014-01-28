package nitinka.dstrace.sample;

import com.strategicgains.restexpress.Format;
import com.strategicgains.restexpress.RestExpress;
import com.strategicgains.restexpress.response.ResponseProcessor;
import nitinka.dstrace.Tracer;
import nitinka.dstrace.conf.TracerConfiguration;
import nitinka.dstrace.conf.TracerFilterConfig;
import nitinka.dstrace.filter.restexpress.RestExpressTracerMessageObserver;
import nitinka.dstrace.filter.restexpress.RestExpressTracerPreprocessor;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.netty.handler.codec.http.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SampleApp {
    public static void main(String[] args)
            throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        RestExpress server = new RestExpress();
        server.putResponseProcessor(Format.JSON, ResponseProcessor.defaultJsonProcessor());
        server.uri("/hello", new nitinka.dstrace.sample.controller.SampleController()).action("sayHello", HttpMethod.GET);

        //===============================================================================
        //Code to instrument resources and initialize Tracer
        //Use this code once you have added all your resources

        server.addMessageObserver(new RestExpressTracerMessageObserver());

        server.addPreprocessor(new RestExpressTracerPreprocessor(new TracerFilterConfig().
                setHttpHeaderKeyForTrace("TRACER_TXN_ID").
                setHttpHeaderKeyForParentSpanId("TRACER_PARENT_SPAN_ID").
                setHttpHeaderKeyForTracerSampleRequest("TRACER_SAMPLE_REQUEST")));


        TracerConfiguration tracerConfiguration = new ObjectMapper().
                readValue(new File("./sample-restexpress-app/config/tracer-config.json"), TracerConfiguration.class);

        Tracer.initialize(tracerConfiguration);
        //Tracer Code ends here
        //===============================================================================
        server.bind(8888);
    }
}
