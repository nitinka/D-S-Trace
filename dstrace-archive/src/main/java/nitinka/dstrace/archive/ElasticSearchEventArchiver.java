package nitinka.dstrace.archive;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
public class ElasticSearchEventArchiver extends AbstractEventArchiver {

    private AsyncHttpClient httpClient;
    private String elasticSearchUrl;
    private ObjectMapper objectMapper = ObjectMapperUtil.instance();
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchEventArchiver.class);

    private String index, type;
    public ElasticSearchEventArchiver(Map<String, Object> config) {
        super(config);
        this.elasticSearchUrl = config.get("elasticSearchUrl").toString();
        this.index = config.get("eventIndex").toString();
        this.type = config.get("eventType").toString();
        httpClient = new AsyncHttpClient();
    }

    @Override
    public void archive(String events) throws IOException, ExecutionException, InterruptedException {
        List<Map> eventList = objectMapper.readValue(events, List.class);
        for(Map event : eventList) {
            String id = ((Map)event.get("header")).get("eventId").toString();
            String url = this.elasticSearchUrl
                    + "/"
                    + index
                    + "/"
                    + type
                    + "/"
                    + id;

            AsyncHttpClient.BoundRequestBuilder builder = httpClient.preparePut(url).setBody(objectMapper.writeValueAsBytes(event));
            logger.info("Pushing document to elastic search using url "+url);
            Future<Response> f = builder.execute();
            Response response = f.get();

            if(response.getStatusCode() != 201) {
                logger.error("Object Archival failed with "+response.getResponseBody());
            }
        }
    }

    @Override
    public void close() {
        this.httpClient.close();
    }
}
