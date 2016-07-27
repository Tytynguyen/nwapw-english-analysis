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

import static org.semanticweb.owlapi.util.CollectionFactory.*;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNaryIndividualAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLPairwiseVisitor;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public abstract class OWLNaryIndividualAxiomImpl extends
        OWLIndividualAxiomImplWithoutEntityAndAnonCaching implements
        OWLNaryIndividualAxiom {

    private static final long serialVersionUID = 40000L;
    @Nonnull
    private final List<OWLIndividual> individuals;

    /**
     * @param individuals
     *        individuals
     * @param annotations
     *        annotations on the axiom
     */
    @SuppressWarnings("unchecked")
    public OWLNaryIndividualAxiomImpl(
            @Nonnull Set<? extends OWLIndividual> individuals,
            @Nonnull Collection<? extends OWLAnnotation> annotations) {
        super(annotations);
        checkNotNull(individuals, "individuals cannot be null");
        this.individuals = (List<OWLIndividual>) sortOptionally(individuals);
    }

    @Override
    public void addSignatureEntitiesToSet(Set<OWLEntity> entities) {
        for (OWLIndividual individual : individuals) {
            if (individual.isNamed()) {
                entities.add(individual.asOWLNamedIndividual());
            }
        }
    }

    @Override
    public void addAnonymousIndividualsToSet(Set<OWLAnonymousIndividual> anons) {
        for (OWLIndividual individual : individuals) {
            if (individual.isAnonymous()) {
                anons.add(individual.asOWLAnonymousIndividual());
            }
        }
    }

    @Override
    public Set<OWLIndividual> getIndividuals() {
        return getCopyOnRequestSetFromImmutableCollection(individuals);
    }

    @Override
    public List<OWLIndividual> getIndividualsAsList() {
        return new ArrayList<>(individuals);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OWLNaryIndividualAxiom)) {
            return false;
        }
        if (obj instanceof OWLNaryIndividualAxiomImpl) {
            return individuals
                    .equals(((OWLNaryIndividualAxiomImpl) obj).individuals);
        }
        return compareObjectOfSameType((OWLNaryIndividualAxiom) obj) == 0;
    }

    @Override
    protected int compareObjectOfSameType(OWLObject object) {
        return compareSets(individuals,
                ((OWLNaryIndividualAxiom) object).getIndividuals());
    }

    @Override
    public <T> Collection<T> walkPairwise(
            OWLPairwiseVisitor<T, OWLIndividual> visitor) {
        List<T> l = new ArrayList<>();
        for (int i = 0; i < individuals.size() - 1; i++) {
            for (int j = i + 1; j < individuals.size(); j++) {
                T t = visitor.visit(individuals.get(i), individuals.get(j));
                if (t != null) {
                    l.add(t);
                }
            }
        }
        return l;
    }
}
