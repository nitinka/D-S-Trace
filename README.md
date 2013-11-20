D-S-TRACE ( D-Stress )
=========

![Alt Image](https://github.com/nitinka/D-S-Trace/raw/master/images/dstrace.png)
<br><br>
Distributed tracing in complex distributed application environment is a very basic and most needed capability. I have used available solutions like Zipkin, i must admit not a quickest solution(niether to deploy nor to use) to get started with.<br>
With D-S-Trace I intent to provide distributed tracing platform which can setup in very little time. <br>
<br>
<h3>DSTrace consist of 3 components :</h3>
<b>1) dstrace-publisher</b> : applications should include this library to start sending various events that they want to trace.<br>
<b>2) dstrace-archive</b> : receive published events (directly from publisher as http call or subscribed message from Redis topic) and archive message to elastic search.<br>
<b>3) dstrace-console</b> : Set of rest end points and UI to search, relate and visualize distributed traces.<br>
<hr size=2>
<h3>Deployment: </h3>
<b>1) Install Redis</b>: Follow http://redis.io/topics/quickstart<br>
<b>2) Install elasticsearch</b>:
<pre>
1) Download elastic Search : https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.90.7.zip
2) unzip
3) cd bin
4) ./elasticsearch -f
5) curl -XPOST 'http://localhost:9200/trace' –d '
{
   "mappings": {
      "event": {
         "properties": {
            "application": {
               "type": "string", "index":"not_analyzed"
            },
            "businessUnit": {
               "type": "string", "index":"not_analyzed"
            },
            "eventId": {
               "type": "string", "index":"not_analyzed"
            },
            "eventName": {
               "type": "string", "index":"not_analyzed"
            },
            "host": {
               "type": "string", "index":"not_analyzed"
            },
            "spanName": {
               "type": "string", "index":"not_analyzed"
            },
            "spanId": {
               "type": "string", "index":"not_analyzed"
            },
            "tags": {
               "type": "object"
            },
            "timestamp": {
               "type": "long"
            }
         }
      }
   }
}'
</pre>
<b>3) Install D-S-Trace Services </b>:
<pre>
1) git clone https://github.com/nitinka/D-S-Trace.git 
2) cd D-S-Trace
3) mvn clean compile package
4) cd dstrace-archive
5) mvn exec:java
6) Open another console
7) cd dstrace-console
8) mvn exec:java
</pre>
<b>4) Done</b>
<hr size=2>
<h3>Getting Started With Tracing: </h3>
<b>1) Include Publisher Library in your application :</b> allows users to publish events to dstrace-archive service.

```xml
<repositories>
    <repository>
        <id>nitinka.mvn.repo</id>
        <url>https://github.com/nitinka/mvn-repo/raw/master</url>
        <!-- use snapshot version -->
        <snapshots>
            <enabled>true</enabled>
            <updatePolicy>always</updatePolicy>
        </snapshots>
    </repository>
</repositories>

<dependency>
    <groupId>nitinka.dstrace.publisher</groupId>
    <artifactId>dstrace-publisher</artifactId>
    <version>0.0.2</version>
</dependency> 
```
</pre>

<b>2) Initialize Publisher/Trace Configuration</b> :Initialize Tracer during the initialization of your application.
```java
TracerConfiguration configuration = new TracerConfiguration();
        configuration.
                setEnabled(true).
                setBusinessUnit("bu1").
                setApplication("app1").
                setEventQueueSize(100000).
                setHost("localhost").
                setSamplePercentage(100).
                setPublishConfiguration(new EventPublishConfiguration().
                        setEventPublishDelay(5000).
                        setEventPublishSize(100).
                        setEnqueueFailedMessages(false).
                        setPublisherClass("nitinka.dstrace.publish.RedisPublisherImpl").
                        setPublisherConfig(new HashMap<String, Object>(){
                            {
                                put("redisHost","localhost"); 
                                put("redisPort","trace-events");
                            }
                        }));

        Tracer.initialize(configuration);
```
<b>3) Publish events from your app</b> :Use Trace class to publish events
```java
        Tracer.setCurrentTraceId(UUID.randomUUID().toString()); // Should be unique transaction id shared across application
        Tracer.startSpan("span1",null, null);
        Thread.sleep(100);
        Tracer.startSpan("span1.1", null, null);
        Thread.sleep(100);
        Tracer.startSpan("span1.1.1", null, null);
        Thread.sleep(100);
        Tracer.startSpan("span1.1.1.1", null, null);
        Thread.sleep(100);
        Tracer.startSpan("span1.1.1.1.1", null, null);
        Thread.sleep(1000);
        Tracer.endSpan();
        Thread.sleep(1000);
        Tracer.endSpan();
        Thread.sleep(1000);
        Tracer.endSpan();
        Thread.sleep(1000);
        Tracer.endSpan();
        Thread.sleep(1000);
        Tracer.endSpan();
```

<b>4) Get the basic trace view </b> :Use trace ID to get the complete distributed transaction
<pre>
http://localhost:8888/index.htm?traceId=5993126e-24e3-4352-99f4-9a425b6fadd9
</pre>
<b>5) Get the raw trace(json)</b> :Use trace ID to get the complete distributed transaction
<pre>
http://localhost:8888/dstrace-console/traces/5993126e-24e3-4352-99f4-9a425b6fadd9
{
  "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
  "startTime": 1384939975603,
  "endTime": 1384939981010,
  "durationMS": 5407,
  "firstSpanId": "1",
  "lastSpanId": "1",
  "spans": {
    "1": {
      "spanId": "1",
      "spanName": "span1",
      "events": [
        {
          "businessUnit": "bu1",
          "application": "app1",
          "host": "localhost",
          "spanName": "span1",
          "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
          "eventId": "a45cae4f-4f86-40ab-82d7-f7f584bfaf05",
          "spanId": "1",
          "parentSpanId": null,
          "eventName": "Start",
          "timestamp": 1384939975603,
          "tags": {
            
          }
        },
        {
          "businessUnit": "bu1",
          "application": "app1",
          "host": "localhost",
          "spanName": "span1",
          "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
          "eventId": "b668d9b5-7182-42c6-89ea-b4fc0c99dfc0",
          "spanId": "1",
          "parentSpanId": null,
          "eventName": "End",
          "timestamp": 1384939981010,
          "tags": {
            
          }
        }
      ],
      "childSpans": {
        "2": {
          "spanId": "2",
          "spanName": "span1.1",
          "events": [
            {
              "businessUnit": "bu1",
              "application": "app1",
              "host": "localhost",
              "spanName": "span1.1",
              "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
              "eventId": "328289a9-8660-4bbc-aaee-03ac3d6e71b3",
              "spanId": "2",
              "parentSpanId": "1",
              "eventName": "Start",
              "timestamp": 1384939975704,
              "tags": {
                
              }
            },
            {
              "businessUnit": "bu1",
              "application": "app1",
              "host": "localhost",
              "spanName": "span1.1",
              "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
              "eventId": "3745dcd9-2077-4373-bd6e-245eacf28b8e",
              "spanId": "2",
              "parentSpanId": "1",
              "eventName": "End",
              "timestamp": 1384939980009,
              "tags": {
                
              }
            }
          ],
          "childSpans": {
            "3": {
              "spanId": "3",
              "spanName": "span1.1.1",
              "events": [
                {
                  "businessUnit": "bu1",
                  "application": "app1",
                  "host": "localhost",
                  "spanName": "span1.1.1",
                  "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
                  "eventId": "ee9391f1-add7-48e6-a287-33152723ea11",
                  "spanId": "3",
                  "parentSpanId": "2",
                  "eventName": "Start",
                  "timestamp": 1384939975805,
                  "tags": {
                    
                  }
                },
                {
                  "businessUnit": "bu1",
                  "application": "app1",
                  "host": "localhost",
                  "spanName": "span1.1.1",
                  "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
                  "eventId": "fb9ca561-41c7-4338-959d-92ce28877860",
                  "spanId": "3",
                  "parentSpanId": "2",
                  "eventName": "End",
                  "timestamp": 1384939979009,
                  "tags": {
                    
                  }
                }
              ],
              "childSpans": {
                "4": {
                  "spanId": "4",
                  "spanName": "span1.1.1.1",
                  "events": [
                    {
                      "businessUnit": "bu1",
                      "application": "app1",
                      "host": "localhost",
                      "spanName": "span1.1.1.1",
                      "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
                      "eventId": "355bf2a1-2fb1-490c-8e08-8085ba11f86a",
                      "spanId": "4",
                      "parentSpanId": "3",
                      "eventName": "Start",
                      "timestamp": 1384939975905,
                      "tags": {
                        
                      }
                    },
                    {
                      "businessUnit": "bu1",
                      "application": "app1",
                      "host": "localhost",
                      "spanName": "span1.1.1.1",
                      "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
                      "eventId": "076071cb-7592-4176-b430-cf36aabe8399",
                      "spanId": "4",
                      "parentSpanId": "3",
                      "eventName": "End",
                      "timestamp": 1384939978008,
                      "tags": {
                        
                      }
                    }
                  ],
                  "childSpans": {
                    "5": {
                      "spanId": "5",
                      "spanName": "span1.1.1.1.1",
                      "events": [
                        {
                          "businessUnit": "bu1",
                          "application": "app1",
                          "host": "localhost",
                          "spanName": "span1.1.1.1.1",
                          "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
                          "eventId": "6edffcca-f351-4b5e-b68d-9e33678c04ce",
                          "spanId": "5",
                          "parentSpanId": "4",
                          "eventName": "Start",
                          "timestamp": 1384939976006,
                          "tags": {
                            
                          }
                        },
                        {
                          "businessUnit": "bu1",
                          "application": "app1",
                          "host": "localhost",
                          "spanName": "span1.1.1.1.1",
                          "traceId": "5993126e-24e3-4352-99f4-9a425b6fadd9",
                          "eventId": "9724f3d2-d314-49e0-8874-95891144d1f3",
                          "spanId": "5",
                          "parentSpanId": "4",
                          "eventName": "End",
                          "timestamp": 1384939977007,
                          "tags": {
                            
                          }
                        }
                      ],
                      "childSpans": {
                        
                      },
                      "duration": 1001
                    }
                  },
                  "duration": 2103
                }
              },
              "duration": 3204
            }
          },
          "duration": 4305
        }
      },
      "duration": 5407
    }
  }
}

</pre>
