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
package org.semanticweb.owlapi.profiles;

import static org.semanticweb.owlapi.vocab.OWL2Datatype.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.violations.*;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorExAdapter;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 */
public class OWL2QLProfile implements OWLProfile {

    @Nonnull
    static Boolean b(boolean b) {
        return b;
    }

    protected static final Set<IRI> ALLOWED_DATATYPES = new HashSet<>(Arrays.asList(
        //@formatter:off
            RDF_PLAIN_LITERAL.getIRI(), 
            RDF_XML_LITERAL.getIRI(), 
            RDFS_LITERAL.getIRI(),
            OWL_REAL.getIRI(), 
            OWL_RATIONAL.getIRI(), 
            XSD_DECIMAL.getIRI(),
            XSD_INTEGER.getIRI(), 
            XSD_NON_NEGATIVE_INTEGER.getIRI(), 
            XSD_STRING.getIRI(),
            XSD_NORMALIZED_STRING.getIRI(), 
            XSD_TOKEN.getIRI(), 
            XSD_NAME.getIRI(),
            XSD_NCNAME.getIRI(), 
            XSD_NMTOKEN.getIRI(), 
            XSD_HEX_BINARY.getIRI(),
            XSD_BASE_64_BINARY.getIRI(), 
            XSD_ANY_URI.getIRI(), 
            XSD_DATE_TIME.getIRI(),
            XSD_DATE_TIME_STAMP.getIRI()
            //@formatter:on
    ));

    /**
     * Gets the name of the profile.
     * 
     * @return A string that represents the name of the profile
     */
    @Override
    public String getName() {
        return "OWL 2 QL";
    }

    @Nonnull
    @Override
    public IRI getIRI() {
        return Profiles.OWL2_QL.getIRI();
    }

    /**
     * Checks an ontology and its import closure to see if it is within this
     * profile.
     * 
     * @param ontology
     *        The ontology to be checked.
     * @return An {@code OWLProfileReport} that describes whether or not the
     *         ontology is within this profile.
     */
    @Override
    public OWLProfileReport checkOntology(OWLOntology ontology) {
        OWL2DLProfile profile = new OWL2DLProfile();
        OWLProfileReport report = profile.checkOntology(ontology);
        Set<OWLProfileViolation> violations = new HashSet<>();
        violations.addAll(report.getViolations());
        OWLOntologyProfileWalker walker = new OWLOntologyProfileWalker(ontology.getImportsClosure());
        OWL2QLObjectVisitor visitor = new OWL2QLObjectVisitor(walker);
        walker.walkStructure(visitor);
        violations.addAll(visitor.getProfileViolations());
        return new OWLProfileReport(this, violations);
    }

    private class OWL2QLObjectVisitor extends OWLOntologyWalkerVisitor {

        @Nonnull
        private final Set<OWLProfileViolation> violations = new HashSet<>();

        OWL2QLObjectVisitor(@Nonnull OWLOntologyWalker walker) {
            super(walker);
        }

        public Set<OWLProfileViolation> getProfileViolations() {
            return new HashSet<>(violations);
        }

        @Override
        public void visit(OWLDatatype node) {
            if (!ALLOWED_DATATYPES.contains(node.getIRI())) {
                violations.add(new UseOfIllegalDataRange(getCurrentOntology(), getCurrentAxiom(), node));
            }
        }

        @Override
        public void visit(OWLAnonymousIndividual individual) {
            violations.add(new UseOfAnonymousIndividual(getCurrentOntology(), getCurrentAxiom(), individual));
        }

        @Override
        public void visit(OWLHasKeyAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLSubClassOfAxiom axiom) {
            if (!isOWL2QLSubClassExpression(axiom.getSubClass())) {
                violations.add(new UseOfNonSubClassExpression(getCurrentOntology(), axiom, axiom.getSubClass()));
            }
            if (!isOWL2QLSuperClassExpression(axiom.getSuperClass())) {
                violations.add(new UseOfNonSuperClassExpression(getCurrentOntology(), axiom, axiom.getSuperClass()));
            }
        }

        @Override
        public void visit(OWLEquivalentClassesAxiom axiom) {
            for (OWLClassExpression ce : axiom.getClassExpressions()) {
                assert ce != null;
                if (!isOWL2QLSubClassExpression(ce)) {
                    violations.add(new UseOfNonSubClassExpression(getCurrentOntology(), axiom, ce));
                }
            }
        }

        @Override
        public void visit(OWLDisjointClassesAxiom axiom) {
            for (OWLClassExpression ce : axiom.getClassExpressions()) {
                assert ce != null;
                if (!isOWL2QLSubClassExpression(ce)) {
                    violations.add(new UseOfNonSubClassExpression(getCurrentOntology(), axiom, ce));
                }
            }
        }

        @Override
        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            if (!isOWL2QLSuperClassExpression(axiom.getDomain())) {
                violations.add(new UseOfNonSuperClassExpression(getCurrentOntology(), axiom, axiom.getDomain()));
            }
        }

        @Override
        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            if (!isOWL2QLSuperClassExpression(axiom.getRange())) {
                violations.add(new UseOfNonSuperClassExpression(getCurrentOntology(), axiom, axiom.getRange()));
            }
        }

        @Override
        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLDataPropertyDomainAxiom axiom) {
            if (!isOWL2QLSuperClassExpression(axiom.getDomain())) {
                violations.add(new UseOfNonSuperClassExpression(getCurrentOntology(), axiom, axiom.getDomain()));
            }
        }

        @Override
        public void visit(OWLClassAssertionAxiom axiom) {
            if (axiom.getClassExpression().isAnonymous()) {
                violations.add(new UseOfNonAtomicClassExpression(getCurrentOntology(), axiom, axiom
                    .getClassExpression()));
            }
        }

        @Override
        public void visit(OWLSameIndividualAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(OWLDisjointUnionAxiom axiom) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), axiom));
        }

        @Override
        public void visit(SWRLRule rule) {
            violations.add(new UseOfIllegalAxiom(getCurrentOntology(), rule));
        }

        @Override
        public void visit(OWLDataComplementOf node) {
            violations.add(new UseOfIllegalDataRange(getCurrentOntology(), getCurrentAxiom(), node));
        }

        @Override
        public void visit(OWLDataOneOf node) {
            violations.add(new UseOfIllegalDataRange(getCurrentOntology(), getCurrentAxiom(), node));
        }

        @Override
        public void visit(OWLDatatypeRestriction node) {
            violations.add(new UseOfIllegalDataRange(getCurrentOntology(), getCurrentAxiom(), node));
        }

        @Override
        public void visit(OWLDataUnionOf node) {
            violations.add(new UseOfIllegalDataRange(getCurrentOntology(), getCurrentAxiom(), node));
        }
    }

    private static class OWL2QLSubClassExpressionChecker extends OWLClassExpressionVisitorExAdapter<Boolean> {

        OWL2QLSubClassExpressionChecker() {
            super(b(false));
        }

        @Override
        public Boolean visit(OWLClass ce) {
            return b(true);
        }

        @Override
        public Boolean visit(OWLObjectSomeValuesFrom ce) {
            return b(ce.getFiller().isOWLThing());
        }

        @Override
        public Boolean visit(OWLDataSomeValuesFrom ce) {
            return b(true);
        }
    }

    @Nonnull
    private final OWL2QLSubClassExpressionChecker subClassExpressionChecker = new OWL2QLSubClassExpressionChecker();

    protected boolean isOWL2QLSubClassExpression(OWLClassExpression ce) {
        return ce.accept(subClassExpressionChecker).booleanValue();
    }

    private class OWL2QLSuperClassExpressionChecker extends OWLClassExpressionVisitorExAdapter<Boolean> {

        OWL2QLSuperClassExpressionChecker() {
            super(b(false));
        }

        @Override
        public Boolean visit(OWLClass ce) {
            return b(true);
        }

        @Override
        public Boolean visit(OWLObjectIntersectionOf ce) {
            for (OWLClassExpression e : ce.getOperands()) {
                if (!e.accept(this).booleanValue()) {
                    return b(false);
                }
            }
            return b(true);
        }

        @Override
        public Boolean visit(OWLObjectComplementOf ce) {
            return b(isOWL2QLSubClassExpression(ce.getOperand()));
        }

        @Override
        public Boolean visit(OWLObjectSomeValuesFrom ce) {
            return b(!ce.getFiller().isAnonymous());
        }

        @Override
        public Boolean visit(OWLDataSomeValuesFrom ce) {
            return b(true);
        }
    }

    @Nonnull
    private final OWL2QLSuperClassExpressionChecker superClassExpressionChecker = new OWL2QLSuperClassExpressionChecker();

    /**
     * @param ce
     *        class
     * @return true if ce is superclass expression
     */
    public boolean isOWL2QLSuperClassExpression(OWLClassExpression ce) {
        return ce.accept(superClassExpressionChecker).booleanValue();
    }
}
