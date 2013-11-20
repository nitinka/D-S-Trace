package nitinka.dstrace.resource;

import com.yammer.metrics.annotation.Timed;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.domain.Span;
import nitinka.dstrace.fetch.AbstractFetchClient;
import nitinka.dstrace.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Path("/spans")
public class SpanResource {

    private final AbstractFetchClient fetchClient;
    private static Logger logger = LoggerFactory.getLogger(SpanResource.class);

    public SpanResource(AbstractFetchClient fetchClient) {
        this.fetchClient = fetchClient;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{spanId}")
    @Timed
    public Span getSpan(@PathParam("spanId") String spanId) throws InterruptedException, ExecutionException, IOException {
        Span span = fetchClient.getSpan(spanId);
        if(span == null)
            throw new WebApplicationException(ResponseBuilder.resourceNotFound("Span", spanId));
        return span;
    }
}