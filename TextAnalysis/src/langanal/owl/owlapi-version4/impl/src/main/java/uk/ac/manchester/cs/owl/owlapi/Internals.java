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
package uk.ac.manchester.cs.owl.owlapi;

import static org.semanticweb.owlapi.model.AxiomType.*;
import static org.semanticweb.owlapi.util.CollectionFactory.*;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;
import static uk.ac.manchester.cs.owl.owlapi.InitVisitorFactory.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Navigation;
import org.semanticweb.owlapi.search.Filters;
import org.semanticweb.owlapi.util.CollectionFactory;
import org.semanticweb.owlapi.util.OWLAxiomSearchFilter;
import org.semanticweb.owlapi.util.OWLAxiomVisitorAdapter;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * @author ignazio
 */
public class Internals implements Serializable {

    private static final long serialVersionUID = 40000L;

    private class ReferenceChecker implements OWLEntityVisitorEx<Boolean>, Serializable {

        private static final long serialVersionUID = 40000L;

        ReferenceChecker() {}

        @Override
        public Boolean visit(OWLClass cls) {
            return owlClassReferences.containsKey(cls);
        }

        @Override
        public Boolean visit(OWLObjectProperty property) {
            return owlObjectPropertyReferences.containsKey(property);
        }

        @Override
        public Boolean visit(OWLDataProperty property) {
            return owlDataPropertyReferences.containsKey(property);
        }

        @Override
        public Boolean visit(OWLNamedIndividual individual) {
            return owlIndividualReferences.containsKey(individual);
        }

        @Override
        public Boolean visit(OWLDatatype datatype) {
            return owlDatatypeReferences.containsKey(datatype);
        }

        @Override
        public Boolean visit(OWLAnnotationProperty property) {
            return owlAnnotationPropertyReferences.containsKey(property);
        }
    }

    protected class SetPointer<K extends Serializable> implements Serializable {

        private static final long serialVersionUID = 40000L;
        @Nonnull
        private final Set<K> set = createSyncSet();

        public boolean isEmpty() {
            return set.isEmpty();
        }

        @Nonnull
        public Set<K> copy() {
            return CollectionFactory.getCopyOnRequestSetFromMutableCollection(set);
        }

        @Nonnull
        public Iterable<K> iterable() {
            return set;
        }

        public boolean add(K k) {
            return set.add(k);
        }

        public boolean contains(K k) {
            return set.contains(k);
        }

        public boolean remove(K k) {
            return set.remove(k);
        }
    }

    //@formatter:off
    @Nonnull protected transient MapPointer<OWLClassExpression, OWLClassAssertionAxiom>                          classAssertionAxiomsByClass                         = buildLazy(CLASS_ASSERTION, CLASSEXPRESSIONS);
    @Nonnull protected transient MapPointer<OWLAnnotationSubject, OWLAnnotationAssertionAxiom>                   annotationAssertionAxiomsBySubject                  = buildLazy(ANNOTATION_ASSERTION, ANNOTSUPERNAMED);
    @Nonnull protected transient MapPointer<OWLClass, OWLSubClassOfAxiom>                                        subClassAxiomsBySubPosition                         = buildLazy(SUBCLASS_OF, CLASSSUBNAMED);
    @Nonnull protected transient MapPointer<OWLClass, OWLSubClassOfAxiom>                                        subClassAxiomsBySuperPosition                       = buildLazy(SUBCLASS_OF, CLASSSUPERNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLSubObjectPropertyOfAxiom>            objectSubPropertyAxiomsBySubPosition                = buildLazy(SUB_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLSubObjectPropertyOfAxiom>            objectSubPropertyAxiomsBySuperPosition              = buildLazy(SUB_OBJECT_PROPERTY, OPSUPERNAMED);
    @Nonnull protected transient MapPointer<OWLDataPropertyExpression, OWLSubDataPropertyOfAxiom>                dataSubPropertyAxiomsBySubPosition                  = buildLazy(SUB_DATA_PROPERTY, DPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLDataPropertyExpression, OWLSubDataPropertyOfAxiom>                dataSubPropertyAxiomsBySuperPosition                = buildLazy(SUB_DATA_PROPERTY, DPSUPERNAMED);

    @Nonnull protected transient MapPointer<OWLClass, OWLClassAxiom>                                             classAxiomsByClass                                  = buildClassAxiomByClass();
    @Nonnull protected transient MapPointer<OWLClass, OWLEquivalentClassesAxiom>                                 equivalentClassesAxiomsByClass                      = buildLazy(EQUIVALENT_CLASSES, CLASSCOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLClass, OWLDisjointClassesAxiom>                                   disjointClassesAxiomsByClass                        = buildLazy(DISJOINT_CLASSES, CLASSCOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLClass, OWLDisjointUnionAxiom>                                     disjointUnionAxiomsByClass                          = buildLazy(DISJOINT_UNION, CLASSCOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLClass, OWLHasKeyAxiom>                                            hasKeyAxiomsByClass                                 = buildLazy(HAS_KEY, CLASSSUPERNAMED);

    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLEquivalentObjectPropertiesAxiom>     equivalentObjectPropertyAxiomsByProperty            = buildLazy(EQUIVALENT_OBJECT_PROPERTIES, OPCOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLDisjointObjectPropertiesAxiom>       disjointObjectPropertyAxiomsByProperty              = buildLazy(DISJOINT_OBJECT_PROPERTIES, OPCOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLObjectPropertyDomainAxiom>           objectPropertyDomainAxiomsByProperty                = buildLazy(OBJECT_PROPERTY_DOMAIN, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLObjectPropertyRangeAxiom>            objectPropertyRangeAxiomsByProperty                 = buildLazy(OBJECT_PROPERTY_RANGE, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLFunctionalObjectPropertyAxiom>       functionalObjectPropertyAxiomsByProperty            = buildLazy(FUNCTIONAL_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLInverseFunctionalObjectPropertyAxiom>inverseFunctionalPropertyAxiomsByProperty           = buildLazy(INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLSymmetricObjectPropertyAxiom>        symmetricPropertyAxiomsByProperty                   = buildLazy(SYMMETRIC_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLAsymmetricObjectPropertyAxiom>       asymmetricPropertyAxiomsByProperty                  = buildLazy(ASYMMETRIC_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLReflexiveObjectPropertyAxiom>        reflexivePropertyAxiomsByProperty                   = buildLazy(REFLEXIVE_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLIrreflexiveObjectPropertyAxiom>      irreflexivePropertyAxiomsByProperty                 = buildLazy(IRREFLEXIVE_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLTransitiveObjectPropertyAxiom>       transitivePropertyAxiomsByProperty                  = buildLazy(TRANSITIVE_OBJECT_PROPERTY, OPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLObjectPropertyExpression, OWLInverseObjectPropertiesAxiom>        inversePropertyAxiomsByProperty                     = buildLazy(INVERSE_OBJECT_PROPERTIES, OPCOLLECTIONS);

    @Nonnull protected transient MapPointer<OWLDataPropertyExpression, OWLEquivalentDataPropertiesAxiom>         equivalentDataPropertyAxiomsByProperty              = buildLazy(EQUIVALENT_DATA_PROPERTIES, DPCOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLDataPropertyExpression, OWLDisjointDataPropertiesAxiom>           disjointDataPropertyAxiomsByProperty                = buildLazy(DISJOINT_DATA_PROPERTIES, DPCOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLDataPropertyExpression, OWLDataPropertyDomainAxiom>               dataPropertyDomainAxiomsByProperty                  = buildLazy(DATA_PROPERTY_DOMAIN, DPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLDataPropertyExpression, OWLDataPropertyRangeAxiom>                dataPropertyRangeAxiomsByProperty                   = buildLazy(DATA_PROPERTY_RANGE, DPSUBNAMED);
    @Nonnull protected transient MapPointer<OWLDataPropertyExpression, OWLFunctionalDataPropertyAxiom>           functionalDataPropertyAxiomsByProperty              = buildLazy(FUNCTIONAL_DATA_PROPERTY, DPSUBNAMED);

    @Nonnull protected transient MapPointer<OWLIndividual, OWLClassAssertionAxiom>                               classAssertionAxiomsByIndividual                    = buildLazy(CLASS_ASSERTION, INDIVIDUALSUBNAMED);
    @Nonnull protected transient MapPointer<OWLIndividual, OWLObjectPropertyAssertionAxiom>                      objectPropertyAssertionsByIndividual                = buildLazy(OBJECT_PROPERTY_ASSERTION, INDIVIDUALSUBNAMED);
    @Nonnull protected transient MapPointer<OWLIndividual, OWLDataPropertyAssertionAxiom>                        dataPropertyAssertionsByIndividual                  = buildLazy(DATA_PROPERTY_ASSERTION, INDIVIDUALSUBNAMED);
    @Nonnull protected transient MapPointer<OWLIndividual, OWLNegativeObjectPropertyAssertionAxiom>              negativeObjectPropertyAssertionAxiomsByIndividual   = buildLazy(NEGATIVE_OBJECT_PROPERTY_ASSERTION, INDIVIDUALSUBNAMED);
    @Nonnull protected transient MapPointer<OWLIndividual, OWLNegativeDataPropertyAssertionAxiom>                negativeDataPropertyAssertionAxiomsByIndividual     = buildLazy(NEGATIVE_DATA_PROPERTY_ASSERTION, INDIVIDUALSUBNAMED);
    @Nonnull protected transient MapPointer<OWLIndividual, OWLDifferentIndividualsAxiom>                         differentIndividualsAxiomsByIndividual              = buildLazy(DIFFERENT_INDIVIDUALS, ICOLLECTIONS);
    @Nonnull protected transient MapPointer<OWLIndividual, OWLSameIndividualAxiom>                               sameIndividualsAxiomsByIndividual                   = buildLazy(SAME_INDIVIDUAL, ICOLLECTIONS);

    @Nonnull protected  SetPointer<OWLImportsDeclaration>                        importsDeclarations                 = new SetPointer<>();
    @Nonnull protected  SetPointer<OWLAnnotation>                                ontologyAnnotations                 = new SetPointer<>();
    @Nonnull protected  SetPointer<OWLClassAxiom>                                generalClassAxioms                  = new SetPointer<>();
    @Nonnull protected  SetPointer<OWLSubPropertyChainOfAxiom>                   propertyChainSubPropertyAxioms      = new SetPointer<>();

    @Nonnull protected transient MapPointer<AxiomType<?>, OWLAxiom>              axiomsByType                        = build();

    @Nonnull protected transient MapPointer<OWLClass, OWLAxiom>                  owlClassReferences                  = build();
    @Nonnull protected transient MapPointer<OWLObjectProperty, OWLAxiom>         owlObjectPropertyReferences         = build();
    @Nonnull protected transient MapPointer<OWLDataProperty, OWLAxiom>           owlDataPropertyReferences           = build();
    @Nonnull protected transient MapPointer<OWLNamedIndividual, OWLAxiom>        owlIndividualReferences             = build();
    @Nonnull protected transient MapPointer<OWLAnonymousIndividual, OWLAxiom>    owlAnonymousIndividualReferences    = build();
    @Nonnull protected transient MapPointer<OWLDatatype, OWLAxiom>               owlDatatypeReferences               = build();
    @Nonnull protected transient MapPointer<OWLAnnotationProperty, OWLAxiom>     owlAnnotationPropertyReferences     = build();
    @Nonnull protected transient MapPointer<OWLEntity, OWLDeclarationAxiom>      declarationsByEntity                = build();
//@formatter:on
    @Nullable
    private List<OWLAxiom> axiomsForSerialization;

    @SuppressWarnings("null")
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        axiomsByType = build();
        owlClassReferences = build();
        owlObjectPropertyReferences = build();
        owlDataPropertyReferences = build();
        owlIndividualReferences = build();
        owlAnonymousIndividualReferences = build();
        owlDatatypeReferences = build();
        owlAnnotationPropertyReferences = build();
        declarationsByEntity = build();
        classAssertionAxiomsByClass = buildLazy(CLASS_ASSERTION, CLASSEXPRESSIONS);
        annotationAssertionAxiomsBySubject = buildLazy(ANNOTATION_ASSERTION, ANNOTSUPERNAMED);
        subClassAxiomsBySubPosition = buildLazy(SUBCLASS_OF, CLASSSUBNAMED);
        subClassAxiomsBySuperPosition = buildLazy(SUBCLASS_OF, CLASSSUPERNAMED);
        objectSubPropertyAxiomsBySubPosition = buildLazy(SUB_OBJECT_PROPERTY, OPSUBNAMED);
        objectSubPropertyAxiomsBySuperPosition = buildLazy(SUB_OBJECT_PROPERTY, OPSUPERNAMED);
        dataSubPropertyAxiomsBySubPosition = buildLazy(SUB_DATA_PROPERTY, DPSUBNAMED);
        dataSubPropertyAxiomsBySuperPosition = buildLazy(SUB_DATA_PROPERTY, DPSUPERNAMED);
        classAxiomsByClass = buildClassAxiomByClass();
        equivalentClassesAxiomsByClass = buildLazy(EQUIVALENT_CLASSES, CLASSCOLLECTIONS);
        disjointClassesAxiomsByClass = buildLazy(DISJOINT_CLASSES, CLASSCOLLECTIONS);
        disjointUnionAxiomsByClass = buildLazy(DISJOINT_UNION, CLASSCOLLECTIONS);
        hasKeyAxiomsByClass = buildLazy(HAS_KEY, CLASSSUPERNAMED);
        equivalentObjectPropertyAxiomsByProperty = buildLazy(EQUIVALENT_OBJECT_PROPERTIES, OPCOLLECTIONS);
        disjointObjectPropertyAxiomsByProperty = buildLazy(DISJOINT_OBJECT_PROPERTIES, OPCOLLECTIONS);
        objectPropertyDomainAxiomsByProperty = buildLazy(OBJECT_PROPERTY_DOMAIN, OPSUBNAMED);
        objectPropertyRangeAxiomsByProperty = buildLazy(OBJECT_PROPERTY_RANGE, OPSUBNAMED);
        functionalObjectPropertyAxiomsByProperty = buildLazy(FUNCTIONAL_OBJECT_PROPERTY, OPSUBNAMED);
        inverseFunctionalPropertyAxiomsByProperty = buildLazy(INVERSE_FUNCTIONAL_OBJECT_PROPERTY, OPSUBNAMED);
        symmetricPropertyAxiomsByProperty = buildLazy(SYMMETRIC_OBJECT_PROPERTY, OPSUBNAMED);
        asymmetricPropertyAxiomsByProperty = buildLazy(ASYMMETRIC_OBJECT_PROPERTY, OPSUBNAMED);
        reflexivePropertyAxiomsByProperty = buildLazy(REFLEXIVE_OBJECT_PROPERTY, OPSUBNAMED);
        irreflexivePropertyAxiomsByProperty = buildLazy(IRREFLEXIVE_OBJECT_PROPERTY, OPSUBNAMED);
        transitivePropertyAxiomsByProperty = buildLazy(TRANSITIVE_OBJECT_PROPERTY, OPSUBNAMED);
        inversePropertyAxiomsByProperty = buildLazy(INVERSE_OBJECT_PROPERTIES, OPCOLLECTIONS);
        equivalentDataPropertyAxiomsByProperty = buildLazy(EQUIVALENT_DATA_PROPERTIES, DPCOLLECTIONS);
        disjointDataPropertyAxiomsByProperty = buildLazy(DISJOINT_DATA_PROPERTIES, DPCOLLECTIONS);
        dataPropertyDomainAxiomsByProperty = buildLazy(DATA_PROPERTY_DOMAIN, DPSUBNAMED);
        dataPropertyRangeAxiomsByProperty = buildLazy(DATA_PROPERTY_RANGE, DPSUBNAMED);
        functionalDataPropertyAxiomsByProperty = buildLazy(FUNCTIONAL_DATA_PROPERTY, DPSUBNAMED);
        classAssertionAxiomsByIndividual = buildLazy(CLASS_ASSERTION, INDIVIDUALSUBNAMED);
        objectPropertyAssertionsByIndividual = buildLazy(OBJECT_PROPERTY_ASSERTION, INDIVIDUALSUBNAMED);
        dataPropertyAssertionsByIndividual = buildLazy(DATA_PROPERTY_ASSERTION, INDIVIDUALSUBNAMED);
        negativeObjectPropertyAssertionAxiomsByIndividual = buildLazy(NEGATIVE_OBJECT_PROPERTY_ASSERTION,
            INDIVIDUALSUBNAMED);
        negativeDataPropertyAssertionAxiomsByIndividual = buildLazy(NEGATIVE_DATA_PROPERTY_ASSERTION,
            INDIVIDUALSUBNAMED);
        differentIndividualsAxiomsByIndividual = buildLazy(DIFFERENT_INDIVIDUALS, ICOLLECTIONS);
        sameIndividualsAxiomsByIndividual = buildLazy(SAME_INDIVIDUAL, ICOLLECTIONS);
        for (OWLAxiom ax : axiomsForSerialization) {
            addAxiom(ax);
        }
        axiomsForSerialization = null;
    }

    /**
     * Trims the capacity of the axiom indexes . An application can use this
     * operation to minimize the storage of the internals instance.
     */
    public void trimToSize() {
        axiomsByType.trimToSize();
        owlClassReferences.trimToSize();
        owlObjectPropertyReferences.trimToSize();
        owlDataPropertyReferences.trimToSize();
        owlIndividualReferences.trimToSize();
        owlAnonymousIndividualReferences.trimToSize();
        owlDatatypeReferences.trimToSize();
        owlAnnotationPropertyReferences.trimToSize();
        declarationsByEntity.trimToSize();
        classAssertionAxiomsByClass.trimToSize();
        annotationAssertionAxiomsBySubject.trimToSize();
        subClassAxiomsBySubPosition.trimToSize();
        subClassAxiomsBySuperPosition.trimToSize();
        objectSubPropertyAxiomsBySubPosition.trimToSize();
        objectSubPropertyAxiomsBySuperPosition.trimToSize();
        dataSubPropertyAxiomsBySubPosition.trimToSize();
        dataSubPropertyAxiomsBySuperPosition.trimToSize();
        classAxiomsByClass.trimToSize();
        equivalentClassesAxiomsByClass.trimToSize();
        disjointClassesAxiomsByClass.trimToSize();
        disjointUnionAxiomsByClass.trimToSize();
        hasKeyAxiomsByClass.trimToSize();
        equivalentObjectPropertyAxiomsByProperty.trimToSize();
        disjointObjectPropertyAxiomsByProperty.trimToSize();
        objectPropertyDomainAxiomsByProperty.trimToSize();
        objectPropertyRangeAxiomsByProperty.trimToSize();
        functionalObjectPropertyAxiomsByProperty.trimToSize();
        inverseFunctionalPropertyAxiomsByProperty.trimToSize();
        symmetricPropertyAxiomsByProperty.trimToSize();
        asymmetricPropertyAxiomsByProperty.trimToSize();
        reflexivePropertyAxiomsByProperty.trimToSize();
        irreflexivePropertyAxiomsByProperty.trimToSize();
        transitivePropertyAxiomsByProperty.trimToSize();
        inversePropertyAxiomsByProperty.trimToSize();
        equivalentDataPropertyAxiomsByProperty.trimToSize();
        disjointDataPropertyAxiomsByProperty.trimToSize();
        dataPropertyDomainAxiomsByProperty.trimToSize();
        dataPropertyRangeAxiomsByProperty.trimToSize();
        functionalDataPropertyAxiomsByProperty.trimToSize();
        classAssertionAxiomsByIndividual.trimToSize();
        objectPropertyAssertionsByIndividual.trimToSize();
        dataPropertyAssertionsByIndividual.trimToSize();
        negativeObjectPropertyAssertionAxiomsByIndividual.trimToSize();
        negativeDataPropertyAssertionAxiomsByIndividual.trimToSize();
        differentIndividualsAxiomsByIndividual.trimToSize();
        sameIndividualsAxiomsByIndividual.trimToSize();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        axiomsForSerialization = new ArrayList<>();
        Iterables.addAll(axiomsForSerialization, axiomsByType.getAllValues());
        stream.defaultWriteObject();
    }

    @Nonnull
    private final AddAxiomVisitor addChangeVisitor = new AddAxiomVisitor();
    @Nonnull
    private final RemoveAxiomVisitor removeChangeVisitor = new RemoveAxiomVisitor();
    @Nonnull
    private final ReferenceChecker refChecker = new ReferenceChecker();
    @Nonnull
    private final ReferencedAxiomsCollector refAxiomsCollector = new ReferencedAxiomsCollector();

    /**
     * @param i
     *        iri
     * @return true if a class with this iri exists
     */
    public boolean containsClassInSignature(IRI i) {
        return owlClassReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if an object property with this iri exists
     */
    public boolean containsObjectPropertyInSignature(IRI i) {
        return owlObjectPropertyReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if a data property with this iri exists
     */
    public boolean containsDataPropertyInSignature(IRI i) {
        return owlDataPropertyReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if an annotation property with this iri exists
     */
    public boolean containsAnnotationPropertyInSignature(IRI i) {
        return owlAnnotationPropertyReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if a individual with this iri exists
     */
    public boolean containsIndividualInSignature(IRI i) {
        return owlIndividualReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if a datatype with this iri exists
     */
    public boolean containsDatatypeInSignature(IRI i) {
        return owlDatatypeReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if a class with this iri exists
     */
    public boolean containsClassInSignature(OWLClass i) {
        return owlClassReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if an object property with this iri exists
     */
    public boolean containsObjectPropertyInSignature(OWLObjectProperty i) {
        return owlObjectPropertyReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if a data property with this iri exists
     */
    public boolean containsDataPropertyInSignature(OWLDataProperty i) {
        return owlDataPropertyReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if an annotation property with this iri exists
     */
    public boolean containsAnnotationPropertyInSignature(OWLAnnotationProperty i) {
        return owlAnnotationPropertyReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if a individual with this iri exists
     */
    public boolean containsIndividualInSignature(OWLNamedIndividual i) {
        return owlIndividualReferences.containsReference(i);
    }

    /**
     * @param i
     *        iri
     * @return true if a datatype with this iri exists
     */
    public boolean containsDatatypeInSignature(OWLDatatype i) {
        return owlDatatypeReferences.containsReference(i);
    }

    /**
     * @param type
     *        type of map key
     * @param axiom
     *        class of axiom indexed
     * @param <T>
     *        key type
     * @param <A>
     *        value type
     * @return map pointer matching the search, or null if there is not one
     */
    // not always not null, but supposed to
    @Nonnull
    <T extends OWLObject, A extends OWLAxiom> Optional<MapPointer<T, A>> get(@Nonnull Class<T> type,
        @Nonnull Class<A> axiom) {
        return get(type, axiom, Navigation.IN_SUB_POSITION);
    }

    /**
     * @param type
     *        type of map key
     * @param axiom
     *        class of axiom indexed
     * @param position
     *        for axioms with a left/right distinction, IN_SUPER_POSITION means
     *        right index
     * @param <T>
     *        key type
     * @param <A>
     *        value type
     * @return map pointer matching the search, or null if there is not one
     */
    // not always not null, but supposed to be
    @Nonnull
    @SuppressWarnings({ "unchecked", })
    <T extends OWLObject, A extends OWLAxiom> Optional<MapPointer<T, A>> get(@Nonnull Class<T> type,
        @Nonnull Class<A> axiom, Navigation position) {
        if (OWLEntity.class.isAssignableFrom(type) && axiom.equals(OWLDeclarationAxiom.class)) {
            return Optional.of((MapPointer<T, A>) declarationsByEntity);
        }
        if (type.equals(OWLClass.class) && axiom.equals(OWLAxiom.class)) {
            return Optional.of((MapPointer<T, A>) owlClassReferences);
        }
        if (type.equals(OWLObjectProperty.class) && axiom.equals(OWLAxiom.class)) {
            return Optional.of((MapPointer<T, A>) owlObjectPropertyReferences);
        }
        if (type.equals(OWLDataProperty.class) && axiom.equals(OWLAxiom.class)) {
            return Optional.of((MapPointer<T, A>) owlDataPropertyReferences);
        }
        if (type.equals(OWLNamedIndividual.class) && axiom.equals(OWLAxiom.class)) {
            return Optional.of((MapPointer<T, A>) owlIndividualReferences);
        }
        if (type.equals(OWLAnonymousIndividual.class) && axiom.equals(OWLAxiom.class)) {
            return Optional.of((MapPointer<T, A>) owlAnonymousIndividualReferences);
        }
        if (type.equals(OWLDatatype.class) && axiom.equals(OWLAxiom.class)) {
            return Optional.of((MapPointer<T, A>) owlDatatypeReferences);
        }
        if (type.equals(OWLAnnotationProperty.class) && axiom.equals(OWLAxiom.class)) {
            return Optional.of((MapPointer<T, A>) owlAnnotationPropertyReferences);
        }
        if (type.equals(OWLClassExpression.class)) {
            return Optional.of((MapPointer<T, A>) classAssertionAxiomsByClass);
        }
        if (type.equals(OWLObjectPropertyExpression.class)) {
            if (axiom.equals(OWLSubObjectPropertyOfAxiom.class)) {
                if (position == Navigation.IN_SUPER_POSITION) {
                    return Optional.of((MapPointer<T, A>) objectSubPropertyAxiomsBySuperPosition);
                } else {
                    return Optional.of((MapPointer<T, A>) objectSubPropertyAxiomsBySubPosition);
                }
            }
            if (axiom.equals(OWLEquivalentObjectPropertiesAxiom.class)) {
                return Optional.of((MapPointer<T, A>) equivalentObjectPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLDisjointObjectPropertiesAxiom.class)) {
                return Optional.of((MapPointer<T, A>) disjointObjectPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLObjectPropertyDomainAxiom.class)) {
                return Optional.of((MapPointer<T, A>) objectPropertyDomainAxiomsByProperty);
            }
            if (axiom.equals(OWLObjectPropertyRangeAxiom.class)) {
                return Optional.of((MapPointer<T, A>) objectPropertyRangeAxiomsByProperty);
            }
            if (axiom.equals(OWLFunctionalObjectPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) functionalObjectPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLInverseFunctionalObjectPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) inverseFunctionalPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLSymmetricObjectPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) symmetricPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLAsymmetricObjectPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) asymmetricPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLReflexiveObjectPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) reflexivePropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLIrreflexiveObjectPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) irreflexivePropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLTransitiveObjectPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) transitivePropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLInverseObjectPropertiesAxiom.class)) {
                return Optional.of((MapPointer<T, A>) inversePropertyAxiomsByProperty);
            }
        }
        if (type.equals(OWLDataPropertyExpression.class)) {
            if (axiom.equals(OWLSubDataPropertyOfAxiom.class)) {
                if (position == Navigation.IN_SUPER_POSITION) {
                    return Optional.of((MapPointer<T, A>) dataSubPropertyAxiomsBySuperPosition);
                } else {
                    return Optional.of((MapPointer<T, A>) dataSubPropertyAxiomsBySubPosition);
                }
            }
            if (axiom.equals(OWLEquivalentDataPropertiesAxiom.class)) {
                return Optional.of((MapPointer<T, A>) equivalentDataPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLDisjointDataPropertiesAxiom.class)) {
                return Optional.of((MapPointer<T, A>) disjointDataPropertyAxiomsByProperty);
            }
            if (axiom.equals(OWLDataPropertyDomainAxiom.class)) {
                return Optional.of((MapPointer<T, A>) dataPropertyDomainAxiomsByProperty);
            }
            if (axiom.equals(OWLDataPropertyRangeAxiom.class)) {
                return Optional.of((MapPointer<T, A>) dataPropertyRangeAxiomsByProperty);
            }
            if (axiom.equals(OWLFunctionalDataPropertyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) functionalDataPropertyAxiomsByProperty);
            }
        }
        if (type.equals(OWLAnnotationSubject.class) || type.equals(IRI.class)) {
            return Optional.of((MapPointer<T, A>) annotationAssertionAxiomsBySubject);
        }
        if (type.equals(OWLIndividual.class)) {
            if (axiom.equals(OWLClassAssertionAxiom.class)) {
                return Optional.of((MapPointer<T, A>) classAssertionAxiomsByIndividual);
            }
            if (axiom.equals(OWLObjectPropertyAssertionAxiom.class)) {
                return Optional.of((MapPointer<T, A>) objectPropertyAssertionsByIndividual);
            }
            if (axiom.equals(OWLDataPropertyAssertionAxiom.class)) {
                return Optional.of((MapPointer<T, A>) dataPropertyAssertionsByIndividual);
            }
            if (axiom.equals(OWLNegativeObjectPropertyAssertionAxiom.class)) {
                return Optional.of((MapPointer<T, A>) negativeObjectPropertyAssertionAxiomsByIndividual);
            }
            if (axiom.equals(OWLNegativeDataPropertyAssertionAxiom.class)) {
                return Optional.of((MapPointer<T, A>) negativeDataPropertyAssertionAxiomsByIndividual);
            }
            if (axiom.equals(OWLDifferentIndividualsAxiom.class)) {
                return Optional.of((MapPointer<T, A>) differentIndividualsAxiomsByIndividual);
            }
            if (axiom.equals(OWLSameIndividualAxiom.class)) {
                return Optional.of((MapPointer<T, A>) sameIndividualsAxiomsByIndividual);
            }
        }
        if (type.equals(OWLClass.class)) {
            if (axiom.equals(OWLSubClassOfAxiom.class)) {
                if (position == Navigation.IN_SUPER_POSITION) {
                    return Optional.of((MapPointer<T, A>) subClassAxiomsBySuperPosition);
                } else {
                    return Optional.of((MapPointer<T, A>) subClassAxiomsBySubPosition);
                }
            }
            if (axiom.equals(OWLClassAxiom.class)) {
                return Optional.of((MapPointer<T, A>) classAxiomsByClass);
            }
            if (axiom.equals(OWLEquivalentClassesAxiom.class)) {
                return Optional.of((MapPointer<T, A>) equivalentClassesAxiomsByClass);
            }
            if (axiom.equals(OWLDisjointClassesAxiom.class)) {
                return Optional.of((MapPointer<T, A>) disjointClassesAxiomsByClass);
            }
            if (axiom.equals(OWLDisjointUnionAxiom.class)) {
                return Optional.of((MapPointer<T, A>) disjointUnionAxiomsByClass);
            }
            if (axiom.equals(OWLHasKeyAxiom.class)) {
                return Optional.of((MapPointer<T, A>) hasKeyAxiomsByClass);
            }
        }
        return Optional.absent();
    }

    @Nonnull
    protected <K, V extends OWLAxiom> MapPointer<K, V> build() {
        return build(null, null);
    }

    @Nonnull
    protected <K, V extends OWLAxiom> MapPointer<K, V> buildLazy(AxiomType<?> t, OWLAxiomVisitorEx<?> v) {
        return new MapPointer<>(t, v, false, this);
    }

    @Nonnull
    protected ClassAxiomByClassPointer buildClassAxiomByClass() {
        return new ClassAxiomByClassPointer(null, null, false, this);
    }

    @Nonnull
    protected <K, V extends OWLAxiom> MapPointer<K, V> build(AxiomType<?> t, OWLAxiomVisitorEx<?> v) {
        return new MapPointer<>(t, v, true, this);
    }

    /**
     * @param axiom
     *        axiom to add
     * @return true if the axiom was not already included
     */
    public boolean addAxiom(@Nonnull final OWLAxiom axiom) {
        checkNotNull(axiom, "axiom cannot be null");
        if (getAxiomsByType().put(axiom.getAxiomType(), axiom)) {
            axiom.accept(addChangeVisitor);
            axiom.accept(new AbstractEntityRegistrationManager() {

                @Override
                public void visit(OWLClass ce) {
                    owlClassReferences.put(ce, axiom);
                }

                @Override
                public void visit(OWLObjectProperty property) {
                    owlObjectPropertyReferences.put(property, axiom);
                }

                @Override
                public void visit(OWLDataProperty property) {
                    owlDataPropertyReferences.put(property, axiom);
                }

                @Override
                public void visit(OWLNamedIndividual individual) {
                    owlIndividualReferences.put(individual, axiom);
                }

                @Override
                public void visit(OWLAnnotationProperty property) {
                    owlAnnotationPropertyReferences.put(property, axiom);
                }

                @Override
                public void visit(OWLDatatype node) {
                    owlDatatypeReferences.put(node, axiom);
                }

                @Override
                public void visit(OWLAnonymousIndividual individual) {
                    owlAnonymousIndividualReferences.put(individual, axiom);
                }
            });
            return true;
        }
        return false;
    }

    /**
     * @param axiom
     *        axiom to remove
     * @return true if removed
     */
    public boolean removeAxiom(@Nonnull final OWLAxiom axiom) {
        checkNotNull(axiom, "axiom cannot be null");
        if (getAxiomsByType().remove(axiom.getAxiomType(), axiom)) {
            axiom.accept(removeChangeVisitor);
            AbstractEntityRegistrationManager referenceRemover = new AbstractEntityRegistrationManager() {

                @Override
                public void visit(OWLClass ce) {
                    owlClassReferences.remove(ce, axiom);
                }

                @Override
                public void visit(OWLObjectProperty property) {
                    owlObjectPropertyReferences.remove(property, axiom);
                }

                @Override
                public void visit(OWLDataProperty property) {
                    owlDataPropertyReferences.remove(property, axiom);
                }

                @Override
                public void visit(OWLNamedIndividual individual) {
                    owlIndividualReferences.remove(individual, axiom);
                }

                @Override
                public void visit(OWLAnnotationProperty property) {
                    owlAnnotationPropertyReferences.remove(property, axiom);
                }

                @Override
                public void visit(OWLDatatype node) {
                    owlDatatypeReferences.remove(node, axiom);
                }

                @Override
                public void visit(OWLAnonymousIndividual individual) {
                    owlAnonymousIndividualReferences.remove(individual, axiom);
                }
            };
            axiom.accept(referenceRemover);
            return true;
        }
        return false;
    }

    /**
     * @param e
     *        entity to check
     * @return true if the entity is declared in the ontology
     */
    public boolean isDeclared(OWLEntity e) {
        return declarationsByEntity.containsKey(e);
    }

    /**
     * @return true if empty
     */
    public boolean isEmpty() {
        return axiomsByType.isEmpty() && ontologyAnnotations.isEmpty();
    }

    /**
     * @param filter
     *        filter to satisfy
     * @param <K>
     *        key type
     * @param key
     *        key
     * @return set of values
     */
    @Nonnull
    public <K> Collection<? extends OWLAxiom> filterAxioms(@Nonnull OWLAxiomSearchFilter filter, @Nonnull K key) {
        if (filter == Filters.annotations) {
            Optional<MapPointer<OWLAnnotationSubject, OWLAnnotationAssertionAxiom>> mapPointerOptional = get(
                OWLAnnotationSubject.class, OWLAnnotationAssertionAxiom.class);
            if (mapPointerOptional.isPresent()) {
                MapPointer<OWLAnnotationSubject, OWLAnnotationAssertionAxiom> mapPointer = mapPointerOptional.get();
                Collection<OWLAnnotationAssertionAxiom> values = mapPointer.getValues((OWLAnnotationSubject) key);
                return values;
            }
        }
        return getAxiomsByType().filterAxioms(filter, key);
    }

    /**
     * @param <K>
     *        key type
     * @param filter
     *        filter to satisfy
     * @param key
     *        key to match
     * @return true if the filter is matched at least once
     */
    public <K> boolean contains(@Nonnull OWLAxiomSearchFilter filter, @Nonnull K key) {
        for (AxiomType<?> at : filter.getAxiomTypes()) {
            for (OWLAxiom t : getAxiomsByType().getValues(at)) {
                assert t != null;
                if (filter.pass(t, key)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param copy
     *        true if a copy of the set should be returned, false for a non
     *        defensive copy (to be used only by OWLImmutableOntologyImpl for
     *        iteration)
     * @return iterable of imports declaration
     */
    @Nonnull
    public Iterable<OWLImportsDeclaration> getImportsDeclarations(boolean copy) {
        if (!copy) {
            return importsDeclarations.iterable();
        }
        return importsDeclarations.copy();
    }

    /**
     * @param importDeclaration
     *        import declaration to remove
     * @return true if added
     */
    public boolean addImportsDeclaration(OWLImportsDeclaration importDeclaration) {
        return importsDeclarations.add(importDeclaration);
    }

    /**
     * @param importDeclaration
     *        import declaration to remove
     * @return true if removed
     */
    public boolean removeImportsDeclaration(OWLImportsDeclaration importDeclaration) {
        return importsDeclarations.remove(importDeclaration);
    }

    /**
     * @param copy
     *        true if a copy of the set should be returned, false for a non
     *        defensive copy (to be used only by OWLImmutableOntologyImpl for
     *        iteration)
     * @return iterable of annotations
     */
    @Nonnull
    Iterable<OWLAnnotation> getOntologyAnnotations(boolean copy) {
        if (!copy) {
            return ontologyAnnotations.iterable();
        }
        return ontologyAnnotations.copy();
    }

    /**
     * @param ann
     *        annotation to add
     * @return true if annotation added
     */
    public boolean addOntologyAnnotation(OWLAnnotation ann) {
        return ontologyAnnotations.add(ann);
    }

    /**
     * @param ann
     *        annotation to remove
     * @return true if annotation removed
     */
    public boolean removeOntologyAnnotation(OWLAnnotation ann) {
        return ontologyAnnotations.remove(ann);
    }

    /**
     * @param p
     *        pointer
     * @param <K>
     *        key type
     * @param <V>
     *        value type
     * @param k
     *        key
     * @param v
     *        value
     * @return true if the pair (key, value) is contained
     */
    public static <K, V extends OWLAxiom> boolean contains(@Nonnull MapPointer<K, V> p, K k, V v) {
        return p.contains(k, v);
    }

    /**
     * @return count of all axioms
     */
    public int getAxiomCount() {
        return axiomsByType.size();
    }

    /**
     * Gets the axioms by type.
     * 
     * @return the axioms by type
     */
    @Nonnull
    public Iterable<OWLAxiom> getAxioms() {
        return axiomsByType.getAllValues();
    }

    /**
     * @param <T>
     *        axiom type
     * @param axiomType
     *        axiom type to count
     * @return axiom count
     */
    public <T extends OWLAxiom> int getAxiomCount(AxiomType<T> axiomType) {
        if (!axiomsByType.isInitialized()) {
            return 0;
        }
        return Iterables.size(axiomsByType.getValues(axiomType));
    }

    /**
     * @return logical axioms
     */
    @Nonnull
    public Set<OWLLogicalAxiom> getLogicalAxioms() {
        Set<OWLLogicalAxiom> axioms = createLinkedSet();
        for (AxiomType<?> type : AXIOM_TYPES) {
            if (type.isLogical()) {
                for (OWLAxiom ax : (Collection<OWLAxiom>) axiomsByType.getValues(type)) {
                    axioms.add((OWLLogicalAxiom) ax);
                }
            }
        }
        return axioms;
    }

    /**
     * @return logical axioms count
     */
    public int getLogicalAxiomCount() {
        int count = 0;
        for (AxiomType<?> type : AXIOM_TYPES) {
            if (type.isLogical()) {
                count += Iterables.size(axiomsByType.getValues(type));
            }
        }
        return count;
    }

    /**
     * @return copy of GCI axioms
     */
    @Nonnull
    public Set<OWLClassAxiom> getGeneralClassAxioms() {
        return generalClassAxioms.copy();
    }

    /**
     * @param ax
     *        GCI axiom to add
     * @return true if axiom added
     */
    public boolean addGeneralClassAxioms(OWLClassAxiom ax) {
        return generalClassAxioms.add(ax);
    }

    /**
     * @param ax
     *        axiom to remove
     * @return true if removed
     */
    public boolean removeGeneralClassAxioms(OWLClassAxiom ax) {
        return generalClassAxioms.remove(ax);
    }

    /**
     * @param ax
     *        axiom to add
     * @return true if added
     */
    public boolean addPropertyChainSubPropertyAxioms(OWLSubPropertyChainOfAxiom ax) {
        return propertyChainSubPropertyAxioms.add(ax);
    }

    /**
     * @param ax
     *        axiom to remove
     * @return true if removed
     */
    public boolean removePropertyChainSubPropertyAxioms(OWLSubPropertyChainOfAxiom ax) {
        return propertyChainSubPropertyAxioms.remove(ax);
    }

    /**
     * @return map of axioms by type
     */
    @Nonnull
    public MapPointer<AxiomType<?>, OWLAxiom> getAxiomsByType() {
        return axiomsByType;
    }

    class AddAxiomVisitor extends OWLAxiomVisitorAdapter implements Serializable {

        private static final long serialVersionUID = 40000L;

        @Override
        public void visit(@Nonnull OWLSubClassOfAxiom axiom) {
            if (!axiom.getSubClass().isAnonymous()) {
                OWLClass subClass = (OWLClass) axiom.getSubClass();
                subClassAxiomsBySubPosition.put(subClass, axiom);
                classAxiomsByClass.put(subClass, axiom);
            } else {
                addGeneralClassAxioms(axiom);
            }
            if (!axiom.getSuperClass().isAnonymous()) {
                subClassAxiomsBySuperPosition.put((OWLClass) axiom.getSuperClass(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
            negativeObjectPropertyAssertionAxiomsByIndividual.put(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
            asymmetricPropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
            reflexivePropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDisjointClassesAxiom axiom) {
            boolean allAnon = true;
            // Index against each named class in the axiom
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    OWLClass cls = (OWLClass) desc;
                    disjointClassesAxiomsByClass.put(cls, axiom);
                    classAxiomsByClass.put(cls, axiom);
                    allAnon = false;
                }
            }
            if (allAnon) {
                addGeneralClassAxioms(axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
            dataPropertyDomainAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
            if (axiom.getProperty() instanceof OWLObjectProperty) {
                objectPropertyDomainAxiomsByProperty.put(axiom.getProperty(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                equivalentObjectPropertyAxiomsByProperty.put(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
            inversePropertyAxiomsByProperty.put(axiom.getFirstProperty(), axiom);
            inversePropertyAxiomsByProperty.put(axiom.getSecondProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
            negativeDataPropertyAssertionAxiomsByIndividual.put(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                differentIndividualsAxiomsByIndividual.put(ind, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                disjointDataPropertyAxiomsByProperty.put(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                disjointObjectPropertyAxiomsByProperty.put(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
            objectPropertyRangeAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
            objectPropertyAssertionsByIndividual.put(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
            functionalObjectPropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
            objectSubPropertyAxiomsBySubPosition.put(axiom.getSubProperty(), axiom);
            objectSubPropertyAxiomsBySuperPosition.put(axiom.getSuperProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDisjointUnionAxiom axiom) {
            disjointUnionAxiomsByClass.put(axiom.getOWLClass(), axiom);
            classAxiomsByClass.put(axiom.getOWLClass(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDeclarationAxiom axiom) {
            declarationsByEntity.put(axiom.getEntity(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
            annotationAssertionAxiomsBySubject.put(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLHasKeyAxiom axiom) {
            if (!axiom.getClassExpression().isAnonymous()) {
                hasKeyAxiomsByClass.put(axiom.getClassExpression().asOWLClass(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
            symmetricPropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
            dataPropertyRangeAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
            functionalDataPropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                equivalentDataPropertyAxiomsByProperty.put(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLClassAssertionAxiom axiom) {
            classAssertionAxiomsByIndividual.put(axiom.getIndividual(), axiom);
            if (!axiom.getClassExpression().isAnonymous()) {
                classAssertionAxiomsByClass.put(axiom.getClassExpression(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
            boolean allAnon = true;
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    equivalentClassesAxiomsByClass.put((OWLClass) desc, axiom);
                    classAxiomsByClass.put((OWLClass) desc, axiom);
                    allAnon = false;
                }
            }
            if (allAnon) {
                addGeneralClassAxioms(axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
            dataPropertyAssertionsByIndividual.put(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
            transitivePropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
            irreflexivePropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
            dataSubPropertyAxiomsBySubPosition.put(axiom.getSubProperty(), axiom);
            dataSubPropertyAxiomsBySuperPosition.put(axiom.getSuperProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
            inverseFunctionalPropertyAxiomsByProperty.put(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLSameIndividualAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                sameIndividualsAxiomsByIndividual.put(ind, axiom);
            }
        }

        @Override
        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            addPropertyChainSubPropertyAxioms(axiom);
        }
    }

    class RemoveAxiomVisitor extends OWLAxiomVisitorAdapter implements Serializable {

        private static final long serialVersionUID = 40000L;

        @Override
        public void visit(@Nonnull OWLSubClassOfAxiom axiom) {
            if (!axiom.getSubClass().isAnonymous()) {
                OWLClass subClass = (OWLClass) axiom.getSubClass();
                subClassAxiomsBySubPosition.remove(subClass, axiom);
                classAxiomsByClass.remove(subClass, axiom);
            } else {
                removeGeneralClassAxioms(axiom);
            }
            if (!axiom.getSuperClass().isAnonymous()) {
                subClassAxiomsBySuperPosition.remove(axiom.getSuperClass().asOWLClass(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLNegativeObjectPropertyAssertionAxiom axiom) {
            negativeObjectPropertyAssertionAxiomsByIndividual.remove(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLAsymmetricObjectPropertyAxiom axiom) {
            asymmetricPropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLReflexiveObjectPropertyAxiom axiom) {
            reflexivePropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDisjointClassesAxiom axiom) {
            boolean allAnon = true;
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    OWLClass cls = (OWLClass) desc;
                    disjointClassesAxiomsByClass.remove(cls, axiom);
                    classAxiomsByClass.remove(cls, axiom);
                    allAnon = false;
                }
            }
            if (allAnon) {
                removeGeneralClassAxioms(axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDataPropertyDomainAxiom axiom) {
            dataPropertyDomainAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLObjectPropertyDomainAxiom axiom) {
            if (axiom.getProperty() instanceof OWLObjectProperty) {
                objectPropertyDomainAxiomsByProperty.remove(axiom.getProperty(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLEquivalentObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                equivalentObjectPropertyAxiomsByProperty.remove(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLInverseObjectPropertiesAxiom axiom) {
            inversePropertyAxiomsByProperty.remove(axiom.getFirstProperty(), axiom);
            inversePropertyAxiomsByProperty.remove(axiom.getSecondProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLNegativeDataPropertyAssertionAxiom axiom) {
            negativeDataPropertyAssertionAxiomsByIndividual.remove(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDifferentIndividualsAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                differentIndividualsAxiomsByIndividual.remove(ind, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDisjointDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                disjointDataPropertyAxiomsByProperty.remove(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDisjointObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                disjointObjectPropertyAxiomsByProperty.remove(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
            objectPropertyRangeAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLObjectPropertyAssertionAxiom axiom) {
            objectPropertyAssertionsByIndividual.remove(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLFunctionalObjectPropertyAxiom axiom) {
            functionalObjectPropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLSubObjectPropertyOfAxiom axiom) {
            objectSubPropertyAxiomsBySubPosition.remove(axiom.getSubProperty(), axiom);
            objectSubPropertyAxiomsBySuperPosition.remove(axiom.getSuperProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDisjointUnionAxiom axiom) {
            disjointUnionAxiomsByClass.remove(axiom.getOWLClass(), axiom);
            classAxiomsByClass.remove(axiom.getOWLClass(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDeclarationAxiom axiom) {
            declarationsByEntity.remove(axiom.getEntity(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLAnnotationAssertionAxiom axiom) {
            annotationAssertionAxiomsBySubject.remove(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLHasKeyAxiom axiom) {
            if (!axiom.getClassExpression().isAnonymous()) {
                hasKeyAxiomsByClass.remove(axiom.getClassExpression().asOWLClass(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLSymmetricObjectPropertyAxiom axiom) {
            symmetricPropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
            dataPropertyRangeAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLFunctionalDataPropertyAxiom axiom) {
            functionalDataPropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLEquivalentDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                equivalentDataPropertyAxiomsByProperty.remove(prop, axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLClassAssertionAxiom axiom) {
            classAssertionAxiomsByIndividual.remove(axiom.getIndividual(), axiom);
            if (!axiom.getClassExpression().isAnonymous()) {
                classAssertionAxiomsByClass.remove(axiom.getClassExpression(), axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLEquivalentClassesAxiom axiom) {
            boolean allAnon = true;
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    equivalentClassesAxiomsByClass.remove((OWLClass) desc, axiom);
                    classAxiomsByClass.remove((OWLClass) desc, axiom);
                    allAnon = false;
                }
            }
            if (allAnon) {
                removeGeneralClassAxioms(axiom);
            }
        }

        @Override
        public void visit(@Nonnull OWLDataPropertyAssertionAxiom axiom) {
            dataPropertyAssertionsByIndividual.remove(axiom.getSubject(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLTransitiveObjectPropertyAxiom axiom) {
            transitivePropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLIrreflexiveObjectPropertyAxiom axiom) {
            irreflexivePropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLSubDataPropertyOfAxiom axiom) {
            dataSubPropertyAxiomsBySubPosition.remove(axiom.getSubProperty(), axiom);
            dataSubPropertyAxiomsBySuperPosition.remove(axiom.getSuperProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLInverseFunctionalObjectPropertyAxiom axiom) {
            inverseFunctionalPropertyAxiomsByProperty.remove(axiom.getProperty(), axiom);
        }

        @Override
        public void visit(@Nonnull OWLSameIndividualAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                sameIndividualsAxiomsByIndividual.remove(ind, axiom);
            }
        }

        @Override
        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            removePropertyChainSubPropertyAxioms(axiom);
        }
    }

    @Nonnull
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("Internals{(first 20 axioms) ");
        for (OWLAxiom ax : Iterables.limit(axiomsByType.getAllValues(), 20)) {
            b.append(ax).append('\n');
        }
        b.append('}');
        return b.toString();
    }

    /**
     * @param entity
     *        entity to check
     * @return true if reference is contained
     */
    public boolean containsReference(@Nonnull OWLEntity entity) {
        return entity.accept(refChecker);
    }

    /**
     * @param owlEntity
     *        entity to describe
     * @return referencing axioms
     */
    @Nonnull
    public Iterable<OWLAxiom> getReferencingAxioms(@Nonnull OWLEntity owlEntity) {
        return owlEntity.accept(refAxiomsCollector);
    }

    private class ReferencedAxiomsCollector implements OWLEntityVisitorEx<Iterable<OWLAxiom>>, Serializable {

        private static final long serialVersionUID = 40000L;

        ReferencedAxiomsCollector() {}

        @Override
        public Iterable<OWLAxiom> visit(OWLClass cls) {
            return owlClassReferences.getValues(cls);
        }

        @Override
        public Iterable<OWLAxiom> visit(OWLObjectProperty property) {
            return owlObjectPropertyReferences.getValues(property);
        }

        @Override
        public Iterable<OWLAxiom> visit(OWLDataProperty property) {
            return owlDataPropertyReferences.getValues(property);
        }

        @Override
        public Iterable<OWLAxiom> visit(OWLNamedIndividual individual) {
            return owlIndividualReferences.getValues(individual);
        }

        @Override
        public Iterable<OWLAxiom> visit(OWLDatatype datatype) {
            return owlDatatypeReferences.getValues(datatype);
        }

        @Override
        public Iterable<OWLAxiom> visit(OWLAnnotationProperty property) {
            return owlAnnotationPropertyReferences.getValues(property);
        }
    }
}
