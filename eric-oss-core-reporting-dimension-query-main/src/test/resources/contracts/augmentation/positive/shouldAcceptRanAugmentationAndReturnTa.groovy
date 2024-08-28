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
  The response with only tac values populated
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
				      "value": "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1"
				    },
				    {
				      "name": "measObjLdn",
				      "value": "ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209"
				    }
                  ],
                  "augmentationFields": [
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
        body("""
        	{
			    "fields": [
			        [
			            {
			                "name": "tac",
			                "value": "999"
			            }
			        ]
			    ]
			}
                """
		)
    }
    priority(20)
}