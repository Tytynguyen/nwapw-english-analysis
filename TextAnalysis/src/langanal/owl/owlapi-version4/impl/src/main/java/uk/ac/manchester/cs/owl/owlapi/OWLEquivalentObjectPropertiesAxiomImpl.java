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

import java.util.*;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.*;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class OWLEquivalentObjectPropertiesAxiomImpl extends OWLNaryPropertyAxiomImpl<OWLObjectPropertyExpression>
    implements OWLEquivalentObjectPropertiesAxiom {

    private static final long serialVersionUID = 40000L;

    /**
     * @param properties
     *        properties
     * @param annotations
     *        annotations
     */
    public OWLEquivalentObjectPropertiesAxiomImpl(@Nonnull Set<? extends OWLObjectPropertyExpression> properties,
        @Nonnull Collection<? extends OWLAnnotation> annotations) {
        super(properties, annotations);
    }

    @Override
    public OWLEquivalentObjectPropertiesAxiom getAxiomWithoutAnnotations() {
        if (!isAnnotated()) {
            return this;
        }
        return new OWLEquivalentObjectPropertiesAxiomImpl(getProperties(), NO_ANNOTATIONS);
    }

    @Override
    public OWLEquivalentObjectPropertiesAxiom getAnnotatedAxiom(Set<OWLAnnotation> annotations) {
        return new OWLEquivalentObjectPropertiesAxiomImpl(getProperties(), mergeAnnos(annotations));
    }

    @Override
    public Set<OWLEquivalentObjectPropertiesAxiom> asPairwiseAxioms() {
        Set<OWLEquivalentObjectPropertiesAxiom> result = new HashSet<>();
        List<OWLObjectPropertyExpression> list = new ArrayList<>(getProperties());
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                result.add(new OWLEquivalentObjectPropertiesAxiomImpl(new HashSet<>(Arrays.asList(list.get(i), list.get(
                    j))), NO_ANNOTATIONS));
            }
        }
        return result;
    }

    @Override
    public Set<OWLEquivalentObjectPropertiesAxiom> splitToAnnotatedPairs() {
        List<OWLObjectPropertyExpression> ops = new ArrayList<>(getProperties());
        if (ops.size() == 2) {
            return Collections.<OWLEquivalentObjectPropertiesAxiom> singleton(this);
        }
        Set<OWLEquivalentObjectPropertiesAxiom> result = new HashSet<>();
        for (int i = 0; i < ops.size() - 1; i++) {
            OWLObjectPropertyExpression indI = ops.get(i);
            OWLObjectPropertyExpression indJ = ops.get(i + 1);
            result.add(new OWLEquivalentObjectPropertiesAxiomImpl(new HashSet<>(Arrays.asList(indI, indJ)),
                getAnnotations()));
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        return obj instanceof OWLEquivalentObjectPropertiesAxiom;
    }

    @Override
    public void accept(OWLAxiomVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void accept(OWLObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLAxiomVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public <O> O accept(OWLObjectVisitorEx<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public AxiomType<?> getAxiomType() {
        return AxiomType.EQUIVALENT_OBJECT_PROPERTIES;
    }

    @Override
    public Set<OWLSubObjectPropertyOfAxiom> asSubObjectPropertyOfAxioms() {
        Set<OWLSubObjectPropertyOfAxiom> result = new HashSet<>();
        List<OWLObjectPropertyExpression> props = new ArrayList<>(getProperties());
        for (int i = 0; i < props.size(); i++) {
            for (int j = 0; j < props.size(); j++) {
                if (i != j) {
                    result.add(new OWLSubObjectPropertyOfAxiomImpl(props.get(i), props.get(j), NO_ANNOTATIONS));
                }
            }
        }
        return result;
    }
}
