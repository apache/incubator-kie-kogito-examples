- op: replace
  path: /spec/http/0/route/0/destination/host
  value: {EVENT_LISTENER_SVC}.kogito-github.svc.cluster.local
- op: replace
  path: /spec/http/0/route/0/headers/request/set/Knative-Serving-Revision
  value: {EVENT_LISTENER_SVC}
