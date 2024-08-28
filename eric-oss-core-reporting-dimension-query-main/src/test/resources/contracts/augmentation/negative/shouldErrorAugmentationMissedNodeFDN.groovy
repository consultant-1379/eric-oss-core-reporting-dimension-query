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

Represents an error scenario of data to be augmented onto a record

```
given:
  client requests to send data to be on record
when:
  a request is submitted without nodeFDN QueryField parameter in the body
then:
  the request is rejected with 400 BAD REQUEST
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
                      "name": "snssai",
                      "value": "${value(consumer(anyNonEmptyString()))}"
                    }
                  ],
                  "augmentationFields": [
                    {
                      "name": "nsi"
                    },
                    {
                      "name": "nssi"
                    }
                  ]
                }
                """
        )
    }
    response {
        status BAD_REQUEST()
        headers {
            contentType(applicationJson())
        }
        body(file("response/error-response-missing-fdn.json"))
    }
    priority(16)
}
