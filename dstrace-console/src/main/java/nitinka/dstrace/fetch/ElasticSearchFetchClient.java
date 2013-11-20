package nitinka.dstrace.fetch;

import nitinka.dstrace.domain.Event;
import nitinka.dstrace.domain.Span;
import nitinka.dstrace.domain.Trace;
import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchFetchClient extends AbstractFetchClient {

    private Client elasticClient ;

    private ObjectMapper objectMapper = ObjectMapperUtil.instance();
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchFetchClient.class);
    private final String index, type;

    public ElasticSearchFetchClient(Map<String, Object> config) {
        super(config);
        this.elasticClient = new TransportClient();

        List<String> elasticSearchHosts = (List<String>) config.get("elasticSearchHosts");
        for(String elasticSearchHost : elasticSearchHosts) {
            ((TransportClient)this.elasticClient).
                    addTransportAddress(new InetSocketTransportAddress(elasticSearchHost.split(":")[0],
                            Integer.parseInt(elasticSearchHost.split(":")[1])));
        }

        this.index = config.get("eventIndex").toString();
        this.type = config.get("eventType").toString();
    }

    @Override
    public Event getEvent(String eventId) throws IOException, ExecutionException, InterruptedException {
        GetResponse response = elasticClient.prepareGet(index, type, eventId)
                .execute()
                .actionGet();

        if(response.getSource() != null)
            return objectMapper.readValue(response.getSourceAsBytes(), Event.class);
        return null;
    }

    @Override
    public Span getSpan(String spanId) throws IOException {
        List<Event> spanEvents = searchEvents(0, 1000, spanId, "spanId", "parentSpanId");
        if(spanEvents.size() > 0)
            return Span.build(spanEvents);
        return null;
    }

    @Override
    public Trace getTrace(String traceId) throws IOException {
        List<Event> traceEvents = searchEvents(0, 1000, traceId, "traceId");
        if(traceEvents.size() > 0)
            return Trace.build(traceEvents, traceId);
        return null;
    }

    @Override
    public void close() {
        this.elasticClient.close();
    }

    @Override
    public Span getSpanByEventId(String eventId) throws InterruptedException, ExecutionException, IOException {
        Event event = getEvent(eventId);
        if(event != null) {
            String spanId = event.getSpanId();
            return getSpan(spanId);
        }
        return null;
    }

    @Override
    public Span getTraceByEventID(String eventId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Event> searchEvents(int pageNo, int pageSize, String searchTerm, String... fields) throws IOException {
        List<Event> events = new ArrayList<Event>();
        SearchRequestBuilder searchRequest = elasticClient.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.setQuery(QueryBuilders.multiMatchQuery(searchTerm, fields));

        SearchResponse searchResponse = searchRequest.setFrom(pageNo * pageSize).
                setSize(pageSize).
                setExplain(true).
                addSort("timestamp", SortOrder.ASC).
                execute().
                actionGet();

        Iterator<SearchHit> searchHitIterator = searchResponse.getHits().iterator();
        while(searchHitIterator.hasNext()) {
            SearchHit searchHit = searchHitIterator.next();
            events.add(objectMapper.readValue(searchHit.getSourceAsString(), Event.class));
        }

        return events;
    }
}
