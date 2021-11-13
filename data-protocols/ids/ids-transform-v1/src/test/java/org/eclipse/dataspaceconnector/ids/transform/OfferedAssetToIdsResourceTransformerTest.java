/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.transform;

import de.fraunhofer.iais.eis.ContractOfferBuilder;
import de.fraunhofer.iais.eis.Representation;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.ids.spi.types.container.OfferedAsset;
import org.eclipse.dataspaceconnector.spi.types.domain.asset.Asset;
import org.eclipse.dataspaceconnector.spi.types.domain.contract.offer.ContractOffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;

class OfferedAssetToIdsResourceTransformerTest {

    private static final String RESOURCE_ID = "test_id";
    private static final URI RESOURCE_ID_URI = URI.create("urn:resource:1");

    // subject
    private OfferedAssetToIdsResourceTransformer assetToIdsResourceTransformer;

    // mocks
    private Asset asset;
    private ContractOffer contractOffer;
    private OfferedAsset assetAndPolicy;
    private TransformerContext context;

    @BeforeEach
    void setUp() {
        assetToIdsResourceTransformer = new OfferedAssetToIdsResourceTransformer();
        asset = EasyMock.createMock(Asset.class);
        contractOffer = EasyMock.createMock(ContractOffer.class);
        assetAndPolicy = new OfferedAsset(asset, Collections.singletonList(contractOffer));
        context = EasyMock.createMock(TransformerContext.class);
    }

    @Test
    void testThrowsNullPointerExceptionForAll() {
        EasyMock.replay(asset, context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            assetToIdsResourceTransformer.transform(null, null);
        });
    }

    @Test
    void testThrowsNullPointerExceptionForContext() {
        EasyMock.replay(asset, context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            assetToIdsResourceTransformer.transform(assetAndPolicy, null);
        });
    }

    @Test
    void testReturnsNull() {
        EasyMock.replay(asset, context);

        var result = assetToIdsResourceTransformer.transform(null, context);

        Assertions.assertNull(result);
    }

    @Test
    void testSuccessfulSimple() {
        // prepare
        EasyMock.expect(asset.getId()).andReturn(RESOURCE_ID);
        EasyMock.expect(asset.getProperties()).andReturn(Collections.emptyMap());

        var representation = new RepresentationBuilder().build();
        EasyMock.expect(context.transform(EasyMock.anyObject(Asset.class), EasyMock.eq(Representation.class))).andReturn(representation);
        EasyMock.expect(context.transform(EasyMock.anyObject(ContractOffer.class), EasyMock.eq(de.fraunhofer.iais.eis.ContractOffer.class))).andReturn(new ContractOfferBuilder().build());

        IdsId id = IdsId.Builder.newInstance().value(RESOURCE_ID).type(IdsType.RESOURCE).build();
        EasyMock.expect(context.transform(EasyMock.eq(id), EasyMock.eq(URI.class))).andReturn(RESOURCE_ID_URI);

        context.reportProblem(EasyMock.anyString());
        EasyMock.expectLastCall().atLeastOnce();

        // record
        EasyMock.replay(asset, context);

        // invoke
        var result = assetToIdsResourceTransformer.transform(assetAndPolicy, context);

        // verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(RESOURCE_ID_URI, result.getId());
        Assertions.assertEquals(1, result.getRepresentation().size());
        Assertions.assertEquals(representation, result.getRepresentation().get(0));
    }

}
