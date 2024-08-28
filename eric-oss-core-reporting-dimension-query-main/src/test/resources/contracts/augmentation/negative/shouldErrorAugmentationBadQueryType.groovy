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
package contracts.augmentation.negative;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of data to be augmented onto a record

```
given:
  client requests to send data to be on record
when:
  a valid request is submitted
then:
  Request data to be augmented onto a record
  The response will include the augmentationFields with the values populated
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
                              "name": "nodeFDN_invalid",
                              "value": "MeContext=PCC00010,ManagedElement=PCC00010"
                            },
                            {
                              "name": "snssai",
                              "value": "1-1"
                            }
                          ],
                          "augmentationFields": [
                            {
                              "name": "nsi"
                            },
                            {
                              "name": "nssi"
                            }
                          ],
                          "queryType": "notcoreorran"
                        }
                        """
        )
    }

    response {
        status BAD_REQUEST()
        headers {
            contentType(applicationJson())
        }
        body(file("response/error-response-bad-query-type.json"))

    }
    priority(5)
}
