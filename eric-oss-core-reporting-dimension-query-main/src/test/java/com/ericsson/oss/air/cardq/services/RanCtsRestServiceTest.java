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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.ericsson.oss.air.cardq.StubRunnerTestSetup;
import com.ericsson.oss.air.cardq.config.CtsConnectionConfiguration;


class RanCtsRestServiceTest extends StubRunnerTestSetup {
    @Autowired
    private RanCtsService ctsService;

    @SpyBean
    private CtsConnectionConfiguration ctsConnectionDetails;

    @Test
    void shouldGetNRCellWithRelatedExternalRef() {
        var nrcellList = ctsService.getNRCellByExternalRef(
                "SubNetwork=Europe,SubNetwork=Ireland,MeContext=gNodeB1,ManagedElement=gNodeB1,GNBDUFunction=1,NRCellDU=209");
        assertThat(nrcellList)
                .hasSize(1)
                .anyMatch(nrcell -> nrcell.getType().equals("ctw/nrcell"))
                .anyMatch(nrcell -> nrcell.getName().equals("NR01gNodeBRadio00013-3"))
                .anyMatch(nrcell -> nrcell.getTrackingAreaCode().equals("999"));
    }

    @Test
    void shouldGetNRCellWithNSI() {
        String externalRef = "SubNetwork=DO5G,MeContext=TD3B297342,ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=CBC_5G_NR_1A";
        var nrcellList = ctsService.getNRCellByExternalRef(externalRef);
        assertThat(nrcellList)
                .singleElement()
                .hasFieldOrPropertyWithValue("type", "ctw/nrcell")
                .hasFieldOrPropertyWithValue("name", "CBC_5G_NR_1A")
                .hasFieldOrPropertyWithValue("trackingAreaCode", "20");

        // check gNodeB-DU
        var nrcell = nrcellList.get(0);
        assertThat(nrcell.getGnbduList()).isNotEmpty();
        var gNodeBList = nrcell.getGnbduList();
        assertThat(gNodeBList)
                .singleElement()
                .hasFieldOrPropertyWithValue("type", "ctw/gnbdu");
        // check gNodeB-DU has 5QISet
        var fiveQiSetList = gNodeBList.get(0).getFiveQiSetList();
        assertThat(fiveQiSetList).isNotEmpty();
        // check 5QISet has ResourcePartition, QoSProfile and 5QIFlow
        assertThat(fiveQiSetList)
                .anySatisfy(fiveQiSet -> {
                    assertThat(fiveQiSet.getFiveQiFlows()).isNotEmpty();
                })
                .anySatisfy(fiveQiSet -> {
                    assertThat(fiveQiSet.getQosProfileList()).isNotEmpty();
                })
                // update stub data model
                .anySatisfy(fiveQiSet -> {
                    assertThat(fiveQiSet.getResourcePartitions()).isNotEmpty();
                });

        // check nrcell has PLMNInfo
        assertThat(nrcell.getPlmnInfoList()).isNotEmpty();

        // check plmninfo has SliceProfile and ServiceProfile
        var plmnInfoList = nrcell.getPlmnInfoList();
        assertThat(plmnInfoList)
                .anySatisfy(plmnInfo -> {
                    assertThat(plmnInfo.getSliceProfiles()).isNotEmpty();
                    // check SliceProfile has NSSI
                    var sliceProfile = plmnInfo.getSliceProfiles().get(0);
                    assertThat(sliceProfile.getNssListR()).isNotEmpty();
                })
                .anySatisfy(plmnInfo -> {
                    assertThat(plmnInfo.getServiceProfiles()).isNotEmpty();
                    // check ServiceProfile has NSI
                    var serviceProfile = plmnInfo.getServiceProfiles().get(0);
                    assertThat(serviceProfile.getNetworkSliceListC()).isNotEmpty();
                })
                .anySatisfy(plmnInfo -> {
                    assertThat(plmnInfo.getResourcePartitionMembers()).isNotEmpty();
                    // check ServiceProfile has NSI
                    assertThat(plmnInfo.getResourcePartitionMembers())
                        .anySatisfy(resourcePartitionMember -> {
                            assertThat(resourcePartitionMember.getResourcePartitionList()).isNotEmpty();
                            var resourcePartitionList = resourcePartitionMember.getResourcePartitionList();
                            assertThat(resourcePartitionList)
                                .anySatisfy(resourcePartition -> {
                                    assertThat(resourcePartition.getResourcePartitionSetList()).isNotEmpty();
                                });
                        });
                });
        // check that nrcell has NetworkSlice
        assertThat(nrcell.getNetworkSliceList()).isNotEmpty();
        assertThat(nrcell.getNetworkSliceList())
                .anySatisfy(networkSlice -> {
                    assertThat(networkSlice.getRequirementServiceProfiles()).isNotEmpty();
                });
        // check that nrcell has NetworkSliceSubnet
        assertThat(nrcell.getNssList()).isNotEmpty();
        assertThat(nrcell.getNssList())
                .anySatisfy(nss -> {
                    assertThat(nss.getRequirementSliceProfiles()).isNotEmpty();
                });
    }

    @Test
    void shouldGetNRCellWithRelatedExternalRefWithPlm() {
        var nrcellList = ctsService.getNRCellByExternalRef("ENMRAN/NRCellDU/NR01gNodeBRadio00013/NR01gNodeBRadio00013/1/NR01gNodeBRadio00013-PLM");
        assertThat(nrcellList)
                .hasSize(1)
                .anyMatch(nrcell -> nrcell.getType().equals("ctw/nrcell"))
                .anyMatch(nrcell -> nrcell.getName().equals("NR01gNodeBRadio00013-PLM"))
                .anyMatch(nrcell -> nrcell.getTrackingAreaCode().equals("999"));
        var plmInfoList = nrcellList.get(0).getPlmnInfoList();
        assertThat(plmInfoList)
                .hasSize(6)
                .allMatch(plminfo -> plminfo.getType().equals("ctw/plmninfo"));
    }

    @Test
    void shouldReturnEmptyNRCellList() {
        var networkSliceList = ctsService.getNRCellByExternalRef("SubNetwork=DO5G,MeContext=TD3B297342,"
            + "ManagedElement=TD3B297342,GNBDUFunction=1,NRCellDU=EMPTY");
        assertThat(networkSliceList).isEmpty();
    }

    @Test
    void shouldErrorWithInvalidCredentials() {
        doReturn("invalid").when(ctsConnectionDetails).getUser();
        doReturn("credentials").when(ctsConnectionDetails).getPassword();
        assertThatThrownBy(() -> ctsService.getNRCellByExternalRef("invalidCredentials"))
                .isInstanceOf(RuntimeException.class).hasMessageContaining("401");
    }

    @Test
    void shouldErrorWithInvalidDatabaseHeaders() {
        doReturn("invalid").when(ctsConnectionDetails).getDatabaseName();
        assertThatThrownBy(() -> ctsService.getNRCellByExternalRef("invalidDatabaseHeader")).isInstanceOf(RuntimeException.class)
                .hasMessageContaining("401");
    }

    @Test
    void shouldGetNetworkSliceSubnetsWithQoSProfilesByName() {
        var nssiList = ctsService.getNetworkSliceSubnetsWithQoSProfilesByName(
                "0505151510833_nst_invoke_1_allocate_ran_nssi_service_SC_ranService_SC_vod_QOS_nssi"
        );
        assertThat(nssiList)
                .hasSize(1)
                .allMatch(nssi -> nssi.getType().equals("ctw/networkslicesubnet"))
                .allMatch(nssi -> !nssi.getQosProfiles().isEmpty());
    }

    @Test
    void shouldGetQosProfilesWith5QISetsById() {
        var qosProfiles = ctsService.getQosProfilesWith5QISetsById("64");
        assertThat(qosProfiles)
                .hasSize(1)
                .allMatch(qosProfile -> qosProfile.getType().equals("ctw/qosprofile"))
                .allMatch(qosProfile -> qosProfile.getName().equals("vod_QOS"))
                .allMatch(qosProfile -> !qosProfile.getFiveQiSets().isEmpty());
    }
}