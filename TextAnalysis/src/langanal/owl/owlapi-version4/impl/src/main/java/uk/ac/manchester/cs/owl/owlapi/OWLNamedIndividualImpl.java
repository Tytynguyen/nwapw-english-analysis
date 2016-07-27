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

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;

import java.util.Set;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 */
public class OWLNamedIndividualImpl extends OWLIndividualImpl implements
        OWLNamedIndividual {

    private static final long serialVersionUID = 40000L;
    @Nonnull
    private final IRI iri;

    @Override
    public void addSignatureEntitiesToSet(Set<OWLEntity> entities) {
        entities.add(this);
    }

    @Override
    public void addAnonymousIndividualsToSet(Set<OWLAnonymousIndividual> anons) {}

    @Override
    protected int index() {
        return OWLObjectTypeIndexProvider.INDIVIDUAL;
    }

    /**
     * @param iri
     *        the iri
     */
    public OWLNamedIndividualImpl(@Nonnull IRI iri) {
        this.iri = checkNotNull(iri, "iri cannot be null");
    }

    @Override
    public boolean isNamed() {
        return true;
    }

    @Override
    public EntityType<?> getEntityType() {
        return EntityType.NAMED_INDIVIDUAL;
    }

    @Override
    public boolean isType(EntityType<?> entityType) {
        return getEntityType().equals(entityType);
    }

    @Override
    public String toStringID() {
        return iri.toString();
    }

    @Override
    public boolean isOWLNamedIndividual() {
        return true;
    }

    @Override
    public IRI getIRI() {
        return iri;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public OWLNamedIndividual asOWLNamedIndividual() {
        return this;
    }

    @Override
    public OWLAnonymousIndividual asOWLAnonymousIndividual() {
        throw new OWLRuntimeException("Not an anonymous individual");
    }

    @Override
    public OWLAnnotationProperty asOWLAnnotationProperty() {
        throw new OWLRuntimeException("Not an annotation property");
    }

    @Override
    public boolean isOWLAnnotationProperty() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OWLNamedIndividual)) {
            return false;
        }
        IRI otherIRI = ((OWLNamedIndividual) obj).getIRI();
        return otherIRI.equals(iri);
    }

    @Override
    protected int compareObjectOfSameType(OWLObject object) {
        OWLNamedIndividual other = (OWLNamedIndividual) object;
        return iri.compareTo(other.getIRI());
    }

    @Override
    public void accept(OWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(OWLEntityVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLEntityVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(OWLNamedObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLNamedObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public void accept(OWLIndividualVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLIndividualVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public OWLClass asOWLClass() {
        throw new OWLRuntimeException("Not an OWLClass!");
    }

    @Override
    public OWLDataProperty asOWLDataProperty() {
        throw new OWLRuntimeException("Not a data property!");
    }

    @Override
    public OWLDatatype asOWLDatatype() {
        throw new OWLRuntimeException("Not a data type!");
    }

    @Override
    public OWLObjectProperty asOWLObjectProperty() {
        throw new OWLRuntimeException("Not an object property");
    }

    @Override
    public boolean isOWLClass() {
        return false;
    }

    @Override
    public boolean isOWLDataProperty() {
        return false;
    }

    @Override
    public boolean isOWLDatatype() {
        return false;
    }

    @Override
    public boolean isOWLObjectProperty() {
        return false;
    }

    @Override
    public boolean isBuiltIn() {
        return false;
    }
}
