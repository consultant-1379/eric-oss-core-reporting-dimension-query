/***********************************************************************
 * Copyright (c) 2023 Telefonaktiebolaget LM Ericsson, Sweden.
 *
 *
 * All rights reserved.
 *
 * The Copyright to the computer program(s) herein is the property of
 *
 * Telefonaktiebolaget LM Ericsson, Sweden.
 *
 * The program(s) may be used and/or copied with the written permission
 *
 * from Telefonaktiebolaget LM Ericsson or in accordance with the terms
 *
 * and conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ***********************************************************************/
package contracts.cts.positive.CORE.networkslices;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of getting network slices

given:
  client requests to get network slice with no service profile
when:
  a valid request is submitted
then:
  request accepted and returns network slice with no service profile

""")
    request {
        method 'GET'
        urlPath('/oss-core-ws/rest/ctw/networkslice/402') {
            queryParameters {
                parameter('fs.requirementServiceProfiles', '')
                parameter('fs.requirementServiceProfiles.plmnInfoList', '')
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
        body(file("networkslice-no-service-profile.json"))
    }
}
