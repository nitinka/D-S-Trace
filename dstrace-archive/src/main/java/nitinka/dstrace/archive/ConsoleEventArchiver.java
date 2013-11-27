package nitinka.dstrace.archive;

import nitinka.dstrace.archive.domain.BusinessUnit;
import nitinka.dstrace.archive.domain.Event;
import nitinka.dstrace.util.ObjectMapperUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: nitinka
 * Date: 11/11/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ConsoleEventArchiver extends AbstractEventArchiver {

    protected ConsoleEventArchiver(Map<String, Object> config) {
        super(config);
    }

    @Override
    public void archive(List<Event> events) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBusinessUnit(List<BusinessUnit> businessUnit) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void close() throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
