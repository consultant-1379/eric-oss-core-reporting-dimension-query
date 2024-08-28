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
package contracts.cts.negative.CORE;

import org.springframework.cloud.contract.spec.Contract

def excludedValues = ['227','228','229','230','231','232','233','234','235','236']
def excludedPattern = excludedValues.collect { "(?!${it})" }.join('|')

Contract.make {
    description("""
Represents an error scenario of getting resources from cts rest endpoint

given:
  client requests to get network slice subnet from cts rest endpoints
when:
  a valid request with invalid network slice subnet id is submitted
then:
  request accepted and returns a 'not found' error

""")
    request {
        method 'GET'
        urlPath(regex('/oss-core-ws/rest/ctw/networkslicesubnet/(?!$excludedPattern\\b)[+-]?\\d+')) {
            queryParameters {
                parameter('fs.networkSlice', '')
                parameter('fs.supportingNetworkSliceSubnets', '')
                parameter('fs.requirementSliceProfiles', '')
                parameter('fs.requirementSliceProfiles.plmnInfoList', '')
            }
            headers {
                header('GS-Database-Name', 'eai_install')
                header('GS-Database-Host-Name', 'localhost')
                header('Authorization', 'Basic c3lzYWRtOg==')
                contentType(applicationJson())
            }
        }
    }
    response {
        status NOT_FOUND()
        //Actual response contains more details
        body("Object ctw:networkSliceSubnet with given is not found")
    }
    priority(20)
}
