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

Contract.make {
    description("""
Represents an error scenario of getting resources from cts rest endpoint

given:
  client requests to get network slices from cts rest endpoints 
when:
  a valid request with invalid network slice id is submitted
then:
  request accepted and returns a 'not found' error

""")
    def excludedValues = ['224','225','226']

    // Create a regex pattern dynamically
    def excludedPattern = excludedValues.collect { "(?!${it})" }.join('|')
    request {
        method 'GET'
        urlPath(regex('/oss-core-ws/rest/ctw/networkslice/(?!$excludedPattern\\b)[+-]?\\d+')) {
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
        status NOT_FOUND()
        //Actual response contains more details
        body("Object ctw:networkSlice with given is not found")
    }
    priority(60)
}

