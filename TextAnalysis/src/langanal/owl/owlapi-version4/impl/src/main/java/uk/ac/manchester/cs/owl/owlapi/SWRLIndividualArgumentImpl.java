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

import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.SWRLIndividualArgument;
import org.semanticweb.owlapi.model.SWRLObjectVisitor;
import org.semanticweb.owlapi.model.SWRLObjectVisitorEx;
import org.semanticweb.owlapi.util.OWLObjectTypeIndexProvider;

import java.util.Set;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class SWRLIndividualArgumentImpl extends
        OWLObjectImplWithoutEntityAndAnonCaching implements
        SWRLIndividualArgument {

    private static final long serialVersionUID = 40000L;
    @Nonnull
    private final OWLIndividual individual;

    @Override
    protected int index() {
        return OWLObjectTypeIndexProvider.RULE_OBJECT_TYPE_INDEX_BASE + 7;
    }

    /**
     * @param individual
     *        individual for argument
     */
    public SWRLIndividualArgumentImpl(@Nonnull OWLIndividual individual) {
        this.individual = checkNotNull(individual, "individual cannot be null");
    }

    @Override
    public void addSignatureEntitiesToSet(Set<OWLEntity> entities) {
        addSignatureEntitiesToSetForValue(entities, individual);
    }

    @Override
    public void addAnonymousIndividualsToSet(Set<OWLAnonymousIndividual> anons) {
        addAnonymousIndividualsToSetForValue(anons, individual);
    }

    @Override
    public OWLIndividual getIndividual() {
        return individual;
    }

    @Override
    public void accept(@Nonnull OWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(SWRLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(SWRLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SWRLIndividualArgument)) {
            return false;
        }
        SWRLIndividualArgument other = (SWRLIndividualArgument) obj;
        return other.getIndividual().equals(getIndividual());
    }

    @Override
    protected int compareObjectOfSameType(OWLObject object) {
        return individual.compareTo(((SWRLIndividualArgument) object)
                .getIndividual());
    }
}
