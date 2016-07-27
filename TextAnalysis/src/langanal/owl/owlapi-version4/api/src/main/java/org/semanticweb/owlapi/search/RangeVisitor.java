package org.semanticweb.owlapi.search;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAnnotationPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.util.OWLAxiomVisitorExAdapter;

@SuppressWarnings("unchecked")
class RangeVisitor<C extends OWLObject> extends OWLAxiomVisitorExAdapter<C> {

    @SuppressWarnings("null")
    /**default constructor*/
    public RangeVisitor() {
        super(null);
    }

    @Override
    public C visit(@Nonnull OWLAnnotationPropertyRangeAxiom axiom) {
        return (C) axiom.getRange();
    }

    @Override
    public C visit(@Nonnull OWLDataPropertyRangeAxiom axiom) {
        return (C) axiom.getRange();
    }

    @Override
    public C visit(@Nonnull OWLObjectPropertyRangeAxiom axiom) {
        return (C) axiom.getRange();
    }
}
