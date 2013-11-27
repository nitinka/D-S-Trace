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
import org.elasticsearch.index.query.BoolQueryBuilder;
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
    private final String index;

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
    }

    @Override
    public Event getEvent(String eventId) throws IOException, ExecutionException, InterruptedException {
        GetResponse response = elasticClient.prepareGet(index, "event", eventId)
                .execute()
                .actionGet();

        if(response.getSource() != null)
            return objectMapper.readValue(response.getSourceAsBytes(), Event.class);
        return null;
    }

    @Override
    public Span getSpan(String spanId) throws IOException {
        Map<String,String> fields = new HashMap<String, String>();
        fields.put("spanId", spanId);
        fields.put("parentSpanId", spanId);

        List<Event> spanEvents = searchEvents(0, 1000, fields);
        if(spanEvents.size() > 0)
            return Span.build(spanEvents);
        return null;
    }

    @Override
    public Trace getTrace(String traceId) throws IOException {
        Map<String,String> fields = new HashMap<String, String>();
        fields.put("traceId", traceId);
        List<Event> traceEvents = searchEvents(0, 1000, fields);
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
    public List<Event> searchEvents(int pageNo, int pageSize, Map<String, String> fields) throws IOException {
        List<Event> events = new ArrayList<Event>();
        SearchRequestBuilder searchRequest = elasticClient.prepareSearch(index)
                .setTypes("event")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(String key : fields.keySet()) {
            boolQueryBuilder.must(QueryBuilders.termQuery(key, fields.get(key)));
        }
        searchRequest.setQuery(boolQueryBuilder);

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


    @Override
    public List<Trace> searchTraces(int pageNo, int pageSize, Map<String, String> fields) throws IOException {
        List<Trace> traces = new ArrayList<Trace>();

        SearchRequestBuilder searchRequest = elasticClient.prepareSearch(index)
                .setTypes("event")
                .addField("traceId")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for(String key : fields.keySet()) {
            boolQueryBuilder.must(QueryBuilders.termQuery(key, fields.get(key)));
        }
        searchRequest.setQuery(boolQueryBuilder);

        SearchResponse searchResponse = searchRequest.setFrom(pageNo * pageSize * 100).
                setSize(pageSize * 100).
                setExplain(true).
                addSort("timestamp", SortOrder.ASC).
                execute().
                actionGet();

        Iterator<SearchHit> searchHitIterator = searchResponse.getHits().iterator();
        Set<String> traceIds = new TreeSet<String>();
        while(searchHitIterator.hasNext()) {
            SearchHit searchHit = searchHitIterator.next();
            String traceId = searchHit.getFields().get("traceId").getValue().toString();
            if(traceIds.add(traceId)) {
                traces.add(getTrace(traceId));
            }
            if(traceIds.size() == pageSize)
                break;
        }

        return traces;
    }

}
