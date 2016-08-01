package langanal.owl.implementation;

import java.io.File;
import java.util.*;

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

	
	File sumoXML = new File("src/langanal/owl/implementation/SUMO.owl");
   
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
    
    //FIXME return type shouldn't be Strings as they're not a unique identifiers
    /**
     * 
     * 
     * @return
     */
    private ArrayList<String> walkOntology() {
    	//create a new set with the only element being the main ontology
    	//there's no need to use multiple sets here
    	HashSet<OWLOntology> ontologySet = new HashSet<OWLOntology>();
    	ontologySet.add(ontology);
    	
    	//create a new walker using ontologySet
    	OWLOntologyWalker walker = new OWLOntologyWalker(ontologySet);
    	
    	
    	return null;
    }

}
