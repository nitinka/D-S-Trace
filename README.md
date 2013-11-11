D-S-TRACE ( D-Stress )
=========

Distributed tracing in complex distributed application environment is a very basic and most needed capability. I have used available solutions like Zipkin, i must admit not a quickest solution(niether to deploy nor to use) to get started with.<br>
With D-S-Trace I intent to provide distributed tracing platform which can setup in very little time. <br>
<br>
<b>DSTrace consist of 3 components :</b><br>
* <b>1) dstrace-publisher</b> : applications should include this library to start sending various events that they want to trace.<br>
* <b>2) dstrace-archive</b> : receive published events (directly from publisher as http call or subscribed message from Redis topic) and archive message to elastic search.
* <b>3) dstrace-console</b> : Set of rest end points and UI to search, relate and visualize distributed traces.
