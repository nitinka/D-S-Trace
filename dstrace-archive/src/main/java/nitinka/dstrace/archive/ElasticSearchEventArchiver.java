package nitinka.dstrace.archive;

import nitinka.dstrace.archive.domain.Application;
import nitinka.dstrace.archive.domain.BusinessUnit;
import nitinka.dstrace.archive.domain.Event;
import nitinka.dstrace.archive.domain.Operation;
import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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
public class ElasticSearchEventArchiver extends AbstractEventArchiver {
    private Client elasticClient ;
    private ObjectMapper objectMapper = ObjectMapperUtil.instance();
    private static Logger logger = LoggerFactory.getLogger(ElasticSearchEventArchiver.class);
    private String index;

    public ElasticSearchEventArchiver(Map<String, Object> config) {
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
    public void archive(List<Event> events) throws IOException, ExecutionException, InterruptedException {
        for(Event event : events) {
            IndexResponse indexResponse = createIndex(index, "event", event.getEventId(), event);
        }
    }

    @Override
    public void updateBusinessUnit(List<BusinessUnit> businessUnit) throws Exception {
        for(BusinessUnit newBu : businessUnit) {
            GetResponse response = elasticClient.prepareGet(index, "businessUnit", newBu.getName())
                    .execute()
                    .actionGet();

            if(response.isExists()) {
                BusinessUnit existingBu = objectMapper.readValue(response.getSourceAsBytes(), BusinessUnit.class);
                for(Application newApp : newBu.getApplications()) {
                    if(existingBu.getApplications().contains(newApp)) {
                        // Get existing application and update the operations
                        Application existingApp = existingBu.getApplications().get(existingBu.getApplications().indexOf(newApp));
                        Set<Operation> existingOperations = new HashSet<Operation>(existingApp.getOperations());

                        for(Operation newOp : newApp.getOperations()) {
                            if(!existingApp.getOperations().contains(newOp))
                                existingOperations.add(newOp);
                        }
                        existingApp.setOperations(new ArrayList<Operation>(existingOperations));
                    }
                    else {
                        existingBu.addApplication(newApp);
                    }
                }

                IndexResponse indexResponse = createIndex(index, "businessUnit", existingBu.getName(), existingBu);
            }
            else {
                // create new business unit
                IndexResponse indexResponse = createIndex(index, "businessUnit", newBu.getName(), newBu);
            }
        }
    }

    private IndexResponse createIndex(String index, String type, String id, Object doc) throws IOException {
        return createIndex(index, type, id, doc, -1);
    }

    private IndexResponse createIndex(String index, String type, String id, Object doc, long version) throws IOException {
        IndexRequestBuilder requestBuilder = elasticClient.prepareIndex(index, type, id).
                setSource(objectMapper.writeValueAsBytes(doc));
        if(version > 0)
            requestBuilder.setVersion(version);

        IndexResponse indexResponse = requestBuilder.
                execute().
                actionGet();
        /**
         * TBD: Handle failure
         */
        return indexResponse;
    }

    @Override
    public void close() {
        this.elasticClient.close();
    }
}
