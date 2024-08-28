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
when:
  a GET query types request is submitted
then:
  The response will include the valid query types
```

""")
    request {
        method 'GET'
        url "/v1/augmentation-info/augmentation/query/types"
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body(file("response/successful-response-queryTypes.json"))
    }
    priority(10)
}
