package nitinka.dstrace.sample.controller;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

public class SampleController {

    /**
     * Say Hello
     * @param request
     * @param response
     */
    public void sayHello(Request request, Response response) {
        response.setBody("Hello " + request.getHeader("name") + "!!");
        response.setResponseStatus(HttpResponseStatus.OK);
    }
}

