package nitinka.dstrace;

/**
 * User : NitinK.Agarwal@yahoo.com
 */

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import nitinka.dstrace.archive.AbstractEventArchiver;
import nitinka.dstrace.archive.ArchiveBuffer;
import nitinka.dstrace.archive.domain.Application;
import nitinka.dstrace.archive.domain.BusinessUnit;
import nitinka.dstrace.archive.domain.Event;
import nitinka.dstrace.archive.domain.Operation;
import nitinka.dstrace.config.DsTraceArchiveConfiguration;
import nitinka.dstrace.config.EventArchiveConfig;
import nitinka.dstrace.consume.RedisConsumer;
import nitinka.dstrace.util.CollectionHelper;
import nitinka.dstrace.util.ObjectMapperUtil;
import nitinka.jmetrics.JMetric;
import nitinka.jmetrics.controller.dropwizard.JMetricController;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DsTraceArchiveService extends Service<DsTraceArchiveConfiguration> {

    public DsTraceArchiveService() {}
    private static Set<String> uniqueOperations = new HashSet<String>();

    @Override
    public void initialize(Bootstrap<DsTraceArchiveConfiguration> bootstrap) {
        bootstrap.setName("loader-server");
    }

    @Override
    public void run(DsTraceArchiveConfiguration configuration, Environment environment) throws Exception {
        JMetric.initialize(configuration.getjMetricConfig());
        environment.addResource(new JMetricController());

        final RedisConsumer redisConsumer = new RedisConsumer(configuration.getRedisConsumerConfig());
        redisConsumer.start();

        final ExecutorService archiveService = startEventArchivingService(configuration.getEventArchiveConfig());

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                redisConsumer.stopIt();
                archiveService.shutdown();
            }
        });
    }

    private ExecutorService startEventArchivingService(final EventArchiveConfig eventArchiveConfig) {
        ExecutorService executorService = Executors.newFixedThreadPool(eventArchiveConfig.getArchiverThreads());
        for(int i=1; i<eventArchiveConfig.getArchiverThreads(); i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        AbstractEventArchiver eventArchiver = AbstractEventArchiver.build(eventArchiveConfig);
                        while(true){
                            try {
                                String eventsStr = ArchiveBuffer.take();
                                List<Event> eventList = CollectionHelper.transformList(ObjectMapperUtil.instance().readValue(eventsStr, List.class), Event.class);
                                eventArchiver.archive(eventList);
                                eventArchiver.updateBusinessUnit(getChangedBusinessUnits(eventList));
                            } catch (InterruptedException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            } catch (Exception e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (InstantiationException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                private List<BusinessUnit> getChangedBusinessUnits(List<Event> eventList) {
                    Map<String,BusinessUnit> changedBusinessUnits = new HashMap<String, BusinessUnit>();
                    for(Event event : eventList) {
                        String absoluteOperation = "" + event.getBusinessUnit()
                                + "." + event.getApplication()
                                + "." +event.getSpanName();
                        synchronized (uniqueOperations) {
                            if(!uniqueOperations.contains(absoluteOperation)) {
                                BusinessUnit bu = changedBusinessUnits.get(event.getBusinessUnit());
                                if(bu == null)
                                    bu = new BusinessUnit().setName(event.getBusinessUnit());

                                Application application = null;
                                if(bu.getApplications().contains(new Application().setName(event.getApplication()))) {
                                    application = bu.getApplications().get(bu.getApplications().indexOf(new Application().setName(event.getApplication())));
                                }
                                else {
                                    application = new Application().setName(event.getApplication());
                                    bu.addApplication(application);
                                }

                                Operation operation = new Operation().setName(event.getSpanName());
                                application.addOperation(operation);
                                changedBusinessUnits.put(bu.getName(), bu);
                                uniqueOperations.add(absoluteOperation);
                            }
                        }
                    }
                    return new ArrayList<BusinessUnit>(changedBusinessUnits.values());
                }
            });
        }
        return executorService;
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"server",args[0]};
        new DsTraceArchiveService().run(args);
    }

}
