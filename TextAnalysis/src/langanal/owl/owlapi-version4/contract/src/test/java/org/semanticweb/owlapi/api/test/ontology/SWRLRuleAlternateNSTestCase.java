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
package org.semanticweb.owlapi.api.test.ontology;

import static org.semanticweb.owlapi.apibinding.OWLFunctionalSyntaxFactory.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.api.test.baseclasses.AbstractAxiomsRoundTrippingTestCase;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLVariable;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 */
public class SWRLRuleAlternateNSTestCase extends
        AbstractAxiomsRoundTrippingTestCase {

    @Nonnull
    @Override
    protected Set<? extends OWLAxiom> createAxioms() {
        Set<OWLAxiom> axioms = new HashSet<>();
        SWRLVariable varX = df.getSWRLVariable(IRI("http://www.owlapi#x"));
        SWRLVariable varY = df.getSWRLVariable(IRI("http://www.owlapi#y"));
        SWRLVariable varZ = df.getSWRLVariable(IRI("http://www.owlapi#z"));
        Set<SWRLAtom> body = new HashSet<>();
        body.add(df.getSWRLClassAtom(Class(iri("A")), varX));
        SWRLIndividualArgument indIArg = df
                .getSWRLIndividualArgument(NamedIndividual(iri("i")));
        SWRLIndividualArgument indJArg = df
                .getSWRLIndividualArgument(NamedIndividual(iri("j")));
        body.add(df.getSWRLClassAtom(Class(iri("D")), indIArg));
        body.add(df.getSWRLClassAtom(Class(iri("B")), varX));
        SWRLVariable varQ = df.getSWRLVariable(IRI("http://www.owlapi#q"));
        SWRLVariable varR = df.getSWRLVariable(IRI("http://www.owlapi#r"));
        body.add(df.getSWRLDataPropertyAtom(DataProperty(iri("d")), varX, varQ));
        OWLLiteral lit = Literal(33);
        SWRLLiteralArgument litArg = df.getSWRLLiteralArgument(lit);
        body.add(df.getSWRLDataPropertyAtom(DataProperty(iri("d")), varY,
                litArg));
        Set<SWRLAtom> head = new HashSet<>();
        head.add(df.getSWRLClassAtom(Class(iri("C")), varX));
        head.add(df.getSWRLObjectPropertyAtom(ObjectProperty(iri("p")), varY,
                varZ));
        head.add(df.getSWRLSameIndividualAtom(varX, varY));
        head.add(df.getSWRLSameIndividualAtom(indIArg, indJArg));
        head.add(df.getSWRLDifferentIndividualsAtom(varX, varZ));
        head.add(df.getSWRLDifferentIndividualsAtom(varX, varZ));
        head.add(df.getSWRLDifferentIndividualsAtom(indIArg, indJArg));
        OWLObjectSomeValuesFrom svf = ObjectSomeValuesFrom(
                ObjectProperty(iri("p")), Class(iri("A")));
        head.add(df.getSWRLClassAtom(svf, varX));
        List<SWRLDArgument> args = new ArrayList<>();
        args.add(varQ);
        args.add(varR);
        args.add(litArg);
        head.add(df
                .getSWRLBuiltInAtom(IRI("http://www.owlapi#myBuiltIn"), args));
        axioms.add(df.getSWRLRule(body, head));
        return axioms;
    }
}
