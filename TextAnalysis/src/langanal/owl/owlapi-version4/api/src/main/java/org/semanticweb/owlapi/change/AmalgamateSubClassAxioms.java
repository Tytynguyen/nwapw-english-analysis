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
package org.semanticweb.owlapi.change;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;

/**
 * Given a set of ontologies S, for each ontology, O, in S, this change combines
 * multiple subclass axioms with a common left hand side into one subclass
 * axiom. For example, given A subClassOf B, A subClassOf C, this change will
 * remove these two axioms and replace them by adding one subclass axiom, A
 * subClassOf (B and C).
 * 
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.1.1
 */
public class AmalgamateSubClassAxioms extends AbstractCompositeOntologyChange {

    private static final long serialVersionUID = 40000L;

    /**
     * Instantiates a new amalgamate sub class axioms.
     * 
     * @param dataFactory
     *        the data factory
     * @param ontologies
     *        the ontologies to use
     */
    public AmalgamateSubClassAxioms(@Nonnull OWLDataFactory dataFactory,
            @Nonnull Set<OWLOntology> ontologies) {
        super(dataFactory);
        for (OWLOntology ont : checkNotNull(ontologies,
                "ontologies cannot be null")) {
            for (OWLClass cls : ont.getClassesInSignature()) {
                assert cls != null;
                Set<OWLSubClassOfAxiom> axioms = ont
                        .getSubClassAxiomsForSubClass(cls);
                if (axioms.size() > 1) {
                    Set<OWLClassExpression> superClasses = new HashSet<>();
                    for (OWLSubClassOfAxiom ax : axioms) {
                        assert ax != null;
                        addChange(new RemoveAxiom(ont, ax));
                        superClasses.add(ax.getSuperClass());
                    }
                    OWLClassExpression combinedSuperClass = getDataFactory()
                            .getOWLObjectIntersectionOf(superClasses);
                    addChange(new AddAxiom(ont, getDataFactory()
                            .getOWLSubClassOfAxiom(cls, combinedSuperClass)));
                }
            }
        }
    }
}
