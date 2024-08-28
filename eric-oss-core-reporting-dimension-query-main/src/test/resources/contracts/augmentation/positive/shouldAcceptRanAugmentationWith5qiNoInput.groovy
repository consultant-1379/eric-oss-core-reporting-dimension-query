/*******************************************************************************
 * COPYRIGHT Ericsson 2023
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
package contracts.augmentation.positive

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of data to be augmented onto a record

```
given:
  client requests to send data to be on record
when:
  a valid request is submitted with only tac augmentation field
then:
  Request data to be augmented onto a record
  The response with NSI, NSSI, tac values populated
```

""")
    request {
        method 'POST'
        url "/v1/augmentation-info/augmentation"
        headers {
            contentType(applicationJson())
            accept(applicationJson())
        }
        body("""
                {
                  "inputFields": [
                  {
                      "name": "localDn",
                      "value": "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342"
                    },
                    {
                      "name": "measObjLdn",
                      "value": "ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A"
                    }
                  ],
                  "augmentationFields": [
                    {
                       "name": "qos"
                    },
                    {
                       "name": "nsi"
                    },
                    {
                       "name": "nssi"
                    },
                    {
                      "name": "tac"
                    }
                  ],
                  "queryType":"ran"
                }
                """
        )
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("response/successful-ran-response-no-input-5qi.json"))
    }
    priority(10)
}