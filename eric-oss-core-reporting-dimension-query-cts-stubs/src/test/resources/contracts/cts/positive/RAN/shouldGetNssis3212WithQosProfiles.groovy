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
Represents a successful scenario of getting one network slice subnet by name

given:
  client requests to send to get one network slice subnet by name
when:
  a valid request is submitted
then:
  request accepted and returns network slice subnet list

""")
    request {
        method 'GET'
        urlPath('/oss-core-ws/rest/ctw/networkslicesubnet') {
            queryParameters {
                parameter('name', '0505151510833_nst_invoke_1_allocate_ran_nssi_service_SC_ranService_SC_vod_QOS_nssi')
                parameter('fs.qosProfiles', '')
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
        body(file("nssi-3212-with-qosprofiles.json"))
    }
}
