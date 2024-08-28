<!--Document information:
Prepared:Team Vibranium
Approved:
Document Name:service-deployment-guide
Revision: {!.bob/var.service-deployment-guide-version!}
Date: {!.bob/var.date!}
-->

# *Core Analytics Reporting Dimensioning Query* Service Deployment Guide

[TOC]

## Deployment
This section describes the operational procedures for how to deploy and upgrade the Core Analytics Reporting Dimensioning Query (CARDQ) Service in a Kubernetes environment with Helm. It also covers hardening guidelines to consider when deploying this service.

### Prerequisites
- A running Kubernetes environment with Helm support
- Information related to the Kubernetes environment including the networking details
- Access rights to deploy and manage workloads
- Availability of the kubectl CLI tool with correct authentication details. Contact the Kubernetes System Admin if necessary
- Availability of the Helm package
- Availability of Helm charts and Docker images for the CARDQ and all dependent services

### Custom Resource Definition Deployment
Custom Resource Definitions (CRD) are cluster-wide resources that must be installed prior to the Generic Service deployment. The latest CRD charts version set must be used in the deployment.
>Note! The release name chosen for the CRD charts in the Kubernetes deployment must be kept and cannot be changed once they are installed.

Helm always requires a namespace to deploy a chart. The namespace that will be used to install the service CRD charts must be created before the
CRD charts are loaded.
>Note! The upgrade of the CRD charts must be done before deploying a service when the service requires either:
> - additional CRD charts
> - CRD version newer than the one already deployed on the Kubernetes cluster


The CRD charts are deployed as follows:
1. Download the latest applicable CRD charts files from the Helm charts repository.
2. Create the `namespace` where the CRD charts will be deployed if it doesn't already exist. This step is only done once on the Kubernetes cluster.
    ```
    kubectl create namespace <NAMESPACE-FOR-CRDs>
    ```
3. Deploy the CRD charts using `helm upgrade` command:
   ```
   helm upgrade --install --atomic <release name> <CRD chart with version> --namespace <NAMESPACE-FOR-CRDs>
   ```
4. Validate the CRD installation with the `helm ls` command:
   ```
   helm ls -a --namespace <NAMESPACE-FOR-CRDs>
   ```
In the CRD validation table, verify the following:
- Locate the entry for the `<release name>`
- Validate the `NAMESPACE` value
- Check that the STATUS is set to `deployed`
- When the output of CRD installation is validated, continue with the installation of the Helm charts.


### Deployment in a Kubernetes Environment using Helm
This section describes how to deploy the service in Kubernetes using Helm and the `kubectl` CLI client. Helm is a package manager for Kubernetes that streamlines the installation and management of Kubernetes applications.

#### Preparation
Prepare Helm chart and Docker images. Use the Helm chart in the following link for installation:

`https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-drop-helm-local/eric-oss-core-reporting-dimension-query/eric-oss-core-reporting-dimension-query-<version>.tgz`

#### CARDQ Pre-Deployment Checks
Ensure the following:
- The <RELEASE_NAME> is not used already in the corresponding cluster. Use  `helm list`  command to list the existing deployments. Delete previous deployment with the corresponding <RELEASE_NAME> if needed.
- The same namespace is used for all deployments.

#### Helm Chart Installations of Dependent Services
- #####  Common Topology Service (CTS)
  CARDQ sends queries to CTS to gather topology information and to explore and gather data related to a provided network function (NF) identifier. Due to this dependency, CARDQ and CTS should be deployed in the same namespace.

  The CARDQ service will not function properly without CTS. CTS creates some Kubernetes secrets (such as the user credentials information) and provides these secrets to CARDQ. If CTS is not deployed in the same namespace, CARDQ will be successfully deployed, but will fail to retrieve the data from CTS.

- ##### Assurance Topology Graph (ATG)
  CARDQ has the capability to query topology information from Neo4j DB, which is called [Assurance Topology Graph (ATG)](https://adp.ericsson.se/marketplace/assurance-topology-graph).

  If `neo4j.enabled` is set to true at the deployment time, ATG must be deployed in the same namespace to ensure CARDQ returns a proper response.
  If ATG is not deployed in the same namespace, CARDQ will successfully deploy but will fail to retrieve the data from ATG.

  CARDQ needs credentials to properly connect to Neo4j DB. The secret will be created if Assurance Topology Notification (ATN) is deployed under the same namespace.
  Please refer to [ATN Service Deployment Guide](https://adp.ericsson.se/marketplace/assurance-topology-notification/documentation/1.84.0-1/dpi/service-deployment-guide) for detailed steps.

  Alternatively, using below command to generate credentials for CARDQ:
  ```
  kubectl create secret generic eric-bos-assurance-topology-notification-secret --from-literal=reader-name=<READER_NAME> --from-literal=reader-pass=<READER_PASSWORD>
  ```

- ##### Integration of ADP logging
  According to the Helm design rule DR-D470222-010, it is required that all services support three logging methods that align with the ADP log 
  collection pattern. Each logging method must be controlled by the log.streamingMethod configuration.
  - “indirect”: Stdout to infrastructure logging framework.
  - “direct”: Direct streaming to the Log Aggregator (Log Transformer).
  - “dual”: Stdout to infrastructure logging framework and direct streaming to Log Aggregator.
  - "null": null or absent parameter the streaming method is determined by global.log.streamingMethod.

  ADP logging provides CARDQ the capability to support indirect, direct and dual logging methods. However, in direct logging method, logs are only accessible through search engines and are not visible to CARDQ.

  For more guidance about installation and implementation, refer to the tutorial for [ADP Logging](https://adp.ericsson.se/workinginadpframework/tutorials/logging-in-adp/introduction-to-logging-architecture).
  
  
- ##### Integration of PM Server:
  PM Server is required to scrape the CARDQ metrices. In order to integrate the PM Server with CARDQ, both services must be deployed in the 
  same namespace.
  If PM Server is not deployed in the same namespace, CARDQ will run successfully, but PM Server will fail to scrape the CARDQ metrices.

  For more details about the PM server, refer to the [Service User Guide](https://adp.ericsson.se/marketplace/pm-server).

#### Helm Chart Installation of CARDQ Service
>Note! Ensure all dependent services are deployed and healthy before you continue with this step (see [Deployment](#Deployment)).

Helm is a tool that streamlines installing and managing Kubernetes applications. CARDQ can be deployed on Kubernetes using Helm Charts. Charts are packages of pre-configured Kubernetes resources.

Users can override the default values provided in the `values.yaml` template of the Helm chart. The recommended parameters to override are listed
in [Configuration Parameters](#Configuration-Parameters).

##### Create Kubernetes Secret for CARDQ
Kubernetes secrets need to be created before installing the Helm chart.
There are no Kubernetes secrets created for CARDQ because all the required secrets would be pulled from CTS chart when the service gets deployed.

##### Deploy the CARDQ Service
Install the CARDQ service on the Kubernetes cluster using the Helm installation command:
```
helm install <RELEASE_NAME> <CHART_REFERENCE> --namespace=<NAMESPACE> [--set <parameters>]
```
The variables specified in the command are as follows:
- `<RELEASE_NAME>`: String value, a name to identify and manage the Helm chart.
- `<CHART_REFERENCE>`: A path to a packaged chart, a path to an unpacked chart directory or a URL.
- `<NAMESPACE>`: String value, a name to be used dedicated by the user for deploying the Helm charts.

Helm install command for successful CARDQ installation:
```
helm install cardq eric-oss-core-reporting-dimension-query-1.32.0-1.tgz 
```
<*The above mentioned version might not be the most up-to-date version of CARDQ service*>

##### Verify the CARDQ Service Availability
To verify whether the deployment is successful, do the following:
1. *Check if the chart is installed with the provided release name and in the related namespace using the following command:*
```text
helm ls --namespace=<NAMESPACE>
```
*Chart status should be reported as "DEPLOYED".*

2. *Verify the status of the deployed Helm chart.*
```text
helm status <RELEASE_NAME>
```
*Chart status should be reported as "DEPLOYED". All Pods status should be reported as "Running" and the number of deployment available should be the
same as the replica count.*

3. *Verify that the pods are running by getting the status of the pods.*
```text
kubectl get pods --namespace=<NAMESPACE> -L role
```
*For example:*
```text
helm ls --namespace=example
helm status examplerelease
kubectl get pods --namespace=example -L role
```

#### Troubleshooting

Please check the [Troubleshooting Guide](https://adp.ericsson.se/marketplace/core-analytics-reporting-dimensioning-qu/documentation/latest/additional-documents/troubleshooting-guide) for detailed information on troubleshooting, operation and
maintenance.


### Configuration Parameters
#### Mandatory Configuration Parameters
Mandatory configuration parameters must be set at deployment. If not provided, the deployment will fail. There are no default values provided for this type of parameters.

#### Optional Configuration Parameters
The following parameters are not mandatory. If not explicitly set using the `--set argument`, the default values provided in the Helm chart are used.

| Variable Name                           | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | Default Value                                           |
|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| replicaCount                            | CARDQ Replica Count                                                                                                                                                                                                                                                                                                                                                                                                                                                                 | 2                                                       |
| appArmorProfile.type                    | AppArmor profile type for the CARDQ service container                                                                                                                                                                                                                                                                                                                                                                                                                               | ""                                                      |
| cts.enabled                             | Use Common Topology Service as the service to gather topology information                                                                                                                                                                                                                                                                                                                                                                                                           | true                                                    |
| cts.tls.enabled                         | Uses TLS to connect to CTS Note: If the CTS is not compatible with SIP-TLS and mTLS, TLS for that connection can be disabled. To disable TLS, use the CTS TLS toggle to override the global TLS setting.                                                                                                                                                                                                                                                                            | false                                                   |
| cts.secret.name                         | CTS secret name                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | eric-oss-cmn-topology-svc-core-adminuser-credentials    |
| cts.url.host                            | CTS host URL                                                                                                                                                                                                                                                                                                                                                                                                                                                                        | eric-oss-cmn-topology-svc-core.{{ .Release.Namespace }} |
| cts.url.port                            | CTS port                                                                                                                                                                                                                                                                                                                                                                                                                                                                            | 8080                                                    |
| cts.secret.userKey                      | Name of the user key in CTS secret                                                                                                                                                                                                                                                                                                                                                                                                                                                  | technicalUser.name                                      |
| cts.secret.passwordKey                  | Name of the password key in CTS secret                                                                                                                                                                                                                                                                                                                                                                                                                                              | technicalUser.password                                  |
| cts.database.name                       | CTS Database name                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | eai_install                                             |
| cts.database.host                       | CTS host name                                                                                                                                                                                                                                                                                                                                                                                                                                                                       | localhost                                               |
| cts.retry.delay                         | Application waiting time before retrying the request (unit: millisecond)                                                                                                                                                                                                                                                                                                                                                                                                            | 20000                                                   |
| cts.retry.maxAttempts                   | Number of maximum retrying attempts while sending a request to CTS                                                                                                                                                                                                                                                                                                                                                                                                                  | 5                                                       |
| cache.expiryTimeSeconds                 | The maximum amount of time the cache will be present                                                                                                                                                                                                                                                                                                                                                                                                                                | 900                                                     |
| cache.maxEntries                        | The maximum capacity of cache memory to store the responses                                                                                                                                                                                                                                                                                                                                                                                                                         | 5000                                                    |
| seccompProfile.type                     | Seccomp profile type for the CARDQ service container                                                                                                                                                                                                                                                                                                                                                                                                                                | ""                                                      |
| security.tls.enabled                    | Webserver will use TLS for responding to requests. Note: The main use of override is to disable TLS as needed. If not all CARDQ clients are compatible with SIP-TLS and mTLS, TLS for that connection can be disabled. To disable TLS, use the webserver TLS toggle to override the global TLS setting.                                                                                                                                                                             | false                                                   |
| service.type                            | The default service type is ClusterIP, so CARDQ will be accessible in the Kubernetes cluster only.                                                                                                                                                                                                                                                                                                                                                                                  | ClusterIP                                               |
| service.port                            | CARDQ HTTP port                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | 8080                                                    |
| service.tls.port                        | CARDQ HTTPS port                                                                                                                                                                                                                                                                                                                                                                                                                                                                    | 8443                                                    |
| log.streamingMethod                     | Logging streaming method. Options: direct, indirect, dual, null or plain. direct: Direct streaming to the Log Aggregator (Log Transformer). indirect: Stdout to infrastructure logging framework in json format.  dual: Stdout to infrastructure logging framework and direct streaming to Log Aggregator. null: null or absent parameter the streaming method is determined by global.log.streamingMethod, plain: Stdout to infrastructure logging framework in plain text format. | null                                                    |
| log.tls.enabled                         | Uses TLS to connect to Log transformer Note: If the Log transformer is not compatible with SIP-TLS and mTLS, TLS for that connection can be disabled. To disable TLS, use the Log TLS toggle to override the global TLS setting.                                                                                                                                                                                                                                                    | false                                                   |
| log.control.enabled                     | Enable changing logging level at runtime.                                                                                                                                                                                                                                                                                                                                                                                                                                           | true                                                    |
| log.control.file                        | Path to log control configuration file                                                                                                                                                                                                                                                                                                                                                                                                                                              | /config/logcontrol.json                                 |
| log.control.severity                    | The initial log control severity level, can be updated at runtime updating configmap values                                                                                                                                                                                                                                                                                                                                                                                         | info                                                    |
| observability.client.enabled            | Enables Observability configuration for adding URI tags to client requests for `http_client_requests_*` metrics                                                                                                                                                                                                                                                                                                                                                                     | true                                                    |
| observability.client.add-query          | When observability enabled, populated uri tags can be either partial or complete. This setting enables adding query part of the URI when the metric tag is being created. Example: Enabled: uri="/path?key=value", Disabled: uri="/path"                                                                                                                                                                                                                                            | false                                                   |
| neo4j.enabled                           | Use neo4j database to gather topology information                                                                                                                                                                                                                                                                                                                                                                                                                                   | false                                                   |
| neo4j.query.path                        | Path to Neo4j query configuration file                                                                                                                                                                                                                                                                                                                                                                                                                                              | /config/queries                                         |
| neo4j.uri                               | The neo4j server uri this driver should connect to                                                                                                                                                                                                                                                                                                                                                                                                                                  | neo4j://eric-bos-neo4j-graphdb:7687                     |
| neo4j.database                          | The neo4j database name                                                                                                                                                                                                                                                                                                                                                                                                                                                             | cardqdb                                                 |
| neo4j.secret.secretName                 | The neo4j database user credential secret name                                                                                                                                                                                                                                                                                                                                                                                                                                      | eric-bos-assurance-topology-notification-secret         |
| neo4j.secret.secretKey                  | The Key of customized user password in the neo4j database user credential secret                                                                                                                                                                                                                                                                                                                                                                                                    | reader-pass                                             |
| neo4j.secret.usernameKey                | The Key of customized user name in the neo4j database user credential secret                                                                                                                                                                                                                                                                                                                                                                                                        | reader-name                                             |
| neo4j.pool.metricsEnabled               | Enable Neo4j pool metrics                                                                                                                                                                                                                                                                                                                                                                                                                                                           | true                                                    |
| neo4j.pool.connectionAcquisitionTimeout | Acquisition of new connections will be attempted for at most configured timeout (unit: second)                                                                                                                                                                                                                                                                                                                                                                                      | 60                                                      |
| neo4j.pool.maxConnectionLifetime        | Pooled connections older than this threshold will be closed and removed from the pool (unit: second)                                                                                                                                                                                                                                                                                                                                                                                | 3600                                                    |
| neo4j.pool.maxConnectionPoolSize        | The maximum amount of connections in the connection pool towards a single database                                                                                                                                                                                                                                                                                                                                                                                                  | 100                                                     |
| neo4j.pool.logLeakedSessions            | Enable leaked sessions logging                                                                                                                                                                                                                                                                                                                                                                                                                                                      | false                                                   |
| neo4j.config.connectionTimeout          | Specify socket connection timeout (unit: second)                                                                                                                                                                                                                                                                                                                                                                                                                                    | 30                                                      |
| neo4j.config.maxTransactionRetryTime    | Specify the maximum amount of time that transactions are allowed to retry  (unit: second)                                                                                                                                                                                                                                                                                                                                                                                           | 30                                                      |
| neo4j.retry.delay                       | Application waiting time before retrying the request to Neo4j (unit: millisecond)                                                                                                                                                                                                                                                                                                                                                                                                   | 20000                                                   |
| neo4j.retry.maxAttempts                 | Number of maximum retrying attempts while sending a request to Neo4j                                                                                                                                                                                                                                                                                                                                                                                                                | 5                                                       |

> Note! The initial capacity of cache is determined by the cache's maxEntries value divided by 5.

### Hardening

#### Hardening during product development

* The service is built on a minimalistic container images with small footprints. Only the required libraries are included
* The service utilizes a container optimized operating system (Common Base OS) and latest security patches are applied
* The containers go through vulnerability scanning
* The service is configured to the strict minimum of services and ports to minimize the attack surface

#### Hardening during service delivery

The service can be further hardened by configuring settings related to AppArmor and Seccomp which are done through the Helm parameters located
under `.Values.appArmorProfile` and `.Values.seccompProfile`, respectively.

To use the default AppArmor and Seccomp profiles, one can install CARDQ by setting these parameters during installation:

```shell
--set appArmorProfile.type="",seccompProfile.type=""
```

See [Configuration Parameters](#configuration-parameters) for more details.


### Service Dimensioning
By default, the service provides resource request values and resource limit values as part of the Helm chart. These values correspond to a default
size for deployment of an instance. This section provides guidance in how to do service dimensioning and how to change the default values when needed.

#### Override Default Dimensioning Configuration
If other values than the default resource request and default resource limit values are preferred, the default values must be overridden at deployment.
Here is an example of the `helm install` command where resource requests and resource limits are set:
```
helm install https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-drop-helm-local/eric-oss-core-reporting-dimension-query/eric-oss-core
-reporting-dimension-query-<version>.tgz --name cardq --namespace <namespace> --set <*ADD request and limit parameters valid for this service*>
```

#### Use Minimum Configuration per Service Instance

| Resource Type (Kubernetes Service)      | Resource Request Memory | Resource Limit Memory               | Resource Request CPU  |  Resource Limit CPU   |
|-----------------------------------------|----------|-----------------------|---------|-----|
| eric-oss-core-reporting-dimension-query | 2Gi    | 4Gi | 250m |  400m   |

#### Use Maximum (Default) Configuration per Service Instance
The maximum recommended configuration per instance is provided as default in the Helm chart. Both resource request values and resource limit values are included in the Helm charts.
