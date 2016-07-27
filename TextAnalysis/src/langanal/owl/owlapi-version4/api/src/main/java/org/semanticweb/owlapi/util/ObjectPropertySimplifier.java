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
package org.semanticweb.owlapi.util;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.*;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.*;

/**
 * This utility class can be used to obtain an object property expression in its
 * simplest form. Let P be an object property name and PE a property expression,
 * then the simplification is inductively defined as: simp(P) = P simp(inv(P)) =
 * inv(P) simp(inv(inv(PE)) = simp(PE)
 *
 * @author Matthew Horridge, The University Of Manchester, Information
 *         Management Group
 * @since 2.2.0
 * @deprecated Legal Object property expressions should always be irreducible -
 *             the standard specifies that inv : P -&gt; PE , not PE -&gt; PE,
 *             so inv (inv P) is an error . Versions of the OWLAPI prior to
 *             4.1.0 did not check for this.
 */
@Deprecated
public class ObjectPropertySimplifier {

    @Nonnull
    private final OWLDataFactory dataFactory;
    @Nonnull
    private final Simplifier simplifier = new Simplifier();

    /**
     * @param dataFactory
     *        datafactory to use
     */
    public ObjectPropertySimplifier(@Nonnull OWLDataFactory dataFactory) {
        this.dataFactory = checkNotNull(dataFactory,
            "dataFactory cannot be null");
    }

    /**
     * Gets an object property expression in its simplest form.
     * 
     * @param prop
     *        The object property expression to be simplified.
     * @return The simplest form of the object property expression.
     */
    @Nonnull
    public OWLObjectPropertyExpression getSimplified(
        @Nonnull OWLObjectPropertyExpression prop) {
        checkNotNull(prop, "prop cannot be null");
        simplifier.reset();
        prop.accept(simplifier);
        if (simplifier.isInverse()) {
            return dataFactory.getOWLObjectInverseOf(simplifier.getProperty());
        } else {
            return simplifier.getProperty();
        }
    }

    private static class Simplifier implements OWLPropertyExpressionVisitor {

        private OWLObjectProperty p;
        private int depth;

        Simplifier() {}

        public void reset() {
            depth = 0;
            p = null;
        }

        @Nonnull
        public OWLObjectProperty getProperty() {
            return verifyNotNull(p);
        }

        public boolean isInverse() {
            return depth % 2 != 0;
        }

        @Override
        public void visit(OWLObjectProperty property) {
            p = property;
        }

        @Override
        public void visit(OWLObjectInverseOf property) {
            depth++;
            property.getInverse().accept(this);
        }

        @Override
        public void visit(OWLDataProperty property) {}

        @Override
        public void visit(OWLAnnotationProperty property) {}
    }
}
