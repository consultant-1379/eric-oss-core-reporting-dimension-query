# CHANGELOG

This Changelog will list all changes and updates that are visible to users of this microservice. We propose a non-standard section called `Retracted` which include features which were introduced earlier but got pulled out before "releasing".

## [2](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/) - Ongoing

### Added
- [IDUN-81320](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-81320):
  Refactor OpenAPI spec to return arrays of name-value pairs as response
  [[15680228]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/a97e3fb194bae90e5146daffa06c4c80a6f817b5)
  - Changed the OpenAPI spec to return the arrays of unique sets of name-value pairs
  - Modified the CARDQ contracts to reflect the new response format
- [IDUN-80220](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-80220):
  Implement Certificate Changes using Adp Certificate Reload Library
  [[3202797]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/320279768c229fc597e4fabd05e7ef8f190a248b)
- [IDUN-74077](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-74077):
  Add Cardq log TLS override for global tls
  [[726ffc7]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/726ffc7748f2aad0908f9b6ba92c351c2f4bdcf3)
- [IDUN-80219](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-80219):
  Add Cardq Webserver TLS override for global tls
  [[5e84a29]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/5e84a297f9b992a14c606d5db8a7e19e5125e709)
- [IDUN-80221](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-80221):
  Update Java to version 17
  [[94db4d9]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/94db4d9564e9108b83a2123d53ea9df85a83c157)
- [IDUN-78023](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-78023):
  Implement webserver setup for TLS
  [[2d2d0b0]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/2d2d0b085530113093fede51dea705fba348593e)
- [IDUN-73018](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-73018):
  Add 'ran' to supported query types
  [[eeabcb6]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/eeabcb65964cbefdbb80f51b356634fb60f94a4a)
- [IDUN-61737](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-61737):
  Add queryType support to augmentation-info/augmentation to differentiate augmentation query types
  [[381179a]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/381179a6abdbc01f2ba8389ff88a0f092eeed746)
- [IDUN-72292](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-72292):
  Integrate ADP Logging Stream Configuration
  [[c4fc879]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/c4fc87943aadd93ed3edd7a15023dc1ffbc1ffee)
- [IDUN-73424](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-73424):
  Refactor CARDQ for preparation of RAN support
  [[b2da1cd]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/b2da1cd767b41e5949e7b3d20b9119d4e37819fa)
  - Fixed logging for all exceptions to be shown in both error and debug level
- [IDUN-72824](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-72824):
  Refactor OpenAPI spec to include GET query types end point
  [[55969bb]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/55969bb4975ee174961d92456cd4d3e2dcf0888f)
  - Supports only 'core'
- [IDUN-74072](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-74072):
  Update CTS Requests to be able to use HTTPS and add Cardq CTS TLS override for global tls
  [[24d57e3]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/24d57e3cf859bbaa81e532969b201975694559cc)

### Changed:
- [ESOA-2749](https://eteamproject.internal.ericsson.com/browse/ESOA-2749):
  HA Related updates
  [[267e2c23]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/3e3025c8a7be9ba390beeeb71663a3ba267e2c23)
  - Default replicaCount to 2
  - Default requests.memory and requests.cpu is the same as limits.memory and limits.cpu
  - Support graceful webserver shutdown with 30s timeout as default

### Fixed
- [ESOA-3681](https://eteamproject.internal.ericsson.com/browse/ESOA-3681): Populate full `uri` tag values in the metrics for http client requests. <br> 
  With Spring Boot 3 upgrade, client request metrics no longer have full URI tag for each request sent within the application.
  This solution provides options for adding URI tags whether partial or complete url as tags. 
  [[864907b]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/864907bd4e6ec1dd499bdeb178abd0be0a28552b)
- [IDUN-75666](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-75666):
  After [IDUN-73424](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-73424), a bug was introduced where the nodeFDN key was used to query CTS instead of the nodeFDN value.
  [[4ba1329]](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/4ba1329cca497d942b8057283912910865eadb20)

## [1](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/edc3ec46150ded75d906c4f2657fa2c624fd1f6f) - 2023.05.05

### Added

- [IDUN-42887](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42887):
  Add metric for showing the number of items in the cache
  [[0658fa5](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/0658fa59901aff4be4f6d8ae405912fc1d150c23)]
- [IDUN-51658](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-51658):
  Configured ADP logging format in helm chart
  [[73c7371](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/73c7371e46d4f84a695a92b1b4d52b3e574497e7)]
- [IDUN-42899](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42899)
  Added generic error handing and responds with HTTP 500 Internal Server Error
  [[3c76790](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/3c7679010bf4cc67b1a4a784c0ff985b67605422)]
- [IDUN-53478](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-53478):
  Implemented CTS retry mechanism
  [[5e90ec1](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/5e90ec1cdc70450128d48da7e807cf2487311813)]
  - Added request timeouts.
  - Added retry configuration in helm chart.
- [IDUN-42882](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42882):
  Added caching mechanism
  [[10b392d](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/10b392d92ea38025adacf5dd438f0984b56ff336)]
  - Added caching of CTS results for faster responses.
  - Added cache configuration in helm chart.
- [IDUN-49275](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-49725):
  Added CTS configuration settings to helm chart
  [[7160a19](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/7160a19a8f37f083c711ab105473ba86d5ba0622)]
- [IDUN-45096](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-45096),
  [IDUN-56055](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-56055):
  Setup connection to CTS and connection settings are configurable through helm chart
  [[cec1032](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/cec10322bee30ce8c6f224b5b5d03e16c713ee90)]
- [IDUN-42886](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42886):
  Added endpoint response time metrics
  [[63fe434](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/63fe4349a39b9d18ff68d5c8071179af72302dc0)]
- [IDUN-42916](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42916):
  Added endpoint error handling for request validation failure which responds with HTTP 400 Bad Request
  [[41150aa](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/41150aa420eece049700b31eb5b60b6e39da2fad)]
- [IDUN-42898](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42898):
  Added implementation of `/v1/augmentation-info` REST API
  [[88a60b1](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/88a60b16872c17d48cefc89dbdb5ddf6291689c7)]

### Changed

- [IDUN-56267](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-56267):
  Increased liveness probe initialDelaySeconds to 120 (doubled timeout) to accommodate for slower systems
  [[5e9c701](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/5e9c7011a56b36ab95c1887b41a74463986e7883)]
- [IDUN-51514](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-51514):
  Changed health check log to debug level instead of info level
  [[19fce08](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/19fce08a778cb3fc237fc352c80ba412e40a98f3)]
- NO-JIRA:
  Changed `MemoryAllocationPercentage` settings to comply with Helm Design Rules (DR-D1126-011 and DR-D1124-020)
  [[cada60a](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/cada60a72ecd6a2f51ff72e7cdb0ab73543908e5)]
- NO-JIRA:
  Changed `global.registry.repoPath` and `imageCredentials.repoPath` to comply with Helm Design Rule (DR-D1121-106)
  [[125449c](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/125449c7b444827c0c0df7936df9e74eb140f47f)]
- [IDUN-51081](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-51081):
  Changed CARDQ metric names to follow ADP Naming Guideline
  [[2ae87aa](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/2ae87aab072bc67cbb276ccf6d70c8df73ebdb5e)]
  - Changed endpoint response time metrics to use one metric name instead of two and use tags to differentiate.
- [IDUN-42812](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42812):
  Increased default K8 resource `requests` and `limits` (but reduced cpu limit)
  [[6ed7142](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/6ed7142e71fbe9048810157723574b4c1270e98d)]
- [IDUN-64673](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-64673):
  Changed Helm templates to comply with Helm Design Rule (DR-D1121-060)
  [[2e1369f](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/2e1369f231d8f93badfaa01de86b3a977e4043a5)]

### Retracted

- [IDUN-42884](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42884):
  Added counters to the augmentation endpoint
  [[42c1757](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/42c1757dfab34b3e582565a4813079eee3f6df57)]
- [IDUN-42900](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42900),
  [IDUN-42813](https://jira-oss.seli.wh.rnd.internal.ericsson.com/browse/IDUN-42813):
  CARDQ Example REST client and controller
  [[47adf11](https://gerrit-gamma.gic.ericsson.se/plugins/gitiles/OSS/com.ericsson.oss.air/eric-oss-core-reporting-dimension-query/+/47adf11f6cc0caaa0ff4a6bca099eb549df8a6d7)]
