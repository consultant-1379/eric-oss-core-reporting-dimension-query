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
Represents a successful scenario of getting an NRCEll with related entities

given:
  client requests to get NRCell 9
when:
  a valid request is submitted
then:
  request accepted and returns NRCell list

""")

    request {
        method 'GET'
        urlPath('/oss-core-ws/rest/ctw/nrcell') {
            queryParameters {
                parameter('ExternalRef::nrCellCuFDN', 'SubNetwork=DO5G,MeContext=TD3B297342,' +
                        'ManagedElement=TD3B2973429,GNBDUFunction=1,NRCellCU=CBC_5G_NR_1A9')
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
        body(file("nrcell-9-2nsi-2nssi.json"))
    }
    priority 2
}
