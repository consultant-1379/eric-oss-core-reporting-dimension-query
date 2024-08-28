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
package contracts.cts.positive.CORE.wirelessnetworkfunctions;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of getting wireless network function

given:
  client requests to send to get wireless network function
when:
  a valid request is submitted
then:
  request accepted and returns wireless network function

""")
    request {
        method 'GET'
        urlPath('/oss-core-ws/rest/ctw/wirelessnetfunction') {
            queryParameters {
                parameter('ExternalRef::nodeFDN', 'MeContext=PCG00031,ManagedElement=PCG00031')
                parameter('fs.supportingNetSliceSubnets', '')
                parameter('fs.geographicSite', '')
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
        body(file("wirelessnetfunction-PCG00031-UPF.json"))
    }
}
