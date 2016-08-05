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
	 * Uses Dikjstra's-ish algorithm to determine the shortest path between the firstClass and the secondClass
	 *
	 * @param firstClass to path between
	 * @param secondClass to path between
	 * @return The shortest path as a list of OWLClasses including the roots
	 */
	private List<OWLClass> findPath(OWLClass firstClass, OWLClass secondClass) {

		//Node class for storing the necessary data for algorithm
		class Node implements Comparable<Node> {

			private OWLClass myClass;
			private LinkedList<Node> currentPath;
			private LinkedList<Node> parents;

			public Node(OWLClass newMyClass) {
				myClass = newMyClass;

				//get the parents and store them in the field
				for (OWLSubClassOfAxiom currentAxiom:ontology.getSubClassAxiomsForSubClass(this.getOWLClass())) {
					//create a new node from the current parent
					Node currentNewNode = new Node(currentAxiom.getSuperClass().asOWLClass());
					parents.add(currentNewNode);
				}
			}

			public int pathLength() {
				return currentPath.size();
			}

			public LinkedList<Node> currentPath() {
				return currentPath;
			}

			//this is for when we've found the endNode
			public LinkedList<OWLClass> currentPathAsOWLClasses() {
				LinkedList<OWLClass> path = new LinkedList<OWLClass>();
				for (Node currentNode:currentPath) {
					path.add(currentNode.getOWLClass());
				}
				return path;
			}

			public OWLClass getOWLClass() {
				return myClass;
			}

			public int compareTo(Node o) {
				if (pathLength() < o.pathLength()) {
					return -1;
				} else if (pathLength() > o.pathLength()) {
					return 1;
				}
				return 0;
			}
		}

		//each node corresponds to the end of the other's pathing
		Node firstNode = new Node(firstClass);
		Node secondNode = new Node(secondClass);

		//these lists are used to determine if the other pather has already found that node
		//if so they'd have a common parent and we can finish
		LinkedList<Node> firstHasChecked = new LinkedList<Node>();
		LinkedList<Node> secondHasChecked = new LinkedList<Node>();

		PriorityQueue<Node> firstFrontier = new PriorityQueue<Node>();
		PriorityQueue<Node> secondFrontier = new PriorityQueue<Node>();


		firstFrontier.add(firstNode);
		secondFrontier.add(secondNode);

		//while both the of the frontiers are not empty
		while ((!firstFrontier.isEmpty()) | (!secondFrontier.isEmpty())) {

			//remove the first node and get it
			Node currentNode = firstFrontier.remove();

			//all comparisons must use the OWLClass not the node, because the node could be unique while the OWLClass isn't
			//if we've found the end
			if (currentNode.getOWLClass().equals(secondNode.getOWLClass())) {
				//return it's path as a bunch of OWLClasses

				//TODO what to return
			} else {
				for (Node currentHasChecked:secondHasChecked) {
					//if the node has already been checked by second
					if (currentNode.getOWLClass().equals(currentHasChecked.getOWLClass())) {
						//TODO what to return
					}

				}
			}

			//if the end hasn't been found loop through the current node's parents
			for (OWLSubClassOfAxiom currentAxiom:ontology.getSubClassAxiomsForSubClass(currentNode.getOWLClass())) {
				//create a new node from the current parent
				Node currentParent = new Node(currentAxiom.getSuperClass().asOWLClass());
				//if it hasn't been checked already add it to firstHasChecked
				//can't use .contains because it needs to check against OWLClasses not Nodes
				if (firstHasChecked.contains(currentParent.getOWLClass())) {
					currentParent.
				} else {
					firstHasChecked.add(currentParent);
					firstFrontier.add(currentParent)
				}

			}

		}

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

		return degreesOfSeparation;

	}

}
