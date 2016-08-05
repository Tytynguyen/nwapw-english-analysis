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

	private File sumoXML = new File("lib/SUMO.owl");

   
	//cast should be fine since OWLOntologyManagerImpl implements OWLOntologyManager
	private OWLOntologyManagerImpl manager = (OWLOntologyManagerImpl) OWLManager.createOWLOntologyManager();
    private OWLOntology ontology;

    /**
     *
	 * Constructs a basic OWLProxy and loads the SUMO.owl ontology
	 *
     */
    public OWLProxy() {
        //System.out.println("Started constructor");

    	//must handle said exception
    	try {
    		ontology = manager.loadOntologyFromOntologyDocument(sumoXML);
    	} catch (OWLOntologyCreationException e) {
    		System.err.println(e);
    	}

    	//System.out.println("Finish loading");

    }

    /**
     * 
     * Walks the ontology searching for a given item
	 *
	 * @param toFind has the word that needs to be found
     * @return The corresponding class, or null if couldn't find the word
     */
    private OWLClass walkOntology(String toFind) {

		//for every class in the ontology
		for (OWLClass currentClass:ontology.getClassesInSignature()) {

			//currentWord is the super short lower case version of the full location of the currentClass
			String currentWord = currentClass.getIRI().getShortForm();

			//make the word lowercase
			currentWord = currentWord.toLowerCase();

			//System.out.println(currentWord);

			//if it's the word we're looking for
			if (currentWord.equals(toFind)) {
				//System.out.println("Found word");
				//call step up to find the parents
				return currentClass;
			}
		}

		return null;

	}

	/**
	 *
	 * @param firstClass to path between
	 * @param secondClass to path between
	 * @return The shortest path as a list of OWLClasses including the roots
	 */
	private List<OWLClass> findPath(OWLClass firstClass, OWLClass secondClass) {

	}

    /**
	 *
	 * @param firstWord to compare
	 * @param secondWord to compare
	 * @return int based on the number of axioms needed to traverse from one word to another, or -1 if no commonality is found
     */
    public int degreesOfSeparation(String firstWord, String secondWord) {

    	//starts at -2 to negate the two roots in the counting
    	int degreesOfSeparation = -2;

    	//gets the classes
		OWLClass firstClass = walkOntology(firstWord);
		OWLClass secondClass = walkOntology(secondWord);

		//if either is empty (no word found), return -1
		if (firstClass == null | secondClass == null) {
			return -1;
		}

		//System.out.println("––––––––––––––––––––––––––––––––––––––––––––––");

		//determines degrees of separation
		for (OWLClass currentClass:findPath(firstClass, secondClass)) {
			degreesOfSeparation++;
		}

	}

}
