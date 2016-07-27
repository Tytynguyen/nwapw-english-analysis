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

import static org.semanticweb.owlapi.model.parameters.ChangeApplied.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.ChangeApplied;

import com.google.inject.assistedinject.Assisted;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health
 *         Informatics Group
 * @since 2.0.0
 */
public class OWLOntologyImpl extends OWLImmutableOntologyImpl implements
    OWLMutableOntology, Serializable {

    private static final long serialVersionUID = 40000L;

    /**
     * @param manager
     *        ontology manager
     * @param ontologyID
     *        ontology id
     */
    @Inject
    public OWLOntologyImpl(@Nonnull OWLOntologyManager manager,
        @Nonnull @Assisted OWLOntologyID ontologyID) {
        super(manager, ontologyID);
    }

    @Override
    public ChangeApplied applyChange(@Nonnull OWLOntologyChange change) {
        OWLOntologyChangeFilter changeFilter = new OWLOntologyChangeFilter();
        return change.accept(changeFilter);
    }

    @Nonnull
    @Override
    public ChangeApplied applyChanges(
        @Nonnull List<? extends OWLOntologyChange> changes) {
        ChangeApplied appliedChanges = SUCCESSFULLY;
        OWLOntologyChangeFilter changeFilter = new OWLOntologyChangeFilter();
        for (OWLOntologyChange change : changes) {
            ChangeApplied result = change.accept(changeFilter);
            if (appliedChanges == SUCCESSFULLY) {
                // overwrite only if appliedChanges is still successful. If one
                // change has been unsuccessful, we want to preserve that
                // information
                appliedChanges = result;
            }
        }
        return appliedChanges;
    }

    @Override
    public ChangeApplied addAxiom(OWLAxiom axiom) {
        return getOWLOntologyManager().addAxiom(this, axiom);
    }

    @Override
    public ChangeApplied addAxioms(Set<? extends OWLAxiom> axioms) {
        return getOWLOntologyManager().addAxioms(this, axioms);
    }

    protected class OWLOntologyChangeFilter implements
        OWLOntologyChangeVisitorEx<ChangeApplied>, Serializable {

        private static final long serialVersionUID = 40000L;

        @Override
        public ChangeApplied visit(@Nonnull RemoveAxiom change) {
            if (ints.removeAxiom(change.getAxiom())) {
                return SUCCESSFULLY;
            }
            return NO_OPERATION;
        }

        @Override
        public ChangeApplied visit(@Nonnull SetOntologyID change) {
            OWLOntologyID id = change.getNewOntologyID();
            if (!id.equals(ontologyID)) {
                ontologyID = id;
                return SUCCESSFULLY;
            }
            return NO_OPERATION;
        }

        @Override
        public ChangeApplied visit(@Nonnull AddAxiom change) {
            if (ints.addAxiom(change.getAxiom())) {
                return SUCCESSFULLY;
            }
            return NO_OPERATION;
        }

        @Override
        public ChangeApplied visit(@Nonnull AddImport change) {
            // TODO change this to be done inside
            if (ints.addImportsDeclaration(change.getImportDeclaration())) {
                return SUCCESSFULLY;
            }
            return NO_OPERATION;
        }

        @Override
        public ChangeApplied visit(@Nonnull RemoveImport change) {
            if (ints.removeImportsDeclaration(change.getImportDeclaration())) {
                return SUCCESSFULLY;
            }
            return NO_OPERATION;
        }

        @Override
        public ChangeApplied visit(@Nonnull AddOntologyAnnotation change) {
            if (ints.addOntologyAnnotation(change.getAnnotation())) {
                return SUCCESSFULLY;
            }
            return NO_OPERATION;
        }

        @Override
        public ChangeApplied visit(@Nonnull RemoveOntologyAnnotation change) {
            if (ints.removeOntologyAnnotation(change.getAnnotation())) {
                return SUCCESSFULLY;
            }
            return NO_OPERATION;
        }
    }
}
