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

