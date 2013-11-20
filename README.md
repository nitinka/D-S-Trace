D-S-TRACE ( D-Stress )
=========

Distributed tracing in complex distributed application environment is a very basic and most needed capability. I have used available solutions like Zipkin, i must admit not a quickest solution(niether to deploy nor to use) to get started with.<br>
With D-S-Trace I intent to provide distributed tracing platform which can setup in very little time. <br>
<br>
<b>DSTrace consist of 3 components :</b><br>
* <b>1) dstrace-publisher</b> : applications should include this library to start sending various events that they want to trace.<br>
* <b>2) dstrace-archive</b> : receive published events (directly from publisher as http call or subscribed message from Redis topic) and archive message to elastic search.
* <b>3) dstrace-console</b> : Set of rest end points and UI to search, relate and visualize distributed traces.
<br>
<br>
![Alt Image](https://github.com/nitinka/D-S-Trace/raw/master/images/dstrace.png)
<br><br>
<b>Deployment</b>
* <b>1) Install Redis</b>: Follow http://redis.io/topics/quickstart
* <b>2) Install elasticsearch</b>:
<pre>
1) Download elastic Search : https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-0.90.7.zip
2) unzip
3) cd bin
4) ./elasticsearch -f
</pre>
* <b>3) Install D-S-Trace Services </b>:
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
* <b>4) Done</b>

