D-S-TRACE ( D-Stress )
=========

Distributed tracing in complex distributed application environment is a very basic and most needed capability. I have used available solutions like Zipkin, i must admit not a quickest solution(niether to deploy nor to use) to get started with.<br>
With D-S-Trace I intent to provide distributed tracing platform which can setup in very little time. <br>
<br>
DSTrace consist of 3 components :<br>
* 1) dstrace-publisher : applications should include this library to start sending various events that they want to trace.<br>
* 2) dstrace-archive : receive published events (directly from publisher as http call or subscribed message from Redis topic) and archive message to elastic search.
* 3) dstrace-console : Set of rest end points and UI to search, relate and visualize distributed traces.
