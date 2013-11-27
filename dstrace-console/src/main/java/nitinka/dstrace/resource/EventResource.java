package nitinka.dstrace.resource;

import com.yammer.metrics.annotation.Timed;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.fetch.AbstractFetchClient;
import nitinka.dstrace.util.HttpServletRequestHelper;
import nitinka.dstrace.util.ResponseBuilder;
import nitinka.jmetrics.util.ObjectMapperUtil;
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

@Path("/events")
public class EventResource {

    private final AbstractFetchClient fetchClient;
    private static Logger logger = LoggerFactory.getLogger(EventResource.class);

    public EventResource(AbstractFetchClient fetchClient) {
        this.fetchClient = fetchClient;
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{eventId}")
    @Timed
    public Event getEvent(@PathParam("eventId") String eventId) throws InterruptedException, ExecutionException, IOException {
        Event event = fetchClient.getEvent(eventId);
        if(event == null)
            throw new WebApplicationException(ResponseBuilder.resourceNotFound("Event", eventId));
        return event;
    }


    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public List<Event> getEvents(@Context HttpServletRequest request) throws IOException {
        Map<String, String> queryParameters = HttpServletRequestHelper.parseQueryParameters(request);
        int pageNo = 0;
        if(queryParameters.containsKey("pageNo")) {
            pageNo = Integer.parseInt(queryParameters.remove("pageNo"));
        }

        int pageSize = 25;
        if(queryParameters.containsKey("pageSize")) {
            pageSize = Integer.parseInt(queryParameters.remove("pageSize"));
            if(pageSize > 100)
                pageSize = 100;
        }

        if(queryParameters.size() > 0) {
            return fetchClient.searchEvents(pageNo, pageSize, queryParameters);
        }

        throw new WebApplicationException(ResponseBuilder.badRequest("No Search Query Parameters"));
    }
}