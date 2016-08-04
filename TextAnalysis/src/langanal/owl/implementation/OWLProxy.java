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
     * @return A List of OWLClasses beginning with the found expression and ending with the root expression, or null if couldn't find the word
     */
    private List<OWLClass> walkOntology(final String toFind) {

		//create a LinkedList of OWLClasses that will be returned
		LinkedList<OWLClassExpression> heirarchy = new LinkedList<OWLClassExpression>();

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
				return stepUp(currentClass);
			}
		}

		return null;

	}

	/**
	 *
	 * Recursive function that finds parents of a given class
	 *
	 * @param lastClass class to find parents of
	 */
	private LinkedList<OWLClass> stepUp(OWLClass lastClass) {

		LinkedList<OWLClass> heirarchy = new LinkedList<OWLClass>();

		heirarchy.add(lastClass);

		//kludgy way of getting the first element
		boolean hasRun = false;
		for (OWLSubClassOfAxiom currentAxiom:ontology.getSubClassAxiomsForSubClass(lastClass.asOWLClass())) {
			if (!hasRun) {
				hasRun = true;
				heirarchy.addAll(stepUp(currentAxiom.getSuperClass().asOWLClass()));
			}
		}
		return heirarchy;
	}

    /**
	 *
	 * @param firstWord to compare
	 * @param secondWord to compare
	 * @return int based on the number of axioms needed to traverse from one word to another, or -1 if no commonality is found
     */
    public int degreesOfSeparation(String firstWord, String secondWord) {


    	//gets the heirarchy
		List<OWLClass> firstHeirarchy = walkOntology(firstWord);
		List<OWLClass> secondHeirarchy = walkOntology(secondWord);

		//if either is empty (no word found), return -1
		if (firstHeirarchy == null | secondHeirarchy == null) {
			return -1;
		}

		//System.out.println("––––––––––––––––––––––––––––––––––––––––––––––");

		//determines degrees of separation

		//find the closest commonality
		//assumes iterator works front to back
		for (OWLClass firstCurrent:firstHeirarchy) {
			for (OWLClass secondCurrent:secondHeirarchy) {
				//if they have a commonality
				if (firstCurrent.equals(secondCurrent)) {
					//number of elements left
					int firstLeft = firstHeirarchy.indexOf(firstCurrent);
					int secondLeft = firstHeirarchy.indexOf(secondCurrent);
					//return the total
					return firstLeft+secondLeft;
				}
			}
		}

		return -1;

	}

}
