package nitinka.dstrace.fetch;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import nitinka.dstrace.domain.Event;
import nitinka.dstrace.domain.Span;
import nitinka.dstrace.util.ObjectMapperUtil;
import nitinka.dstrace.util.ResponseBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchFetchClient extends AbstractFetchClient {

    private AsyncHttpClient httpClient;
    private ObjectMapper objectMapper = ObjectMapperUtil.instance();
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchFetchClient.class);

    private final String elasticSearchUrl, index, type, documentTypeUrl;

    public ElasticSearchFetchClient(Map<String, Object> config) {
        super(config);
        this.elasticSearchUrl = config.get("elasticSearchUrl").toString();
        this.index = config.get("eventIndex").toString();
        this.type = config.get("eventType").toString();
        this.documentTypeUrl = this.elasticSearchUrl
                + "/"
                + index
                + "/"
                + type;
        httpClient = new AsyncHttpClient();
    }

    @Override
    public Event getEvent(String eventId) throws IOException, ExecutionException, InterruptedException {
        String url = this.documentTypeUrl + "/" + eventId + "/_source";
        AsyncHttpClient.BoundRequestBuilder builder = httpClient.prepareGet(url);
        Future<Response> f = builder.execute();
        Response response = f.get();

        if(response.getStatusCode() == 200) {
            return objectMapper.readValue(response.getResponseBodyAsBytes(), Event.class);
        }
        return null;
    }

    @Override
    public Span getSpanByEventId(String eventId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Span getTraceByEventID(String eventId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Span getTrace(String traceId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() {
        this.httpClient.close();
    }

    @Override
    public List<Event> searchEvents(Map<String, String> queryParameters, int pageNo, int pageSize) {
        List<Event> events = new ArrayList<Event>();
//        Elastic
        return events;
    }

    public static void main(String[] args) {
        Client client = new TransportClient()
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300))
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));



    }
}
