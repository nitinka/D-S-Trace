package nitinka.dstrace.resource;

import com.yammer.metrics.annotation.Timed;
import nitinka.dstrace.domain.Span;
import nitinka.dstrace.domain.Trace;
import nitinka.dstrace.fetch.AbstractFetchClient;
import nitinka.dstrace.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Path("/traces")
public class TraceResource {

    private final AbstractFetchClient fetchClient;
    private static Logger logger = LoggerFactory.getLogger(TraceResource.class);

    public TraceResource(AbstractFetchClient fetchClient) {
        this.fetchClient = fetchClient;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{traceId}")
    @Timed
    public Trace getTrace(@PathParam("traceId") String traceId) throws InterruptedException, ExecutionException, IOException {
        Trace trace = fetchClient.getTrace(traceId);
        if(trace == null)
            throw new WebApplicationException(ResponseBuilder.resourceNotFound("Trace", traceId));
        return trace;
    }
}