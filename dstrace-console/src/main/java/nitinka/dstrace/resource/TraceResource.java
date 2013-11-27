package nitinka.dstrace.resource;

import com.yammer.metrics.annotation.Timed;
import nitinka.dstrace.domain.Trace;
import nitinka.dstrace.domain.TraceRoute;
import nitinka.dstrace.fetch.AbstractFetchClient;
import nitinka.dstrace.util.HttpServletRequestHelper;
import nitinka.dstrace.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{traceId}/route")
    @Timed
    public TraceRoute getTraceTemplate(@PathParam("traceId") String traceId) throws InterruptedException, ExecutionException, IOException {
        Trace trace = fetchClient.getTrace(traceId);
        if(trace == null)
            throw new WebApplicationException(ResponseBuilder.resourceNotFound("Trace", traceId));
        return trace.template();
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public List<Trace> searchTraces(@Context HttpServletRequest request) throws IOException {
        Map<String, String> queryParameters = HttpServletRequestHelper.parseQueryParameters(request);
        int pageNo = 0;
        if(queryParameters.containsKey("pageNo")) {
            pageNo = Integer.parseInt(queryParameters.remove("pageNo"));
        }

        int pageSize = 5;
        if(queryParameters.containsKey("pageSize")) {
            pageSize = Integer.parseInt(queryParameters.remove("pageSize"));
            if(pageSize > 10)
                pageSize = 10;
        }

        if(queryParameters.size() > 0) {
            return fetchClient.searchTraces(pageNo, pageSize, queryParameters);
        }

        throw new WebApplicationException(ResponseBuilder.badRequest("No Search Query Parameters"));
    }

}