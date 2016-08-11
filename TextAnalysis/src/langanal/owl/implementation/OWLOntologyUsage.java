package langanal.owl.implementation;

import org.semanticweb.owlapi.model.OWLClass;

public class OWLOntologyUsage {
	
	public static Base B = new Base("");
	private static OWLFinder OF = new OWLFinder();
	
	/**
	 * Finds the degree of separation between two words in ontology
	 * @param firstWord to find
	 * @param secondWord to find
	 * @return degrees of separation or -1 if any of the words are not found
	 */
	public static int degreesOfSeparation(String firstWord, String secondWord){
		OWLClass firstClass = B.getOWLClass(firstWord);
		OWLClass secondClass = B.getOWLClass(secondWord);
		
		if(firstClass == null){
			System.err.println("Error: Word \"" + firstWord + "\" was not found in the ontology!");
			return -1;
		}
		if(secondClass == null){
			System.err.println("Error: Word \"" + secondWord + "\" was not found in the ontology!");
			return -1;
		}
		//Removes 2 for the starting nodes
		return OF.findPath(firstClass, secondClass).size()-2;
		
	
		
	}
}