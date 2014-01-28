package nitinka.dstrace.filter.restexpress;

import com.strategicgains.restexpress.Request;
import com.strategicgains.restexpress.Response;
import com.strategicgains.restexpress.pipeline.MessageObserver;
import nitinka.dstrace.Tracer;

/**
 * Created by IntelliJ IDEA.
 * User: nitinka
 * Date: 24/9/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class RestExpressTracerMessageObserver extends MessageObserver{

    @Override
    protected void onReceived(Request request, Response response) {
        super.onReceived(request, response);
    }

    @Override
    protected void onSuccess(Request request, Response response) {
        super.onSuccess(request, response);
    }

    @Override
    protected void onComplete(Request request, Response response) {
        super.onComplete(request, response);
        Tracer.endSpan();
        Tracer.reset();
    }

    @Override
    protected void onException(Throwable e, Request request, Response response) {
        super.onException(e, request, response);
        Tracer.addTag("status","error");
    }
}
