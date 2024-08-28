/*******************************************************************************
 * COPYRIGHT Ericsson 2021
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

package com.ericsson.oss.air.cardq.services;

import com.ericsson.oss.air.cardq.StubRunnerTestSetup;
import com.ericsson.oss.air.cardq.config.CtsConnectionConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

public class CoreCtsRestServiceTest extends StubRunnerTestSetup {
    @Autowired
    private CoreCtsService coreCtsService;

    @SpyBean
    private CtsConnectionConfiguration ctsConnectionDetails;

    @Test
    public void shouldGetWirelessNetworkFunctionsWithRelatedNodeFDN() {
        var wirelessNetworkFunctionList = coreCtsService.getWirelessNetworkFunctionByNodeFDN("MeContext=PCC00020,ManagedElement=PCC00020");

        assertThat(wirelessNetworkFunctionList)
                .hasSize(1)
                .anyMatch(virtualNetworkFunction -> virtualNetworkFunction.getType().equals("ctw/smf"))
                .anyMatch(virtualNetworkFunction -> virtualNetworkFunction.getName().equals("WirelessNetworkFunction:PCC00020-SMF"));
    }

    @Test
    public void shouldGetNSSIById() {
        var networkSliceSubnet = coreCtsService.getNetworkSliceSubnetById("230");

        assertEquals("ctw/networkslicesubnet", networkSliceSubnet.getType());
        assertEquals("NetworkSliceSubnet:NSSI-A11", networkSliceSubnet.getName());
    }

    @Test
    public void shouldGetNSIById() {
        var networkSlice = coreCtsService.getNetworkSliceById("224");

        assertThat(networkSlice.getId().equals(224));
        assertThat(networkSlice.getType().equals("ctw/networkslice"));
        assertThat(networkSlice.getName().equals("NetworkSlice:NSI-A"));
    }

    @Test
    public void shouldReturnEmptyWirelessNetFunctionListForNonExistingNodeFDN() {
        var wirelessNetworkFunctionList = coreCtsService.getWirelessNetworkFunctionByNodeFDN("MeContext=nonexistentWirelessNetFunctions," +
                "ManagedElement=nonexistentWirelessNetFunctions");
        assertThat(wirelessNetworkFunctionList).isEmpty();
    }

    @Test
    public void shouldNotReturnNetworkSliceSubnetForNonExistingId() {
        assertThatThrownBy(() -> coreCtsService.getNetworkSliceSubnetById("488"))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("404");
    }

    @Test
    public void shouldErrorWithInvalidNetworkSliceId() {
        assertThatThrownBy(() -> coreCtsService.getNetworkSliceById("1"))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("404");

    }

    @Test
    public void shouldErrorWithInvalidCredentialsWithNsiId() {
        doReturn("invalid").when(ctsConnectionDetails).getUser();
        doReturn("credentials").when(ctsConnectionDetails).getPassword();
        assertThatThrownBy(() -> coreCtsService.getNetworkSliceById("224"))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("401");
    }

    @Test
    public void shouldErrorWithInvalidDatabaseHeader() {
        doReturn("invalid").when(ctsConnectionDetails).getDatabaseName();
        assertThatThrownBy(() -> coreCtsService.getWirelessNetworkFunctionByNodeFDN("invalidDatabaseHeader")).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("401");
    }
}
