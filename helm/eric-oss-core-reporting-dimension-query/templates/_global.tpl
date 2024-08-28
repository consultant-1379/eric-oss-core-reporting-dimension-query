{{/*
  Define chart global default values.
*/}}
{{- define "eric-oss-core-reporting-dimension-query.globalDefaultValues" -}}
nodeSelector: {}
pullSecret:
registry:
  url:
  repoPath:
  imagePullPolicy:
timezone: UTC
internalIPFamily:
security:
  tls:
    # Enables/Disables tls intra-cluster communication. Requires SIP-TLS.
    enabled: false
  policyBinding:
    create:
  policyReferenceMap:
    default-restricted-security-policy:
metrics:
  # PM Server deployment name
  serviceReference: eric-pm-server

networkPolicy:
  # Enable or disable Network Policies.
  enabled: false

# Tolerations allow the scheduler to schedule pods with matching taints. Service level toleration take precedence over global toleration.
tolerations: []
{{- end -}}


{{/*
  Merge all chart global values in a single object.

  It merges default values with global values
  from integration chart. Additional components
  can contribute with theirs own global default values.
*/}}
{{- define "eric-oss-core-reporting-dimension-query.global" -}}
  {{- $globalDefaults := fromYaml ( include "eric-oss-core-reporting-dimension-query.globalDefaultValues" . ) -}}

  {{- if .Values.global -}}
    {{- toYaml ( mergeOverwrite $globalDefaults .Values.global ) -}}
  {{- else -}}
    {{- toYaml ( $globalDefaults ) -}}
  {{- end -}}
{{- end -}}
