package langanal.owl.implementation;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;

public class Base {
	private File sumoXML = new File("lib/SUMO.owl");
	private OWLOntology ontology;
	//Stores all of the owlclasses in the ontology by it's identifier(Word) 
	//Used for easy access
	private HashMap<String,OWLClass> map;	

	Base(String filename) {
		this.map = new HashMap<String,OWLClass>();
		
		try {
			OWLOntologyManagerImpl OWLM = (OWLOntologyManagerImpl) OWLManager.createOWLOntologyManager();

			//allows for custom ontology loading
			if(!filename.equals("")){
				this.ontology = OWLM.loadOntologyFromOntologyDocument(new File("lib/" + filename));
			}else{
				this.ontology = OWLM.loadOntologyFromOntologyDocument(sumoXML);
			}
			
			//Init map
			for(OWLClass curClass : ontology.getClassesInSignature()){
				this.map.put(curClass.getIRI().getShortForm().toLowerCase(), curClass);
			}
			
		} catch (OWLOntologyCreationException e) {
			System.err.println("Could not load XML file!");
			e.printStackTrace();
		}
	}

	/**
	 * Finds and returns the OWLClass with the same identifier as the input
	 * @param word String to be searched for
	 * @return OWLClass of the word
	 */
	public OWLClass getOWLClass(String word){
		return map.get(word);
	}
	
	/**
	 * Gets and returns the parents of the specified class
	 * @param child OWLClass of child
	 * @return LinkedList<OWLClass> of parents
	 */
	public LinkedList<OWLClass> getParents(OWLClass child){
		LinkedList<OWLClass> returnList = new LinkedList<OWLClass>();

		for(OWLSubClassOfAxiom curAxiom : ontology.getSubClassAxiomsForSubClass(child)) {
			returnList.add(curAxiom.getSuperClass().asOWLClass());
		}
		return returnList;
	}
}
