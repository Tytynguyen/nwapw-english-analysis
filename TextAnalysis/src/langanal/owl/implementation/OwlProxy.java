package langanal.owl.implementation;

import java.io.File;

import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.*;
import uk.ac.manchester.cs.owl.owlapi.*;

/**
 * 
 * @author Jonathan Edelman
 *
 */
public class OWLProxy {

	
	File sumoXML = new File("TextAnalysis/src/langanal/owl/implementation/SUMO.owl");
   
	//cast should be fine since OWLOntologyManagerImpl implements OWLOntologyManager
	private OWLOntologyManagerImpl manager = (OWLOntologyManagerImpl) OWLManager.createOWLOntologyManager();
    private OWLOntology ontology;

    /**
     * 
     */
    public OWLProxy() {
    	//must handle said exception
    	try {
    		manager.loadOntologyFromOntologyDocument(sumoXML);
    	} catch (OWLOntologyCreationException e) {
    		System.err.println(e);
    	}
    }
    /**
     * 
     * 
     * @return
     */
    public String walkOntology() {
    	return null;
    }
}
