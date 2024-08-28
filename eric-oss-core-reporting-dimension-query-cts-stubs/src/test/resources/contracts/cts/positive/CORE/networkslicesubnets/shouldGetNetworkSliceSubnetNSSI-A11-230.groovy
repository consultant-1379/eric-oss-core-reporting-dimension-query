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
package contracts.cts.positive.CORE.networkslicesubnets;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of getting network slice subnet

given:
  client requests to get network slice subnet
when:
  a valid request is submitted
then:
  request accepted and returns network slice subnet

""")
    request {
        method 'GET'
        urlPath('/oss-core-ws/rest/ctw/networkslicesubnet/230') {
            queryParameters {
                parameter('fs.networkSlice', '')
                parameter('fs.supportingNetworkSliceSubnets', '')
                parameter('fs.requirementSliceProfiles', '')
                parameter('fs.requirementSliceProfiles.plmnInfoList', '')
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
        body(file("networkslicesubnet-NSSI-A11-230.json"))
    }
    priority(10)
}
