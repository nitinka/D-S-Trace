<b>Mapping Configuration for trace in elastic search</>
<pre>
PUT trace
{
  "mappings": {
    "event": {
      "properties": {
        "application": {
          "type": "string",
          "index": "not_analyzed"
        },
        "businessUnit": {
          "type": "string",
          "index": "not_analyzed"
        },
        "eventId": {
          "type": "string",
          "index": "not_analyzed"
        },
        "traceId": {
          "type": "string",
          "index": "not_analyzed"
        },
        "eventName": {
          "type": "string",
          "index": "not_analyzed"
        },
        "host": {
          "type": "string",
          "index": "not_analyzed"
        },
        "spanName": {
          "type": "string",
          "index": "not_analyzed"
        },
        "spanId": {
          "type": "string",
          "index": "not_analyzed"
        },
        "tags": {
          "type": "object"
        },
        "timestamp": {
          "type": "long"
        }
      }
    },
    "businessUnit": {
      "properties": {
        "name": {
          "type": "string",
          "index": "not_analyzed"
        },
        "applications": {
          "type": "nested",
          "index": "not_analyzed",
          "properties": {
            "name": {
              "type": "string"
            },
            "operations": {
              "type": "nested",
              "index": "not_analyzed",
              "properties": {
                "name": {
                  "type": "string",
                  "index": "not_analyzed"
                }
              }
            }
          }
        }
      }
    }
  }
}
</pre>