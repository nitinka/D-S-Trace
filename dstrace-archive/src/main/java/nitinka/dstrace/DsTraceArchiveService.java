package nitinka.dstrace;

/**
 * User : NitinK.Agarwal@yahoo.com
 */

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import nitinka.dstrace.archive.AbstractEventArchiver;
import nitinka.dstrace.archive.ArchiveBuffer;
import nitinka.dstrace.config.DsTraceArchiveConfiguration;
import nitinka.dstrace.config.EventArchiveConfig;
import nitinka.dstrace.consume.RedisConsumer;
import nitinka.jmetrics.JMetric;
import nitinka.jmetrics.controller.dropwizard.JMetricController;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DsTraceArchiveService extends Service<DsTraceArchiveConfiguration> {

    public DsTraceArchiveService() {}

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
                redisConsumer.stop();
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
                                eventArchiver.archive(ArchiveBuffer.take());
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
            });
        }
        return executorService;
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{"server",args[0]};
        new DsTraceArchiveService().run(args);
    }

}
