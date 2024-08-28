/*******************************************************************************
 * COPYRIGHT Ericsson 2024
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
package contracts.augmentation.positive;

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of data to be augmented onto a record

```
given:
  client requests to send data to be on record
when:
  a valid request is submitted without snssai input field
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
                              "name": "nodeFDN",
                              "value": "MeContext=PCC00010,ManagedElement=PCC00010"
                            }
                          ],
                          "augmentationFields": [
                            {
                              "name": "nssi"
                            },
                            {
                              "name": "nsi"
                            },
                            {
                              "name": "site"
                            },
                            {
                              "name": "plmnId"
                            }
                          ]
                        }
                        """
        )
    }
    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("response/successful-response-without-snssai.json"))
    }
    priority(20)
}
