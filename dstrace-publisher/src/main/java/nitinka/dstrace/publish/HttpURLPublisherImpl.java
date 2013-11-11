package nitinka.dstrace.publish;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import nitinka.dstrace.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


/**
 * User: NitinK.Agarwal@yahoo.com
 */
public class HttpURLPublisherImpl extends AbstractEventPublisher{
    private AsyncHttpClient httpClient = new AsyncHttpClient();
    private String httpUrl;
    private Logger logger = LoggerFactory.getLogger(HttpURLPublisherImpl.class);
    public static final String HTTP_URL = "httpUrl";

    protected HttpURLPublisherImpl(Map<String, Object> config) {
        super(config);
        this.httpUrl = config.get(HTTP_URL).toString();
    }

    @Override
    public void publish(List<Event> events) throws Exception {
        AsyncHttpClient.BoundRequestBuilder b = httpClient.
                preparePost(httpUrl).
                setHeader("Content-Type", "application/json").
                setBody(objectMapper.writeValueAsBytes(events));

        logger.debug("Publishing Events :" + events);
        Future<Response> r = b.execute();
        if(!r.isDone())
            r.get();

        if(r.get().getStatusCode() != 200 && r.get().getStatusCode() != 201) {
            logger.error("Publish Event Failed");
            logger.error("Failure Response Code : "+r.get().getStatusCode());
            logger.error("Failure Response Bode : "+r.get().getResponseBody());
        }
    }

    public void close() {
        httpClient.close();
    }
}
