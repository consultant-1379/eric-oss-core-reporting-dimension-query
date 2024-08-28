# Core Analytics Reporting Dimensioning Query (CARDQ)

This service is to query topology information from the CTS (Common Topology Service) to be able to aggregate PM (Performance Metrics) counters for 5G Core Slices. CARDQ acts as an augmentation information provider to add slice data from CTS to PM counter records augmented by the AAS (Assurance Augmentation Service).

It is a Java Spring Boot application created from the Microservice Chassis.

## Contact Information
#### Team Members
##### CARDQ
[Team Vibranium](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/pages/viewpage.action?spaceKey=IDUN&title=Team+Vibranium) is currently the acting development team working on CARDQ. For support please contact Team Vibranium through [vibranium_team](mailto:PDLVIBRANI@pdl.internal.ericsson.com)

##### CI Pipeline
The CI Pipeline aspect of this Microservice is now owned, developed and maintained by [Team Hummingbirds](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/ACE/Hummingbirds+Home) in the DE (Development Environment) department of PDU OSS.

#### Email
Guardians for this project can be reached at [vibranium_team](mailto:PDLVIBRANI@pdl.internal.ericsson.com)

## Maven Dependencies
The dependencies for CARDQ are listed in the relevant `pom.xml` file. The following Maven dependencies are some of the main ones used by this microservice:
  - Spring Boot Start Parent version 2.5.12.
  - Spring Boot Starter Web.
  - Spring Boot Actuator.
  - Spring Cloud Sleuth.
  - Spring Boot Starter Test.
  - Spring Cloud Contract Spec.
  - JaCoCo Code Coverage Plugin.
  - Sonar Maven Plugin.
  - Common Logging utility for logback created by Vortex team.
  - Properties for spring cloud version and java are as follows.
  
```
<version.spring-cloud>2020.0.3</version.spring-cloud>
<version.openjdk>11</version.openjdk>
```
## Build and run CARDQ locally
#### **Local set up instructions**
1. Follow the instructions listed on [IDE setup guide for ADP-compliant code style](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/IDUN/IDE+setup+guide+for+ADP-compliant+code+style) and [Common OSS Artifactory Repositories](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/pages/viewpage.action?spaceKey=DGBase&title=Common+OSS+Artifactory+Repositories) to set up your local development environment.
2. Ensure that Java 11 is installed.

#### **Run CARDQ using Stubrunner**

One of the main advantages of having stubrunner in place is to reduce the amount of manual data creation when writing our test cases. To configure the stubrunner you may check the `application-test.yaml` file located in `resources` directory of the main project. The following pattern is used in `ids` field of the stubrunner section to specify what JAR file to be executed.

    ```
    groupid:artifactid:version:classifier:port
    ```
  - The `groupid` would be the groupid specified in `pom.xml` of the project.
  - The `artifactid` would be the artifactid specified in `pom.xml` of the project.
  - The latest version of the stubs will be used if `version` is not specified or in case of having `+` instead. The following lines are all considered valid.
      ```
      com.ericsson.oss.air:eric-oss-core-reporting-dimension-query-cts-stubs::stubs:10101
      com.ericsson.oss.air:eric-oss-core-reporting-dimension-query-cts-stubs:+:stubs:10101
      com.ericsson.oss.air:eric-oss-core-reporting-dimension-query-cts-stubs:1.44.0-SNAPSHOT:stubs:10101
      ```
  - We use `stubs` as the default classifier. However you can change it to something else if needed.
  - The `port`, which is the port of the stubrunner server, should match the port you have specified in the `url` of your cts server.

Note that the `stub-mode` in the `application-test.yaml` specifies where to look for the JAR file. Setting it to `local` means that the JAR file is expected to be found in your local maven .m2 repository.

By running `CtsRestServiceTest.java` class the stubrunner configuration is read and when its profile is loaded, it will start the stubrunner on the specified port and will execute the tests. For more information about basic stubrunner configuration please have a loot at the official doc for [Spring Cloud Contract Stub Runner](https://cloud.spring.io/spring-cloud-contract/reference/html/project-features.html#features-stub-runner)

## Build related artifacts
The main build tool is BOB provided by ADP. For convenience, maven wrapper is provided to allow the developer to build in an isolated workstation that does not have access to ADP.
  - [ruleset2.0.yaml](ruleset2.0.yaml) - for more details on BOB please see [Bob 2.0 User Guide](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/adp-cicd/bob/+/refs/heads/master/USER_GUIDE_2.0.md). 
     You can also see an example of Bob usage in a Maven project in [BOB](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Adopting+BOB+Into+the+MVP+Project).
  - [precoderview.Jenkinsfile](precodereview.Jenkinsfile) - for pre code review Jenkins pipeline that runs when patch set is pushed.
  - [publish.Jenkinsfile](publish.Jenkinsfile) - for publish Jenkins pipeline that runs after patch set is merged to master.
  - [.bob.env](.bob.env) - if you are running Bob for the first time this file will not be available on your machine. 
    For more details on how to set it up please see [Bob 2.0 User Guide](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/adp-cicd/bob/+/refs/heads/master/USER_GUIDE_2.0.md). 

If the developer wishes to manually build the application in the local workstation, the ```bob clean init-dev build image package-local``` command can be used once BOB is configured in the workstation.  
Note: The ```mvn clean install``` command will be required before running the bob command above.  
See the "Containerization and Deployment to Kubernetes cluster" section for more details on deploying the built application.

Stub jar files are necessary to allow contract tests to run. The stub jars are stored in JFrog (Artifactory).
To allow the contract test to access and retrieve the stub jars, the .bob.env file must be configured as follows.

```
SELI_ARTIFACTORY_REPO_USER=<LAN user id>
SELI_ARTIFACTORY_REPO_PASS=<JFrog encripted LAN PWD or API key>
HOME=<path containing .m2, e.g. /c/Users/<user>/>
```
To retrieve an encrypted LAN password or API key, login to [JFrog](https://arm.seli.gic.ericsson.se) and select "Edit Profile". 
For info in setting the .bob.env file see [Bob 2.0 User Guide](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/adp-cicd/bob/+/refs/heads/master/USER_GUIDE_2.0.md).

## Containerization and Deployment to Kubernetes cluster.
Following artifacts contains information related to building a container and enabling deployment to a Kubernetes cluster:
- [charts](charts/) folder - used by BOB to lint, package and upload helm chart to helm repository.
  -  Once the project is built in the local workstation using the ```bob clean init-dev build image package-local``` command, a packaged helm chart is available in the folder ```.bob/eric-oss-core-reporting-dimension-query-internal/``` folder. 
     This chart can be manually installed in Kubernetes using ```helm install``` command. [P.S. required only for Manual deployment from local workstation]
- [Dockerfile](Dockerfile) - used by Spotify dockerfile maven plugin to build docker image.
  - The base image for the chassis application is ```sles-jdk8``` available in ```armdocker.rnd.ericsson.se```.
  
#### Deploy CARDQ in local k8s cluster
1. Download and install [Rancher Desktop](https://docs.rancherdesktop.io/getting-started/installation) application.
2. Make sure you select `containerd` option in `General > Settings > Container Engine` tab.
3. Open a new terminal and set the Kubernetes context to `rancher-desktop`.
    ```
    kubectl config use-context rancher-desktop
    ```

4. Create namespace
    ```
    kubectl create namespace cardq-ns
    ```

5. Login to ARM docker registry. Use your ECN/local machine password for login.
    ```
    nerdctl login armdocker.rnd.ericsson.se --username <SIGNUM>
    ```

6. Create a docker-registry secret that will be used for pulling the images.
    ```
    kubectl create secret docker-registry k8s-registry --docker-server=armdocker.rnd.ericsson.se --docker-username=<SIGNUM> --docker-password=<PASSWORD> --docker-email=<XXXXXXX@ericsson.com> --namespace=cardq-ns
    ```

7. Build the CARDQ project in bob VM.
    ```
    cd ~/gerrit/eric-oss-core-reporting-dimension-query
    bob clean init-dev build image package-local
    ```

8. Copy the CARDQ docker image built from bob VM to local environment.
    ```
    # Save docker image as tar file. This command needs to be run inside bob VM.
    docker save armdocker.rnd.ericsson.se/proj-eric-oss-dev/eric-oss-core-reporting-dimension-query > cardq-image.tar
    # Load the saved docker image to local environment. This command needs to be run outside bob VM (in local env).
    nerdctl --namespace=k8s.io load --input cardq-image.tar
    ```

9. Install CARDQ
    ```
    # Untar the packaged helm chart in your project's .bob/eric-oss-core-reporting-dimension-query-internal/ folder.
    tar -xvf eric-oss-core-reporting-dimension-query.tar
    cd eric-oss-core-reporting-dimension-query
    # Helm install
    helm install cardq -n cardq-ns --set appArmorProfile.type=unconfined,global.pullSecret=k8s-registry .
    ```
    
#### Custom CARDQ Metrics
CARDQ exposes the following custom metric via prometheus:
* **cardq.augmentation.response:** reports the number of seconds it takes for CARDQ to respond to the received query. 
    * The metric is reported in histogram buckets.
    * `response_type` is the tag associated with this metric and it may have `cached` or `uncached` values.
* **cardq.augmentation.cached.count:** reports the size of the cached CTS responses.

## Source
The [src](eric-oss-core-reporting-dimension-query-main/src/) folder of `eric-oss-core-reporting-dimension-query-main` contains a core spring boot application, a controller for health check and an interceptor for helping with logging details like user name. CARDQ uses submodules to separate stubs from the microservice's code. The folder also contains corresponding java unit tests.


## Setting up CI Pipeline
-  Docker Registry is used to store and pull Docker images. At Ericsson official chart repository is maintained at the org-level JFrog Artifactory. 
   Follow the link to set up a [Docker registry](https://confluence.lmera.ericsson.se/pages/viewpage.action?spaceKey=ACD&title=How+to+create+new+docker+repository+in+ARM+artifactory).
-  Helm repo is a location where packaged charts can be stored and shared. The official chart repository is maintained at the org-level JFrog Artifactory. 
   Follow the link to set up a [Helm repo](https://confluence.lmera.ericsson.se/display/ACD/How+to+setup+Helm+repositories+for+ADP+e2e+CICD).
-  Follow instructions at [Jenkins Pipeline setup](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-JenkinsPipelinesetup)
   to use out-of-box Jenkinsfiles which comes along with eric-oss-core-reporting-dimension-query.
-  Jenkins Setup involves master and agent machines. If there is not any Jenkins master setup, follow instructions at [Jenkins Master](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-JenkinsMaster-2.89.2(FEMJenkins)) - 2.89.2 (FEM Jenkins).
-  Request a node from the GIC (Note: RHEL 7 GridEngine Nodes have been successfully tested).
   [Request Node](https://estart.internal.ericsson.com/).
-  To setup [Jenkins Agent](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-Prerequisites) 
   for Jenkins, jobs execution follow the instructions at Jenkins Agent Setup.
-  The provided ruleset is designed to work in standard environments, but in case you need, you can fine tune the automatically generated ruleset to adapt to your project needs. 
   Take a look at [Bob 2.0 User Guide](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/adp-cicd/bob/+/refs/heads/master/USER_GUIDE_2.0.md) for details about ruleset configuration.
    
   [Gerrit Repos](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Design+and+Development+Environment)  
   [BOB](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Adopting+BOB+Into+the+MVP+Project)  
   [Bob 2.0 User Guide](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/adp-cicd/bob/+/refs/heads/master/USER_GUIDE_2.0.md)  
   [Docker registry](https://confluence.lmera.ericsson.se/pages/viewpage.action?spaceKey=ACD&title=How+to+create+new+docker+repository+in+ARM+artifactory)  
   [Helm repo](https://confluence.lmera.ericsson.se/display/ACD/How+to+setup+Helm+repositories+for+ADP+e2e+CICD)  
   [Jenkins Master](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-JenkinsMaster-2.89.2(FEMJenkins))  
   [Jenkins Agent](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-Prerequisites)  
   [Jenkins Pipeline setup](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-JenkinsPipelinesetup)  
   [EO Common Logging](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/ESO/EO+Common+Logging+Library)  
   [SLF4J](https://logging.apache.org/log4j/2.x/log4j-slf4j-impl/index.html)  
   [JFrog](https://arm.seli.gic.ericsson.se)  
   [Request Node](https://estart.internal.ericsson.com/)

## Using the Helm Repo API Token
The Helm Repo API Token is usually set using credentials on a given Jenkins FEM.
If the project you are developing is part of IDUN/Aeonic this will be pre-configured for you.
However, if you are developing an independent project please refer to the 'Helm Repo' section:
[Microservice providing Slice related aggregation fields to the Assurance Augmentation service to augment PCC and PCG PM counters.  CI Pipeline Guide](https://confluence-oss.seli.wh.rnd.internal.ericsson.com/display/PCNCG/Microservice+Chassis+CI+Pipeline+Start+Guide#MicroserviceChassisCIPipelineStartGuide-HelmRepo)

Once the Helm Repo API Token is made available via the Jenkins job credentials the precodereview and publish Jenkins jobs will accept the credentials (ex. HELM_SELI_REPO_API_TOKEN' or 'HELM_SERO_REPO_API_TOKEN) and create a variable HELM_REPO_API_TOKEN which is then used by the other files.

Credentials refers to a user or a functional user. This user may have access to multiple Helm repos.
In the event where you want to change to a different Helm repo, that requires a different access rights, you will need to update the set credentials.

## Artifactory Set-up Explanation
The Microservice providing Slice related aggregation fields to the Assurance Augmentation service to augment PCC and PCG PM counters.  Artifactory repos (dev, ci-internal and drop) are set up following the ADP principles: [ADP Repository Principles](https://confluence.lmera.ericsson.se/pages/viewpage.action?spaceKey=AA&title=2+Repositories)

The commands: "bob init-dev build image package" will ensure that you are pushing a Docker image to:
[Docker registry - Dev](https://arm.seli.gic.ericsson.se/artifactory/docker-v2-global-local/proj-eric-oss-dev/)

The Precodereview Jenkins job pushes a Docker image to:
[Docker registry - CI Internal](https://arm.seli.gic.ericsson.se/artifactory/docker-v2-global-local/proj-eric-oss-ci-internal/)

This is intended behaviour which mimics the behavior of the Publish Jenkins job.
This job presents what will happen when the real microservice image is being pushed to the drop repository.
Furthermore, the 'Helm Install' stage needs a Docker image which has been previously uploaded to a remote repository, hence why making a push to the CI Internal is necessary.

The Publish job also pushes to the CI-Internal repository, however the Publish stage promotes the Docker image and Helm chart to the drop repo:
[Docker registry - Drop](https://arm.seli.gic.ericsson.se/artifactory/docker-v2-global-local/proj-eric-oss-drop/)

Similarly, the Helm chart is being pushed to three separate repositories:
[Helm registry - Dev](https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-dev-helm/)

The Precodereview Jenkins job pushes the Helm chart to:
[Helm registry - CI Internal](https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-ci-internal-helm/)

This is intended behaviour which mimics the behavior of the Publish Jenkins job.
This job presents what will happen when the real Helm chart is being pushed to the drop repository.
The Publish Jenkins job pushes the Helm chart to:
[Helm registry - Drop](https://arm.seli.gic.ericsson.se/artifactory/proj-eric-oss-drop-helm/)
