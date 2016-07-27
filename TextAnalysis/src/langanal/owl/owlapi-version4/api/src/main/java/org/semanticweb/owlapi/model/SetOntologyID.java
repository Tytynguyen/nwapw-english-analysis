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
package org.semanticweb.owlapi.model;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.change.SetOntologyIDData;
import org.semanticweb.owlapi.util.CollectionFactory;

import com.google.common.base.Optional;

/**
 * @author Matthew Horridge, The University of Manchester, Information
 *         Management Group
 * @since 3.0.0
 */
public class SetOntologyID extends OWLOntologyChange {

    private static final long serialVersionUID = 40000L;
    @Nonnull
    private final OWLOntologyID ontologyID;
    @Nonnull
    private final OWLOntologyID newOntologyID;

    /**
     * Creates a set ontology id change, which will set the ontology id to the
     * new one.
     * 
     * @param ont
     *        The ontology whose id is to be changed
     * @param ontologyID
     *        The ontology ID
     */
    public SetOntologyID(@Nonnull OWLOntology ont, @Nonnull OWLOntologyID ontologyID) {
        super(ont);
        this.ontologyID = checkNotNull(ont.getOntologyID(), "ontology id cannot be null");
        newOntologyID = checkNotNull(ontologyID, "ontology id cannot be null");
    }

    SetOntologyID(@Nonnull OWLOntology ont, @Nonnull OWLOntologyID ontologyID, @Nonnull OWLOntologyID newOntologyID) {
        super(ont);
        this.ontologyID = checkNotNull(ontologyID, "ontology id cannot be null");
        this.newOntologyID = checkNotNull(newOntologyID, "ontology id cannot be null");
    }

    /**
     * Creates a set ontology id change using the ontologyIRI, which will set
     * the ontology id to the new one.
     * 
     * @param ont
     *        The ontology whose id is to be changed
     * @param ontologyIRI
     *        The ontology iri
     */
    public SetOntologyID(@Nonnull OWLOntology ont, @Nonnull IRI ontologyIRI) {
        this(ont, new OWLOntologyID(Optional.of(ontologyIRI), Optional.<IRI> absent()));
    }

    @Override
    public SetOntologyIDData getChangeData() {
        return new SetOntologyIDData(newOntologyID);
    }

    @Override
    public Set<OWLEntity> getSignature() {
        return CollectionFactory.getCopyOnRequestSetFromImmutableCollection(CollectionFactory.<OWLEntity> emptySet());
    }

    @Override
    public boolean isImportChange() {
        return false;
    }

    @Override
    public boolean isAxiomChange() {
        return false;
    }

    @Override
    public boolean isAddAxiom() {
        return false;
    }

    @Override
    public OWLAxiom getAxiom() {
        throw new UnsupportedOperationException("This is an ontology id change, not an axiom change: " + this);
    }

    /**
     * Gets the original ID of the ontology whose URI was changed.
     * 
     * @return The original ID
     */
    @Nonnull
    public OWLOntologyID getOriginalOntologyID() {
        return ontologyID;
    }

    /**
     * @return the new URI - i.e. the URI of the ontology after the change was
     *         applied.
     */
    @Nonnull
    public OWLOntologyID getNewOntologyID() {
        return newOntologyID;
    }

    @Override
    public void accept(OWLOntologyChangeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLOntologyChangeVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.format("SetOntologyID(%s OntologyID(%s))", newOntologyID, ontologyID);
    }

    @Override
    public int hashCode() {
        return 57 + ontologyID.hashCode() + newOntologyID.hashCode() * 3;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SetOntologyID)) {
            return false;
        }
        SetOntologyID change = (SetOntologyID) obj;
        return change.ontologyID.equals(ontologyID) && change.newOntologyID.equals(newOntologyID);
    }

    @Override
    public OWLOntologyChange reverseChange() {
        SetOntologyID setOntologyID = new SetOntologyID(getOntology(), newOntologyID, getOntology().getOntologyID());
        return setOntologyID;
    }
}
