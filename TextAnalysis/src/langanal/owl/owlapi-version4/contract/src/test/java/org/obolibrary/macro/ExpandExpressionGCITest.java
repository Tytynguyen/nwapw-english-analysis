package org.obolibrary.macro;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.obolibrary.obo2owl.OboFormatTestBasics;
import org.semanticweb.owlapi.model.*;

@SuppressWarnings("javadoc")
public class ExpandExpressionGCITest extends OboFormatTestBasics {

    @Test
    public void testExpand() {
        OWLOntology ontology = convert(parseOBOFile("no_overlap.obo"));
        MacroExpansionGCIVisitor mev = new MacroExpansionGCIVisitor(m, ontology, false);
        OWLOntology gciOntology = mev.createGCIOntology();
        int axiomCount = gciOntology.getAxiomCount();
        assertTrue(axiomCount > 0);
        OWLClass cls = df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/TEST_2"));
        Set<OWLDisjointClassesAxiom> dcas = gciOntology.getDisjointClassesAxioms(cls);
        assertEquals(1, dcas.size());
        Set<OWLEquivalentClassesAxiom> equivalentClassesAxioms = gciOntology.getAxioms(AxiomType.EQUIVALENT_CLASSES);
        // assertEquals(2, equivalentClassesAxioms.size());
        for (OWLEquivalentClassesAxiom eca : equivalentClassesAxioms) {
            Set<OWLClassExpression> ces = eca.getClassExpressions();
            OWLClass clst4 = df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/TEST_4"));
            OWLObjectPropertyExpression p = df.getOWLObjectProperty(IRI.create(
                "http://purl.obolibrary.org/obo/RO_0002104"));
            OWLClassExpression cet4 = df.getOWLObjectSomeValuesFrom(p, clst4);
            OWLClass clst5 = df.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/TEST_5"));
            OWLClassExpression cet5 = df.getOWLObjectSomeValuesFrom(p, clst5);
            if (ces.contains(cet4)) {
                ces.remove(cet4);
                OWLClassExpression clst4ex = ces.iterator().next();
                assertEquals(
                    "ObjectSomeValuesFrom(<http://purl.obolibrary.org/obo/BFO_0000051> ObjectIntersectionOf(<http://purl.obolibrary.org/obo/GO_0005886> ObjectSomeValuesFrom(<http://purl.obolibrary.org/obo/BFO_0000051> <http://purl.obolibrary.org/obo/TEST_4>)))",
                    clst4ex.toString());
            } else if (ces.contains(cet5)) {
                ces.remove(cet5);
                OWLClassExpression clst5ex = ces.iterator().next();
                assertEquals(
                    "ObjectSomeValuesFrom(<http://purl.obolibrary.org/obo/BFO_0000051> ObjectIntersectionOf(<http://purl.obolibrary.org/obo/GO_0005886> ObjectSomeValuesFrom(<http://purl.obolibrary.org/obo/BFO_0000051> <http://purl.obolibrary.org/obo/TEST_5>)))",
                    clst5ex.toString());
            } else {
                fail("Unknown OWLEquivalentClassesAxiom: " + eca);
            }
        }
    }
}
