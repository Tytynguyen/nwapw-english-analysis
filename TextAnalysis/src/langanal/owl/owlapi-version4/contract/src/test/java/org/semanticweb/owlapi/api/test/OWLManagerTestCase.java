package org.semanticweb.owlapi.api.test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.api.test.baseclasses.TestBase;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.ConcurrentOWLOntologyImpl;

/**
 * Matthew Horridge Stanford Center for Biomedical Informatics Research 10/04/15
 */
@SuppressWarnings("javadoc")
public class OWLManagerTestCase extends TestBase {

    private OWLOntology ontology;
    OWLOntologyManager manager = OWLManager.createConcurrentOWLOntologyManager();

    @Before
    public void setUp() throws OWLOntologyCreationException {
        ontology = manager.createOntology();
    }

    @Test
    public void shouldCreateOntologyWithCorrectManager() {
        assertThat(ontology.getOWLOntologyManager(), is(manager));
    }

    @Test
    public void shouldCreateConcurrentOntologyByDefault() {
        assertThat(ontology, is(instanceOf(ConcurrentOWLOntologyImpl.class)));
    }

    @Test
    public void shouldShareReadWriteLock() throws Exception {
        // Nasty, but not sure of another way to do this without exposing it in
        // the interface
        Field ontologyLockField = ConcurrentOWLOntologyImpl.class.getDeclaredField("readWriteLock");
        ontologyLockField.setAccessible(true);
        Field ontologyManagerField = OWLOntologyManagerImpl.class.getDeclaredField("readWriteLock");
        ontologyManagerField.setAccessible(true);
        assertThat(ontologyLockField.get(ontology), is(ontologyManagerField.get(manager)));
    }
}
