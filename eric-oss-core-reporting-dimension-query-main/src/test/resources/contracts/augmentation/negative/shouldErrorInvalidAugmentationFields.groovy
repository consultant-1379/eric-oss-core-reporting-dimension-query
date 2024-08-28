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
  a request is submitted with invalid augmentationFields in the body
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
                          "name": "nodeFDN",
                          "value": "${value(consumer(anyNonEmptyString()))}"
                        },
                        {
                          "name": "snssai",
                          "value": "${value(consumer(anyNonEmptyString()))}"
                        }
                      ],
                    "augmentationFields": [
                        {
                            "name": "${value(consumer(anyNonEmptyString()), producer("invalid"))}"
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
        body("""
                {
                    "type": null,
                    "title": "Malformed Request, IllegalArgumentException",
                    "status": 400,
                    "detail": "Malformed Request, Reason: Invalid augmentationFields value, supported values are: [nsi, nssi, site, plmnId], but found: [${value(consumer(fromRequest().rawBody('\$.augmentationFields[0].name')), producer("invalid"))}]",
                    "instance": null
                }
                """
        )

    }
    priority(29)
}
