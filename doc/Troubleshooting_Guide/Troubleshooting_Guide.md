## Troubleshooting

This section describes the troubleshooting functions and procedures for
the Core Analytics Reporting Dimensioning Query (CARDQ) service. It
provides the following information:

-   Simple verification and possible recovery

-   The required information when reporting a bug or writing a support 
    case, including the needed files and system logs

-   How to retrieve the above information from the system

### Prerequisites

-   `kubectl` CLI tool properly configured

### Installation

If CARDQ does not install properly and fails to start, collect the logs and restart the pod.

1- Collect the logs by following the steps in the [Data Collection](#Data-Collection) section below.

2- When the logs are collected restart the pod.

a- To restart one pod, use the Kubernetes delete command:

```text
kubectl delete pod <pod_name> -n <pods_namespace>
```

b- To restart multiple pods, use this command:
```text
kubectl rollout restart deployment <release_name> -n <namespace>
```

Result: The pods are restarted and function properly.
If the pods still fail to start, follow the steps in the [Bug Reporting and
Additional Support](#Bug-Reporting-and-Additional-Support) section below.

### Uninstall a Release

To uninstall the CARDQ application, follow these steps:

1- Collect the logs by following the steps in the [Data Collection](#Data-Collection) section below.

2- Uninstall the application using this command: 
```text
helm uninstall <release_name> -n <namespace>
```
### Service Identity Provider - Transport Layer Security (SIP-TLS)

All pod-to-pod communication involving CARDQ is secured using SIP-TLS with mutual-TLS (mTLS) when TLS is enabled for the microservice.
The ability to enable TLS globally is already provided with `global.security.tls.enable`. If a microservice does not work with SIP-TLS and mTLS, TLS for that connection can be disabled. To disable TLS, use the TLS toggle of the individual microservice to override the global TLS setting.

1- To enable/disable mTLS for CARDQ webserver, set `security.tls.enabled`. No value is set by default. Set 'true' to enable and 'false' to disable.

2- To enable/disable mTLS for requests made to CTS, set `cts.tls.enabled`. By default, this is set as false.

Required Keystore and Truststore, which are used in REST client for mTLS, will be generated when `security.tls.enabled=true`.

3- To enable/disable mTLS for sending logs to Log transformer, set `log.tls.enabled`. By default, this is set as false.

When the microservice settings are disabled, they take precedence over the global TLS setting. This setting is recommended only if a microservice does not support TLS.

Note: When Global TLS is disabled, enabling microservice settings for TLS is not recommended. These overrides will not be valid and application will not function properly.


#### ResourceAccessException

ResourceAccessException may occur while calling CTS over HTTPS from CARDQ webserver.

```text
{
"type":null,
"title":"Connect Timed Out, ResourceAccessException",
"status":500,
"detail":"Connection Timed Out, Retried: 5, Reason: I/O error on GET request for  a\"https://eric-oss-cts-stub:8443/oss-core-ws/rest/ctw/wirelessnetfunction\ is java.net.SocketException: Connection reset",
"instance":null
}
````
There are two possible causes for this exception:

1- When CTS pod is not installed or not running properly.

2- If CTS pod is installed, running properly and CARDQ sends requests to CTS using HTTPS (`cts.tls.enabled` is set as true), verify that `security.tls.enabled` is also set to true.
The Keystore and Truststore that are used in REST client for mTLS are generated only when mTLS for CARDQ webserver is enabled. The mode of requests made to CTS should match CARDQ webserver TLS mode.

### Health Checks

To check the health of the service, go to the `<service_address>/actuator/health` endpoint

### Enable Debug Logging

To enable the debug logging on a fresh install, run: 
```text
helm upgrade --install --atomic <release name> <CRD chart with version> --namespace <NAMESPACE-FOR-CRDs> --set log.control.severity=debug
```

Alternatively, change the `data.logcontrol.json.severity` value to "debug" 
in `eric-oss-core-reporting-dimension-query-configmap`.

<!--### Log Categories

Log Categories are used to support automatic filtering which enables the
possibility to support artificial intelligence (AI) and machine learning. Log categories provided by
the service are listed in the table below.

| Category Name     | Security Log | Description              |
| ----------------- | -------------| ------------------------ |
| <*category_name*> | <*yes_no*>   | <*category_description*> |
| ... | ... | ... |
-->

### Data Collection

If the log mode is set to "indirect" or "dual", use this command:
```text
text kubectl logs <pod_name>  kubectl logs <pod_name> -n <namespace> 
```
If required, the log can be directed to a file.

Alternatively, if the logging mode is set to "direct" or "dual", do the following:

1- Login to the data-search-engine-ingest pod using this command:
```text
kubectl exec -n <namespace> <data-search-ingest-pod> -it -- bash
```

2- Run this command while in the data-search-engine-ingest pod:
```text
? esRest GET "/_cat/indices?v"
health status index                   uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   adp-app-logs-2023.05.12 <uuid>   5   1         28            0    435.4kb          224kb
```
Result: In the log file table, find the index name that will be used in the next step. It will look similar to this "adp-app-logs-2023.05.12".

3- Use the file name from the output of step 2 in the following command:
```
? esRest GET "/<index_from_previous_output>/_search?pretty"
```

4- Collect the detailed information about the pod using this command:

```text
kubectl describe pod <pod name> --namespace=<pod's namespace>
kubectl exec --namespace=<pod's namespace> <pod-name> -- env
```

### Bug Reporting and Additional Support

Issues can be handled in different ways:

-   For questions, support or hot requesting, see
    [Additional Support](#Additional-Support).

-   For reporting of faults, see [Bug Reporting](#Bug-Reporting).

#### Additional Support

If there are CARDQ service support issues, use the
[JIRA][jira]/[JIRA][jira2].

#### Bug Reporting

If there is a suspected fault, report a bug. The bug report must
contain specific CARDQ service information and the
applicable troubleshooting information highlighted in the
[Troubleshooting](#Troubleshooting), and [Data Collection](#Data-Collection).

Once Support is more familiar with the system, they will be able to determine
if the suspected faults can be resolved by restarting the pod.

Use [JIRA][jira]/[JIRA][jira2] to report and track bugs.

*When reporting a bug for the CARDQ service, specify
the following in the JIRA issue:*

- *Issue type: Bug*
- Component: CARDQ
- Reported from: the level at which the issue was originally detected
  (ADP Program, Application, Customer, etc.)*
- Application: identity of the application where the issue was 
   observed
  (if applicable)*
- Business Impact: the business impact caused by the issue for the 
   affected users*

### Recovery Procedure

Since the service is not stateful, there is no data to be
recovered.

#### Restarting the Pod

Kubernetes will recreate the pod after it is deleted.

<!--### Alarm Handling

Alarm handling will be taken care of in a different step in the future.-->

### Known Issues

No known issues

## References

[ADP Generic Services Support JIRA][jira]

[jira]: https://eteamproject.internal.ericsson.com/projects/GSSUPP
[jira2]: https://jira-oss.seli.wh.rnd.internal.ericsson.com/