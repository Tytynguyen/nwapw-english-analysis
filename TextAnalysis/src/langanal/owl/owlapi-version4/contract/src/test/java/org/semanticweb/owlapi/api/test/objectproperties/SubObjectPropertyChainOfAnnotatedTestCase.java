/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.api.test.objectproperties;

import static org.junit.Assert.assertNotEquals;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.api.test.baseclasses.AbstractAnnotatedAxiomRoundTrippingTestCase;
import org.semanticweb.owlapi.model.*;

import com.google.common.collect.Lists;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 */
@SuppressWarnings({ "javadoc" })
public class SubObjectPropertyChainOfAnnotatedTestCase extends AbstractAnnotatedAxiomRoundTrippingTestCase {

    @Override
    protected OWLAxiom getMainAxiom(Set<OWLAnnotation> annos) {
        List<OWLObjectProperty> props = Arrays.asList(ObjectProperty(iri("p")), ObjectProperty(iri("q")));
        return SubPropertyChainOf(props, ObjectProperty(iri("r")), annos);
    }

    @Test
    public void testCompareRoleChains() {
        OWLObjectPropertyExpression p = df.getOWLObjectProperty(IRI.create("_:", "p"));
        OWLObjectPropertyExpression q = df.getOWLObjectProperty(IRI.create("_:", "q"));
        OWLObjectPropertyExpression r = df.getOWLObjectProperty(IRI.create("_:", "r"));
        OWLSubPropertyChainOfAxiom ax1 = df.getOWLSubPropertyChainOfAxiom(Lists.newArrayList(p, q), r);
        OWLSubPropertyChainOfAxiom ax2 = df.getOWLSubPropertyChainOfAxiom(Lists.newArrayList(p, p), r);
        assertNotEquals("role chains should not be equal", ax1, ax2);
        int comparisonResult = ax1.compareTo(ax2);
        assertNotEquals("role chain comparision:\n " + ax1 + " should not compare to\n " + ax2 + " as 0\n", 0,
            comparisonResult);
    }
}
