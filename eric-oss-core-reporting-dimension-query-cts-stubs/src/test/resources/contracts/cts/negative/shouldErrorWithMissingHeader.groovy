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
package contracts.cts.negative;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents an error scenario of getting resources from cts rest endpoint

given:
  client requests to get a resource from cts rest endpoints
when:
  a request is submitted with missing database headers
then:
  the request is rejected with 401 UNAUTHORIZED

""")
    request {
        method 'GET'
        url(regex('/oss-core-ws/rest/[a-z]*/(?i)[a-z0-9]*[\\S]+'))
        headers {
            contentType(applicationJson())
        }
    }
    response {
        status UNAUTHORIZED()
    }
    priority(100)
}
