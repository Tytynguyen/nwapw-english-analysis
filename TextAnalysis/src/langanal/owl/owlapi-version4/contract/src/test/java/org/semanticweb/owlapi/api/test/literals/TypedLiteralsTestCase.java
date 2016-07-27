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
package org.semanticweb.owlapi.api.test.literals;

import static org.junit.Assert.*;
import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

import java.util.*;

import javax.annotation.Nonnull;

import org.junit.Test;
import org.semanticweb.owlapi.api.test.baseclasses.AbstractAxiomsRoundTrippingTestCase;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLLiteralReplacer;
import org.semanticweb.owlapi.util.OWLObjectTransformer;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 */
@SuppressWarnings("javadoc")
public class TypedLiteralsTestCase extends AbstractAxiomsRoundTrippingTestCase {

    OWLDataProperty prop = DataProperty(iri("p"));
    OWLNamedIndividual ind = NamedIndividual(iri("i"));

    @Nonnull
    @Override
    protected Set<? extends OWLAxiom> createAxioms() {
        Set<OWLAxiom> axioms = new HashSet<>();
        addAxiomForLiteral(Literal(3), axioms);
        addAxiomForLiteral(Literal(33.3), axioms);
        addAxiomForLiteral(Literal(true), axioms);
        addAxiomForLiteral(Literal(33.3f), axioms);
        addAxiomForLiteral(Literal("33.3"), axioms);
        return axioms;
    }

    private void addAxiomForLiteral(@Nonnull OWLLiteral lit, @Nonnull Set<OWLAxiom> axioms) {
        axioms.add(DataPropertyAssertion(prop, ind, lit));
    }

    @Test
    public void shouldReplaceLiterals() {
        OWLOntology o = getOnt();
        OWLLiteralReplacer replacer = new OWLLiteralReplacer(o.getOWLOntologyManager(), Collections.singleton(o));
        Map<OWLLiteral, OWLLiteral> replacements = new HashMap<>();
        replacements.put(Literal(true), Literal(false));
        replacements.put(Literal(3), Literal(4));
        List<OWLOntologyChange> results = replacer.changeLiterals(replacements);
        assertTrue(results.contains(new AddAxiom(o, DataPropertyAssertion(prop, ind, Literal(4)))));
        assertTrue(results.contains(new AddAxiom(o, DataPropertyAssertion(prop, ind, Literal(false)))));
        assertTrue(results.contains(new RemoveAxiom(o, DataPropertyAssertion(prop, ind, Literal(3)))));
        assertTrue(results.contains(new RemoveAxiom(o, DataPropertyAssertion(prop, ind, Literal(true)))));
        assertEquals(4, results.size());
    }

    @Test
    public void shouldReplaceLiteralsWithTransformer() {
        OWLOntology o = getOnt();
        final Map<OWLLiteral, OWLLiteral> replacements = new HashMap<>();
        replacements.put(Literal(true), Literal(false));
        replacements.put(Literal(3), Literal(4));
        OWLObjectTransformer<OWLLiteral> replacer = new OWLObjectTransformer<>(new Predicate<Object>() {

            @Override
            public boolean apply(Object input) {
                return true;
            }
        }, new Function<OWLLiteral, OWLLiteral>() {

            @Override
            public OWLLiteral apply(OWLLiteral input) {
                OWLLiteral l = replacements.get(input);
                if (l == null) {
                    return input;
                }
                return l;
            }
        }, df, OWLLiteral.class);
        List<OWLOntologyChange> results = replacer.change(o);
        assertTrue(results.contains(new AddAxiom(o, DataPropertyAssertion(prop, ind, Literal(4)))));
        assertTrue(results.contains(new AddAxiom(o, DataPropertyAssertion(prop, ind, Literal(false)))));
        assertTrue(results.contains(new RemoveAxiom(o, DataPropertyAssertion(prop, ind, Literal(3)))));
        assertTrue(results.contains(new RemoveAxiom(o, DataPropertyAssertion(prop, ind, Literal(true)))));
        assertEquals(4, results.size());
    }
}
