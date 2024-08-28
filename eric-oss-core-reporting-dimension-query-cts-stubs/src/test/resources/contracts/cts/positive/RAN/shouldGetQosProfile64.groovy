/*******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/
package contracts.cts.positive.RAN;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of getting one QoS Profile

given:
  client requests to send to get one QoS Profile
when:
  a valid request is submitted
then:
  request accepted and returns one QoS Profile

""")
    request {
        method 'GET'
        urlPath('/oss-core-ws/rest/ctw/qosprofile') {
            queryParameters {
                parameter('id', '64')
                parameter('fs.fiveQISets', 'dynAttrs')
                parameter('fs.fiveQISets.fiveQIFlows', 'dynAttrs')
            }
        }
        headers {
            header('GS-Database-Name', 'eai_install')
            header('GS-Database-Host-Name', 'localhost')
            header('Authorization', 'Basic c3lzYWRtOg==')
            contentType(applicationJson())
        }
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("qosprofile-64.json"))
    }
}
