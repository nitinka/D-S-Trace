package nitinka.dstrace.sample.resource;

import com.yammer.metrics.annotation.Timed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Path("/hello")
public class HelloResource {

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Timed
    public String sayHello(@QueryParam("name") String name) throws InterruptedException, ExecutionException, IOException {
        return "Hello "+name +"!!";
    }

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Timed
    public String sayHelloAsPost(@QueryParam("name") String name) throws InterruptedException, ExecutionException, IOException {
        return "Hello "+name +"!!";
    }
}