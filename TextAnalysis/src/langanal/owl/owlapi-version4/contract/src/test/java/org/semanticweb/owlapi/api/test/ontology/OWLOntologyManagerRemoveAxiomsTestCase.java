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

import org.junit.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

@SuppressWarnings("javadoc")
public class OWLOntologyManagerRemoveAxiomsTestCase extends TestBase {

    @Test
    public void testRemove() throws OWLOntologyCreationException {
        String premise = "Prefix(:=<http://example.org/>)\n"
                + "Prefix(xsd:=<http://www.w3.org/2001/XMLSchema#>)\n"
                + "Ontology(\n" + "  Declaration(NamedIndividual(:a))\n"
                + "  Declaration(DataProperty(:dp1))\n"
                + "  Declaration(DataProperty(:dp2))\n"
                + "  Declaration(Class(:A))\n"
                + "  DisjointDataProperties(:dp1 :dp2) \n"
                + "  DataPropertyAssertion(:dp1 :a \"10\"^^xsd:integer)\n"
                + "  SubClassOf(:A DataSomeValuesFrom(:dp2 \n"
                + "    DatatypeRestriction(xsd:integer \n"
                + "      xsd:minInclusive \"18\"^^xsd:integer \n"
                + "      xsd:maxInclusive \"18\"^^xsd:integer)\n" + "    )\n"
                + "  )\n" + "  ClassAssertion(:A :a)\n" + ')';
        OWLOntology o = loadOntologyFromString(premise);
        m.removeAxioms(o, o.getAxioms(AxiomType.DECLARATION));
    }
}
