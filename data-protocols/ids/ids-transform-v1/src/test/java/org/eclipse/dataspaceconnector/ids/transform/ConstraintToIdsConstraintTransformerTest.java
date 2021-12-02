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

import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.iais.eis.util.RdfResource;
import org.easymock.EasyMock;
import org.eclipse.dataspaceconnector.ids.spi.IdsId;
import org.eclipse.dataspaceconnector.ids.spi.IdsType;
import org.eclipse.dataspaceconnector.ids.spi.transform.TransformerContext;
import org.eclipse.dataspaceconnector.policy.model.AtomicConstraint;
import org.eclipse.dataspaceconnector.policy.model.Constraint;
import org.eclipse.dataspaceconnector.policy.model.Expression;
import org.eclipse.dataspaceconnector.policy.model.Operator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class ConstraintToIdsConstraintTransformerTest {

    private static final URI CONSTRAINT_ID = URI.create("https://constraint.com");

    // subject
    private ConstraintToIdsConstraintTransformer transformer;

    // mocks
    private AtomicConstraint constraint;
    private TransformerContext context;

    @BeforeEach
    void setUp() {
        transformer = new ConstraintToIdsConstraintTransformer();
        constraint = EasyMock.createMock(AtomicConstraint.class);
        context = EasyMock.createMock(TransformerContext.class);
    }

    @Test
    void testThrowsNullPointerExceptionForAll() {
        EasyMock.replay(constraint, context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            //noinspection ConstantConditions
            transformer.transform(null, null);
        });
    }

    @Test
    void testThrowsNullPointerExceptionForContext() {
        EasyMock.replay(constraint, context);

        Assertions.assertThrows(NullPointerException.class, () -> {
            //noinspection ConstantConditions
            transformer.transform(constraint, null);
        });
    }

    @Test
    void testReturnsNull() {
        EasyMock.replay(constraint, context);

        var result = transformer.transform(null, context);

        Assertions.assertNull(result);
    }

    @Test
    void testNonAtomicConstraint() {
        // prepare
        Constraint nonAtomicConstraint = EasyMock.mock(Constraint.class);

        context.reportProblem(EasyMock.anyString());
        EasyMock.expectLastCall().once();

        // record
        EasyMock.replay(constraint, context);

        // invoke
        var result = transformer.transform(nonAtomicConstraint, context);

        // verify
        Assertions.assertNull(result);
    }

    @Test
    void testSuccessfulSimple() {
        // prepare
        LeftOperand leftOperand = LeftOperand.PURPOSE;
        RdfResource rightOperand = EasyMock.createMock(RdfResource.class);
        BinaryOperator binaryOperator = BinaryOperator.AFTER;

        Expression leftExpression = EasyMock.createMock(Expression.class);
        Expression rightExpression = EasyMock.createMock(Expression.class);
        Operator operator = Operator.EQ;

        EasyMock.expect(constraint.getOperator()).andReturn(operator);
        EasyMock.expect(constraint.getLeftExpression()).andReturn(leftExpression);
        EasyMock.expect(constraint.getRightExpression()).andReturn(rightExpression);

        var idsId = IdsId.Builder.newInstance().value(constraint.hashCode()).type(IdsType.CONSTRAINT).build();
        EasyMock.expect(context.transform(EasyMock.eq(idsId), EasyMock.eq(URI.class))).andReturn(CONSTRAINT_ID);
        EasyMock.expect(context.transform(EasyMock.eq(leftExpression), EasyMock.eq(LeftOperand.class))).andReturn(leftOperand);
        EasyMock.expect(context.transform(EasyMock.eq(rightExpression), EasyMock.eq(RdfResource.class))).andReturn(rightOperand);
        EasyMock.expect(context.transform(EasyMock.eq(operator), EasyMock.eq(BinaryOperator.class))).andReturn(binaryOperator);

        // record
        EasyMock.replay(constraint, context);

        // invoke
        var result = transformer.transform(constraint, context);

        // verify
        Assertions.assertNotNull(result);
        Assertions.assertEquals(CONSTRAINT_ID, result.getId());
        Assertions.assertEquals(leftOperand, result.getLeftOperand());
        Assertions.assertEquals(rightOperand, result.getRightOperand());
        Assertions.assertEquals(binaryOperator, result.getOperator());
    }

    @AfterEach
    void teardown() {
        EasyMock.verify(constraint, context);
    }
}
