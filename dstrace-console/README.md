<b>Mapping Configuration for trace in elastic search</>
<pre>
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
}
</pre>