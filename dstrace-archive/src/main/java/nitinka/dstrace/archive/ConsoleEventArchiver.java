package nitinka.dstrace.archive;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import nitinka.dstrace.util.ObjectMapperUtil;
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
public class ConsoleEventArchiver extends AbstractEventArchiver {

    private ObjectMapper objectMapper = ObjectMapperUtil.instance();
    private static Logger logger = LoggerFactory.getLogger(ConsoleEventArchiver.class);

    public ConsoleEventArchiver(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void archive(String events) throws IOException, ExecutionException, InterruptedException {
        logger.info("Archiving Events :"+events);
    }

    @Override
    public void close() {
    }
}
