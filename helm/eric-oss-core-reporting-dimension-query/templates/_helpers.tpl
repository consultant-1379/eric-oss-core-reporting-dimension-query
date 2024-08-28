{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "eric-oss-core-reporting-dimension-query.name" }}
  {{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart version as used by the chart label.
*/}}
{{- define "eric-oss-core-reporting-dimension-query.version" }}
{{- printf "%s" .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Expand the name of the chart.
*/}}
{{- define "eric-oss-core-reporting-dimension-query.fullname" -}}
{{- if .Values.fullnameOverride -}}
  {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
  {{- $name := default .Chart.Name .Values.nameOverride -}}
  {{- printf "%s" $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "eric-oss-core-reporting-dimension-query.chart" }}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create image pull secrets for global (outside of scope)
*/}}
{{- define "eric-oss-core-reporting-dimension-query.pullSecret.global" -}}
{{- $pullSecret := "" -}}
{{- if .Values.global -}}
  {{- if .Values.global.pullSecret -}}
    {{- $pullSecret = .Values.global.pullSecret -}}
  {{- end -}}
  {{- end -}}
{{- print $pullSecret -}}
{{- end -}}

{{/*
Create image pull secret, service level parameter takes precedence
*/}}
{{- define "eric-oss-core-reporting-dimension-query.pullSecret" -}}
{{- $pullSecret := (include "eric-oss-core-reporting-dimension-query.pullSecret.global" . ) -}}
{{- if .Values.imageCredentials -}}
  {{- if .Values.imageCredentials.pullSecret -}}
    {{- $pullSecret = .Values.imageCredentials.pullSecret -}}
  {{- end -}}
{{- end -}}
{{- print $pullSecret -}}
{{- end -}}

{{- define "eric-oss-core-reporting-dimension-query.mainImagePath" -}}
    {{- $productInfo := fromYaml (.Files.Get "eric-product-info.yaml") -}}
    {{- $registryUrl := (index $productInfo "images" "eric-oss-core-reporting-dimension-query" "registry") -}}
    {{- $repoPath := (index $productInfo "images" "eric-oss-core-reporting-dimension-query" "repoPath") -}}
    {{- $name := (index $productInfo "images" "eric-oss-core-reporting-dimension-query" "name") -}}
    {{- $tag := (index $productInfo "images" "eric-oss-core-reporting-dimension-query" "tag") -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- if .Values.global.registry.url -}}
                {{- $registryUrl = .Values.global.registry.url -}}
            {{- end -}}
            {{- if not (kindIs "invalid" .Values.global.registry.repoPath) -}}
                {{- $repoPath = .Values.global.registry.repoPath -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- if .Values.imageCredentials -}}
        {{- if not (kindIs "invalid" .Values.imageCredentials.repoPath) -}}
            {{- $repoPath = .Values.imageCredentials.repoPath -}}
        {{- end -}}
        {{- if (index .Values "imageCredentials" "eric-oss-core-reporting-dimension-query") -}}
            {{- if (index .Values "imageCredentials" "eric-oss-core-reporting-dimension-query" "registry") -}}
                {{- if (index .Values "imageCredentials" "eric-oss-core-reporting-dimension-query" "registry" "url") -}}
                    {{- $registryUrl = (index .Values "imageCredentials" "eric-oss-core-reporting-dimension-query" "registry" "url") -}}
                {{- end -}}
            {{- end -}}
            {{- if not (kindIs "invalid" (index .Values "imageCredentials" "eric-oss-core-reporting-dimension-query" "repoPath")) -}}
                {{- $repoPath = (index .Values "imageCredentials" "eric-oss-core-reporting-dimension-query" "repoPath") -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- if $repoPath -}}
        {{- $repoPath = printf "%s/" $repoPath -}}
    {{- end -}}
    {{- printf "%s/%s%s:%s" $registryUrl $repoPath $name $tag -}}
{{- end -}}

{{/*
Timezone variable
*/}}
{{- define "eric-oss-core-reporting-dimension-query.timezone" }}
  {{- $timezone := "UTC" }}
  {{- if .Values.global }}
    {{- if .Values.global.timezone }}
      {{- $timezone = .Values.global.timezone }}
    {{- end }}
  {{- end }}
  {{- print $timezone | quote }}
{{- end -}}


{{/*----------------------------------- Labels----------------------------------*/}}

{{/*
Common labels
*/}}
{{- define "eric-oss-core-reporting-dimension-query.common-labels" }}
app.kubernetes.io/name: {{ include "eric-oss-core-reporting-dimension-query.name" . }}
helm.sh/chart: {{ include "eric-oss-core-reporting-dimension-query.chart" . }}
{{ include "eric-oss-core-reporting-dimension-query.selectorLabels" . }}
app.kubernetes.io/version: {{ include "eric-oss-core-reporting-dimension-query.version" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end -}}

{{/*
Create a user defined label (DR-D1121-068, DR-D1121-060)
*/}}
{{ define "eric-oss-core-reporting-dimension-query.config-labels" }}
  {{- $global := (.Values.global).labels -}}
  {{- $service := .Values.labels -}}
  {{- include "eric-oss-core-reporting-dimension-query.mergeLabels" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

{{/*
Merged labels for Default, which includes Standard and Config
*/}}
{{- define "eric-oss-core-reporting-dimension-query.labels" -}}
  {{- $common := include "eric-oss-core-reporting-dimension-query.common-labels" . | fromYaml -}}
  {{- $config := include "eric-oss-core-reporting-dimension-query.config-labels" . | fromYaml -}}
  {{- $istioLabels := "sidecar.istio.io/inject: \"false\"" | fromYaml -}}
  {{- include "eric-oss-core-reporting-dimension-query.mergeLabels" (dict "location" .Template.Name "sources" (list $common $config $istioLabels)) | trim }}
{{- end -}}

{{/*
Selector labels
*/}}
{{- define "eric-oss-core-reporting-dimension-query.selectorLabels" -}}
app.kubernetes.io/name: {{ include "eric-oss-core-reporting-dimension-query.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Merged labels with Log Streaming Method label
*/}}
{{- define "eric-oss-core-reporting-dimension-query.labels-and-streamingMethod" -}}
    {{- $labels := include "eric-oss-core-reporting-dimension-query.labels" . | fromYaml -}}
    {{- $streamingLabel := include "eric-oss-core-reporting-dimension-query.directStreamingLabel" . | fromYaml -}}
    {{- include "eric-oss-core-reporting-dimension-query.mergeLabels" (dict "location" .Template.Name "sources" (list $labels $streamingLabel)) | trim }}
{{- end -}}


{{/*
Define the label needed for reaching eric-log-transformer (DR-470222-010)
*/}}
{{- define "eric-oss-core-reporting-dimension-query.directStreamingLabel" -}}
    {{- $streamingMethod := (include "eric-oss-core-reporting-dimension-query.streamingMethod" .) -}}
    {{- if or (eq "direct" $streamingMethod) (eq "dual" $streamingMethod) }}
    logger-communication-type: "direct"
    {{- end -}}
{{- end -}}

{{/*----------------------------------- Annotations----------------------------------*/}}

{{/*
Create container level annotations
*/}}
{{- define "eric-oss-core-reporting-dimension-query.container-annotations" }}
{{- $appArmorValue := .Values.appArmorProfile.type -}}
    {{- if .Values.appArmorProfile -}}
        {{- if .Values.appArmorProfile.type -}}
            {{- if eq .Values.appArmorProfile.type "localhost" -}}
                {{- $appArmorValue = printf "%s/%s" .Values.appArmorProfile.type .Values.appArmorProfile.localhostProfile }}
            {{- end}}
container.apparmor.security.beta.kubernetes.io/eric-oss-core-reporting-dimension-query: {{ $appArmorValue | quote }}
        {{- end}}
    {{- end}}
{{- end}}

{{/*
Annotations for Product Name and Product Number (DR-D1121-064).
*/}}
{{- define "eric-oss-core-reporting-dimension-query.product-info" }}
ericsson.com/product-name: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productName | quote }}
ericsson.com/product-number: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productNumber | quote }}
ericsson.com/product-revision: {{regexReplaceAll "(.*)[+|-].*" .Chart.Version "${1}" | quote }}
{{- end }}

{{/*
Create a user defined annotation (DR-D1121-065, DR-D1121-060)
*/}}
{{ define "eric-oss-core-reporting-dimension-query.config-annotations" }}
  {{- $global := (.Values.global).annotations -}}
  {{- $service := .Values.annotations -}}
  {{- include "eric-oss-core-reporting-dimension-query.mergeAnnotations" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

{{/*
Merged annotations for Default, which includes productInfo and config
*/}}
{{- define "eric-oss-core-reporting-dimension-query.annotations" -}}
  {{- $productInfo := include "eric-oss-core-reporting-dimension-query.product-info" . | fromYaml -}}
  {{- $config := include "eric-oss-core-reporting-dimension-query.config-annotations" . | fromYaml -}}
  {{- include "eric-oss-core-reporting-dimension-query.mergeAnnotations" (dict "location" .Template.Name "sources" (list $productInfo $config)) | trim }}
{{- end -}}

{{/*
Create prometheus info
*/}}
{{- define "eric-oss-core-reporting-dimension-query.prometheus" -}}
prometheus.io/path: {{ .Values.prometheus.path | quote }}
prometheus.io/port: {{ include "eric-oss-core-reporting-dimension-query.service-port" . | quote }}
prometheus.io/scrape: {{ .Values.prometheus.scrape | quote }}
{{- end -}}

{{/*
Merged annotations with Prometheus
*/}}
{{- define "eric-oss-core-reporting-dimension-query.annotations-and-prometheus" -}}
  {{- $prometheus := include "eric-oss-core-reporting-dimension-query.prometheus" . | fromYaml -}}
  {{- $annotations := include "eric-oss-core-reporting-dimension-query.annotations" . | fromYaml -}}
  {{- include "eric-oss-core-reporting-dimension-query.mergeAnnotations" (dict "location" .Template.Name "sources" (list $prometheus $annotations)) | trim }}
{{- end -}}

{{/*
Merged annotations with Prometheus and Container annotations
*/}}
{{- define "eric-oss-core-reporting-dimension-query.annotations-and-prometheus-with-container" -}}
  {{- $annotations := include "eric-oss-core-reporting-dimension-query.annotations-and-prometheus" . | fromYaml -}}
  {{- $container := include "eric-oss-core-reporting-dimension-query.container-annotations" . | fromYaml -}}
  {{- include "eric-oss-core-reporting-dimension-query.mergeAnnotations" (dict "location" .Template.Name "sources" (list $annotations $container)) | trim }}
{{- end -}}

{{/*
Define the annotations for security policy
*/}}
{{- define "eric-oss-core-reporting-dimension-query.securityPolicy.annotations" -}}
# Automatically generated annotations for documentation purposes.
{{- end -}}

{{/*
Return the fsgroup set via global parameter if it's set, otherwise 10000
*/}}
{{- define "eric-oss-core-reporting-dimension-query.fsGroup.coordinated" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.fsGroup -}}
      {{- if .Values.global.fsGroup.manual -}}
        {{ .Values.global.fsGroup.manual }}
      {{- else -}}
        {{- if eq .Values.global.fsGroup.namespace true -}}
          # The 'default' defined in the Security Policy will be used.
        {{- else -}}
          10000
      {{- end -}}
    {{- end -}}
  {{- else -}}
    10000
  {{- end -}}
  {{- else -}}
    10000
  {{- end -}}
{{- end -}}


{{/*
Create the name of the service account to use
*/}}
{{- define "eric-oss-core-reporting-dimension-query.serviceAccountName" -}}
  {{- if .Values.serviceAccount.create }}
    {{- default (include "eric-oss-core-reporting-dimension-query.fullname" .) .Values.serviceAccount.name }}
  {{- else }}
    {{- default "default" .Values.serviceAccount.name }}
  {{- end }}
{{- end }}

{{/*
Seccomp profile section (DR-1123-128)
*/}}
{{- define "eric-oss-core-reporting-dimension-query.seccomp-profile" }}
    {{- if .Values.seccompProfile }}
      {{- if .Values.seccompProfile.type }}
          {{- if eq .Values.seccompProfile.type "Localhost" }}
              {{- if .Values.seccompProfile.localhostProfile }}
seccompProfile:
  type: {{ .Values.seccompProfile.type }}
  localhostProfile: {{ .Values.seccompProfile.localhostProfile }}
            {{- end }}
          {{- else }}
seccompProfile:
  type: {{ .Values.seccompProfile.type }}
          {{- end }}
        {{- end }}
      {{- end }}
{{- end }}

{{/*
Define the role reference for security policy
*/}}
{{- define "eric-oss-core-reporting-dimension-query.securityPolicy.reference" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.security -}}
      {{- if .Values.global.security.policyReferenceMap -}}
        {{ $mapped := index .Values "global" "security" "policyReferenceMap" "default-restricted-security-policy" }}
        {{- if $mapped -}}
          {{ $mapped }}
        {{- else -}}
          default-restricted-security-policy
        {{- end -}}
      {{- else -}}
        default-restricted-security-policy
      {{- end -}}
    {{- else -}}
      default-restricted-security-policy
    {{- end -}}
  {{- else -}}
    default-restricted-security-policy
  {{- end -}}
{{- end -}}


{{/*
Define Pod Disruption Budget value taking into account its type (int or string)
*/}}
{{- define "eric-oss-core-reporting-dimension-query.pod-disruption-budget" -}}
  {{- if kindIs "string" .Values.podDisruptionBudget.minAvailable -}}
    {{- print .Values.podDisruptionBudget.minAvailable | quote -}}
  {{- else -}}
    {{- print .Values.podDisruptionBudget.minAvailable | atoi -}}
  {{- end -}}
{{- end -}}

{{/*
Define upper limit for TerminationGracePeriodSeconds
*/}}
{{- define "eric-oss-core-reporting-dimension-query.terminationGracePeriodSeconds" -}}
{{- if .Values.terminationGracePeriodSeconds -}}
  {{- toYaml .Values.terminationGracePeriodSeconds -}}
{{- end -}}
{{- end -}}

{{/*
Define tolerations to comply to DR-D1120-060, DR-D1120-061-AD
*/}}
{{- define "eric-oss-core-reporting-dimension-query.tolerations" -}}
  {{- $global := fromYaml ( include "eric-oss-core-reporting-dimension-query.global" . ) }}
  {{- if $global.tolerations }}
      {{- $globalTolerations := $global.tolerations -}}
      {{- $serviceTolerations := list -}}
      {{- if .Values.tolerations -}}
        {{- if eq (typeOf .Values.tolerations) ("[]interface {}") -}}
          {{- $serviceTolerations = .Values.tolerations -}}
        {{- else if eq (typeOf .Values.tolerations) ("map[string]interface {}") -}}
          {{- $serviceTolerations = index .Values.tolerations .podbasename -}}
        {{- end -}}
      {{- end -}}
      {{- $result := list -}}
      {{- $nonMatchingItems := list -}}
      {{- $matchingItems := list -}}
      {{- range $globalItem := $globalTolerations -}}
        {{- $globalItemId := include "eric-oss-core-reporting-dimension-query.merge-tolerations.get-identifier" $globalItem -}}
        {{- range $serviceItem := $serviceTolerations -}}
          {{- $serviceItemId := include "eric-oss-core-reporting-dimension-query.merge-tolerations.get-identifier" $serviceItem -}}
          {{- if eq $serviceItemId $globalItemId -}}
            {{- $matchingItems = append $matchingItems $serviceItem -}}
          {{- end -}}
        {{- end -}}
      {{- end -}}
      {{- range $globalItem := $globalTolerations -}}
        {{- $globalItemId := include "eric-oss-core-reporting-dimension-query.merge-tolerations.get-identifier" $globalItem -}}
        {{- $matchCount := 0 -}}
        {{- range $matchItem := $matchingItems -}}
          {{- $matchItemId := include "eric-oss-core-reporting-dimension-query.merge-tolerations.get-identifier" $matchItem -}}
          {{- if eq $matchItemId $globalItemId -}}
            {{- $matchCount = add1 $matchCount -}}
          {{- end -}}
        {{- end -}}
        {{- if eq $matchCount 0 -}}
          {{- $nonMatchingItems = append $nonMatchingItems $globalItem -}}
        {{- end -}}
      {{- end -}}
      {{- range $serviceItem := $serviceTolerations -}}
        {{- $serviceItemId := include "eric-oss-core-reporting-dimension-query.merge-tolerations.get-identifier" $serviceItem -}}
        {{- $matchCount := 0 -}}
        {{- range $matchItem := $matchingItems -}}
          {{- $matchItemId := include "eric-oss-core-reporting-dimension-query.merge-tolerations.get-identifier" $matchItem -}}
          {{- if eq $matchItemId $serviceItemId -}}
            {{- $matchCount = add1 $matchCount -}}
          {{- end -}}
        {{- end -}}
        {{- if eq $matchCount 0 -}}
          {{- $nonMatchingItems = append $nonMatchingItems $serviceItem -}}
        {{- end -}}
      {{- end -}}
      {{- toYaml (concat $result $matchingItems $nonMatchingItems) -}}
  {{- else -}}
      {{- if .Values.tolerations -}}
        {{- if eq (typeOf .Values.tolerations) ("[]interface {}") -}}
          {{- toYaml .Values.tolerations -}}
        {{- else if eq (typeOf .Values.tolerations) ("map[string]interface {}") -}}
          {{- toYaml (index .Values.tolerations .podbasename) -}}
        {{- end -}}
      {{- end -}}
  {{- end -}}
{{- end -}}

{{/*
Helper function to get the identifier of a tolerations array element.
Assumes all keys except tolerationSeconds are used to uniquely identify
a tolerations array element.
*/}}
{{ define "eric-oss-core-reporting-dimension-query.merge-tolerations.get-identifier" }}
  {{- $keyValues := list -}}
  {{- range $key := (keys . | sortAlpha) -}}
    {{- if eq $key "effect" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- else if eq $key "key" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- else if eq $key "operator" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- else if eq $key "value" -}}
      {{- $keyValues = append $keyValues (printf "%s=%s" $key (index $ $key)) -}}
    {{- end -}}
  {{- end -}}
  {{- printf "%s" (join "," $keyValues) -}}
{{ end }}

{{/*
Create a merged set of nodeSelectors from global and service level.
*/}}
{{- define "eric-oss-core-reporting-dimension-query.nodeSelector" -}}
{{- $globalValue := (dict) -}}
{{- if .Values.global -}}
    {{- if .Values.global.nodeSelector -}}
      {{- $globalValue = .Values.global.nodeSelector -}}
    {{- end -}}
{{- end -}}
{{- if .Values.nodeSelector -}}
  {{- range $key, $localValue := .Values.nodeSelector -}}
    {{- if hasKey $globalValue $key -}}
         {{- $Value := index $globalValue $key -}}
         {{- if ne $Value $localValue -}}
           {{- printf "nodeSelector \"%s\" is specified in both global (%s: %s) and service level (%s: %s) with differing values which is not allowed." $key $key $globalValue $key $localValue | fail -}}
         {{- end -}}
     {{- end -}}
    {{- end -}}
    nodeSelector: {{- toYaml (merge $globalValue .Values.nodeSelector) | trim | nindent 2 -}}
{{- else -}}
  {{- if not ( empty $globalValue ) -}}
    nodeSelector: {{- toYaml $globalValue | trim | nindent 2 -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
    Define Image Pull Policy
*/}}
{{- define "eric-oss-core-reporting-dimension-query.registryImagePullPolicy" -}}
    {{- $globalRegistryPullPolicy := "IfNotPresent" -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- if .Values.global.registry.imagePullPolicy -}}
                {{- $globalRegistryPullPolicy = .Values.global.registry.imagePullPolicy -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- print $globalRegistryPullPolicy -}}
{{- end -}}

{/*
Define JVM heap size (DR-D1126-010 | DR-D1126-011)
*/}}
{{- define "eric-oss-core-reporting-dimension-query.jvmHeapSettings" -}}
    {{- $initRAM := "" -}}
    {{- $maxRAM := "" -}}
    {{/*
       ramLimit is set by default to 1.0, this is if the service is set to use anything less than M/Mi
       Rather than trying to cover each type of notation,
       if a user is using anything less than M/Mi then the assumption is its less than the cutoff of 1.3GB
       */}}
    {{- $ramLimit := 1.0 -}}
    {{- $ramComparison := 1.3 -}}

    {{- if not (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory") -}}
        {{- fail "memory limit for eric-oss-core-reporting-dimension-query is not specified" -}}
    {{- end -}}

    {{- if (hasSuffix "Gi" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory")) -}}
        {{- $ramLimit = trimSuffix "Gi" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory") | float64 -}}
    {{- else if (hasSuffix "G" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory")) -}}
        {{- $ramLimit = trimSuffix "G" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory") | float64 -}}
    {{- else if (hasSuffix "Mi" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory")) -}}
        {{- $ramLimit = (div (trimSuffix "Mi" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory") | float64) 1000) | float64  -}}
    {{- else if (hasSuffix "M" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory")) -}}
        {{- $ramLimit = (div (trimSuffix "M" (index .Values "resources" "eric-oss-core-reporting-dimension-query" "limits" "memory")| float64) 1000) | float64  -}}
    {{- end -}}


    {{- if (index .Values "resources" "eric-oss-core-reporting-dimension-query" "jvm") -}}
        {{- if (index .Values "resources" "eric-oss-core-reporting-dimension-query" "jvm" "initialMemoryAllocationPercentage") -}}
            {{- $initRAM = (index .Values "resources" "eric-oss-core-reporting-dimension-query" "jvm" "initialMemoryAllocationPercentage") | int -}}
            {{- $initRAM = printf "-XX:InitialRAMPercentage=%d" $initRAM -}}
        {{- else -}}
            {{- fail "initialMemoryAllocationPercentage not set" -}}
        {{- end -}}
        {{- if and (index .Values "resources" "eric-oss-core-reporting-dimension-query" "jvm" "smallMemoryAllocationMaxPercentage") (index .Values "resources" "eric-oss-core-reporting-dimension-query" "jvm" "largeMemoryAllocationMaxPercentage") -}}
            {{- if lt $ramLimit $ramComparison -}}
                {{- $maxRAM = (index .Values "resources" "eric-oss-core-reporting-dimension-query" "jvm" "smallMemoryAllocationMaxPercentage") | int -}}
                {{- $maxRAM = printf "-XX:MaxRAMPercentage=%d" $maxRAM -}}
            {{- else -}}
                {{- $maxRAM = (index .Values "resources" "eric-oss-core-reporting-dimension-query" "jvm" "largeMemoryAllocationMaxPercentage") | int -}}
                {{- $maxRAM = printf "-XX:MaxRAMPercentage=%d" $maxRAM -}}
            {{- end -}}
        {{- else -}}
            {{- fail "smallMemoryAllocationMaxPercentage | largeMemoryAllocationMaxPercentage not set" -}}
        {{- end -}}
    {{- else -}}
        {{- fail "jvm heap percentages are not set" -}}
    {{- end -}}
{{- printf "%s %s" $initRAM $maxRAM -}}
{{- end -}}

{{/*----------------------------------- ADP Logging----------------------------------*/}}

{{/*
Define the log streaming method (DR-470222-010)
*/}}
{{- define "eric-oss-core-reporting-dimension-query.streamingMethod" -}}
{{- $streamingMethod := "direct" -}}
{{- if .Values.global -}}
  {{- if .Values.global.log -}}
      {{- if .Values.global.log.streamingMethod -}}
        {{- $streamingMethod = .Values.global.log.streamingMethod }}
      {{- end -}}
  {{- end -}}
{{- end -}}
{{- if .Values.log -}}
  {{- if .Values.log.streamingMethod -}}
    {{- $streamingMethod = .Values.log.streamingMethod }}
  {{- end -}}
{{- end -}}
{{- print $streamingMethod -}}
{{- end -}}

{{/*
Define logging environment variables (DR-470222-010)
*/}}
{{ define "eric-oss-core-reporting-dimension-query.loggingEnv" }}
{{- $streamingMethod := (include "eric-oss-core-reporting-dimension-query.streamingMethod" .) -}}
{{- $logtls := include "eric-oss-core-reporting-dimension-query.log-tls-enabled" . | trim -}}

{{- if or (eq "direct" $streamingMethod) (eq "dual" $streamingMethod) -}}
  {{- if and (eq "direct" $streamingMethod) (eq "true" $logtls) }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-https.xml"
  {{- else if eq "direct" $streamingMethod }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-http.xml"
  {{- end }}
  {{- if and (eq "dual" $streamingMethod) (eq "true" $logtls) }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-dual-sec.xml"
  {{- else if eq "dual" $streamingMethod }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-dual.xml"
  {{- end }}
- name: LOGSTASH_DESTINATION
  value: eric-log-transformer
  {{- if eq "true" $logtls }}
- name: LOGSTASH_PORT
  value: "9443"
  {{- else }}
- name: LOGSTASH_PORT
  value: "9080"
  {{- end }}
- name: POD_NAME
  valueFrom:
    fieldRef:
      fieldPath: metadata.name
- name: POD_UID
  valueFrom:
    fieldRef:
      fieldPath: metadata.uid
- name: CONTAINER_NAME
  value: eric-oss-core-reporting-dimension-query
- name: NODE_NAME
  valueFrom:
    fieldRef:
      fieldPath: spec.nodeName
- name: NAMESPACE
  valueFrom:
    fieldRef:
      fieldPath: metadata.namespace
{{- else if eq $streamingMethod "indirect" }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-json.xml"
{{- else if eq $streamingMethod "plain" }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-plain-text.xml"
{{- else }}
  {{- fail ".log.streamingMethod unknown" }}
{{- end -}}
{{ end }}

{{/*----------------------------------- Security - TLS Options----------------------------------*/}}

{{/*
    Define Global Security enabled
*/}}
{{- define "eric-oss-core-reporting-dimension-query.global-security-tls-enabled" -}}
    {{- if  .Values.global -}}
      {{- if  .Values.global.security -}}
        {{- if  .Values.global.security.tls -}}
           {{- .Values.global.security.tls.enabled | toString -}}
        {{- else -}}
           {{- "false" -}}
        {{- end -}}
      {{- else -}}
           {{- "false" -}}
      {{- end -}}
    {{- else -}}
        {{- "false" -}}
    {{- end -}}
{{- end -}}

{{/*
    Define log Security enabled
*/}}
{{- define "eric-oss-core-reporting-dimension-query.log-tls-enabled" -}}
    {{- $logtls := include "eric-oss-core-reporting-dimension-query.global-security-tls-enabled" . | trim -}}

      {{- if  .Values.log -}}
        {{- if  .Values.log.tls -}}
         {{- $logtls = .Values.log.tls.enabled | toString -}}
        {{- end -}}
      {{- end -}}
          {{- print $logtls -}}
{{- end -}}

{{/*
    Define Service Port based on TLS
*/}}
{{- define "eric-oss-core-reporting-dimension-query.service-port" -}}
    {{- if  (eq (include "eric-oss-core-reporting-dimension-query.cardq-security-tls-enabled" .) "true") }}
        {{- .Values.service.tls.port -}}
    {{- else -}}
        {{- .Values.service.port -}}
    {{- end -}}
{{- end -}}

{{/*
    Define CARDQ Security enabled
*/}}
{{- define "eric-oss-core-reporting-dimension-query.cardq-security-tls-enabled" -}}
    {{- $tls := include "eric-oss-core-reporting-dimension-query.global-security-tls-enabled" . | trim -}}

    {{- if  .Values.security -}}
      {{- if  .Values.security.tls -}}
        {{- $tls = .Values.security.tls.enabled | toString -}}
      {{- end -}}
    {{- end -}}
    {{- print $tls -}}
{{- end -}}

{{/*
    Define TLS Certificate ID for APD Certificate Reloader library
*/}}
{{- define "eric-oss-core-reporting-dimension-query.cardq-security-tls-keystore-alias" -}}
    {{- $alias := "cardq" -}}
        {{- if  .Values.security -}}
            {{- if  .Values.security.keystore -}}
                {{- if  .Values.security.keystore.alias -}}
                    {{- $alias = .Values.security.keystore.alias -}}
                {{- end -}}
            {{- end -}}
        {{- end -}}
    {{- print $alias -}}
{{- end -}}

{{/*
    Define Liveness - Readiness probe command
*/}}
{{- define "eric-oss-core-reporting-dimension-query.probe-command" -}}
  {{- if  (eq (include "eric-oss-core-reporting-dimension-query.cardq-security-tls-enabled" .) "true") }}
  {{ $readPath := "$TLS_READ_ROOT_PATH/server/$KEYSTORE_REL_DIR/" }}
  {{ $certPath := print $readPath "srvcert.crt" }}
  {{ $keyPath := print $readPath "srvprivkey.pem" }}
    exec:
      command:
        - /bin/sh
        - -c
        - curl -k --cert {{ $certPath }} --key {{ $keyPath }} https://localhost:{{ .Values.service.tls.port }}/actuator/health
  {{- else }}
    httpGet:
      path: /actuator/health
      port: http
  {{- end }}
{{- end -}}

{{/*
    Define CTS endpoint based on the tls
*/}}
{{- define "eric-oss-core-reporting-dimension-query.cts.endpoint" -}}
 {{- $ctstls := include "eric-oss-core-reporting-dimension-query.global-security-tls-enabled" . | trim -}}
    {{- $protocol := "http" -}}
    {{- $host := printf "%s%s" "eric-oss-cmn-topology-svc-core." .Release.Namespace -}}
    {{- $port := "8080" -}}

    {{- if .Values.cts.tls -}}
        {{- $ctstls = .Values.cts.tls.enabled | toString -}}
    {{- end }}

    {{- if eq "true" $ctstls -}}
       {{- $protocol = "https" -}}
       {{- $port = "8443" -}}
    {{- end }}

    {{- if .Values.cts.url -}}
          {{- if .Values.cts.url.host -}}
             {{- $host = .Values.cts.url.host -}}
          {{- end }}
          {{- if .Values.cts.url.port -}}
             {{- $port = .Values.cts.url.port | toString -}}
          {{- end }}
     {{- end -}}

    {{- printf "%s://%s:%s" $protocol $host $port -}}
{{- end -}}

{{/*
    Define cts security enabled
*/}}
{{- define "eric-oss-core-reporting-dimension-query.cts-tls-enabled" -}}
    {{- $ctstls := include "eric-oss-core-reporting-dimension-query.global-security-tls-enabled" . | trim -}}

      {{- if  .Values.cts -}}
        {{- if  .Values.cts.tls -}}
         {{- $ctstls = .Values.cts.tls.enabled | toString -}}
        {{- end -}}
      {{- end -}}
          {{- print $ctstls -}}
{{- end -}}