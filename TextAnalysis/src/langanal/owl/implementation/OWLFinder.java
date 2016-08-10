package langanal.owl.implementation;

import java.util.Collections;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.semanticweb.owlapi.model.OWLClass;

public class OWLFinder {
	public class OWLGraphNode implements Comparable<OWLGraphNode> {

		private OWLClass value; //Stores the class
		private LinkedList<OWLGraphNode> pathTo;
		private LinkedList<OWLGraphNode> parents;

		public OWLGraphNode(OWLClass value) {
			this.value = value;
			this.pathTo = new LinkedList<OWLGraphNode>();
			this.parents = new LinkedList<OWLGraphNode>();
		}

		public OWLClass getValue() {	return this.value;	}
		public LinkedList<OWLGraphNode> pathTo() { 	return this.pathTo;	}
		public LinkedList<OWLGraphNode> getParents() {	return this.parents;	}

		/**
		 * Converts the path of OWLGraphNodes to a linked list of OWLClasses
		 * @return LinkedList of OWLClasses in order of pathTo
		 */
		public LinkedList<OWLClass> pathToAsOWL() {
			LinkedList<OWLClass> path = new LinkedList<OWLClass>();
			for (OWLGraphNode currentOWLGraphNode : this.pathTo) {
				path.add(currentOWLGraphNode.getValue());
			}
			return path;
		}

		public int pathLength() {	return pathTo.size();	}

		public int compareTo(OWLGraphNode o) {
			if (this.pathLength() < o.pathLength()) {
				return -1;
			} else if (this.pathLength() > o.pathLength()) {
				return 1;
			}
			return 0;
		}
	}

	/**
	 *
	 * Uses Dikjstra's-ish algorithm to determine the shortest path between the firstClass and the secondClass
	 *
	 * @param firstClass to path between
	 * @param secondClass to path between
	 * @return The shortest path as a list of OWLClasses including the roots
	 */
	public LinkedList<OWLClass> findPath(OWLClass firstClass, OWLClass secondClass) {
		System.out.println("starting pathfinding");
		//Starting node
		OWLGraphNode firstOWLGraphNode = new OWLGraphNode(firstClass);
		firstOWLGraphNode.pathTo.add(firstOWLGraphNode);
		OWLGraphNode secondOWLGraphNode = new OWLGraphNode(secondClass);
		secondOWLGraphNode.pathTo.add(secondOWLGraphNode);

		//these lists are used to determine if the other pather has already found that OWLGraphNode
		//if so they'd have a common parent and we can finish
		LinkedList<OWLGraphNode> firstHasChecked = new LinkedList<OWLGraphNode>();
		firstHasChecked.add(firstOWLGraphNode);
		LinkedList<OWLGraphNode> secondHasChecked = new LinkedList<OWLGraphNode>();
		secondHasChecked.add(secondOWLGraphNode);
		
		PriorityQueue<OWLGraphNode> firstFrontier = new PriorityQueue<OWLGraphNode>();
		PriorityQueue<OWLGraphNode> secondFrontier = new PriorityQueue<OWLGraphNode>();


		firstFrontier.add(firstOWLGraphNode);
		secondFrontier.add(secondOWLGraphNode);

		while (!firstFrontier.isEmpty()&&!secondFrontier.isEmpty()){
			/*
			 *First word 
			 */
			OWLGraphNode checkingFirstNode = firstFrontier.remove();
			System.out.println("First word: " + checkingFirstNode.getValue());

			if(checkingFirstNode.getValue().equals(secondOWLGraphNode.getValue())){	//If the checking node is end node, return its path
				return checkingFirstNode.pathToAsOWL();
			}

			//If the other pather has found this node, return the path of the checkingnode plus 
			//the reversed path for the node in the other checking list
			for(OWLGraphNode curClass : secondHasChecked){
				if(curClass.getValue().equals(checkingFirstNode.getValue())){
					LinkedList<OWLClass> secondHolder = new LinkedList<OWLClass>(curClass.pathToAsOWL());
					LinkedList<OWLClass> returnList = new LinkedList<OWLClass>();

					Collections.reverse(secondHolder);//Reverses the path, so that we may add it onto the return list

					returnList.addAll(checkingFirstNode.pathToAsOWL());
					returnList.addAll(secondHolder);
					return returnList;
				}
			}

			//Creates parents, loops through, sets path length

			LinkedList<OWLClass> checkingFirstParents  = OWLOntologyUsage.B.getParents(checkingFirstNode.getValue());

			//Makes sure the parents are initialized
			for(OWLClass firstParent : checkingFirstParents){	
				Boolean hasAdded = false;
				for(OWLGraphNode checkedNode : firstHasChecked){
					if(checkedNode.getValue().equals(firstParent)){
						checkingFirstNode.getParents().add(checkedNode);
						hasAdded = true;
						break;
					}
				}
				if(!hasAdded){	//If the parent is not already initialized, init it
					checkingFirstNode.getParents().add(new OWLGraphNode(firstParent));

					//sets the path
					checkingFirstNode.getParents().getLast().pathTo.addAll(checkingFirstNode.pathTo());
					checkingFirstNode.getParents().getLast().pathTo.add(checkingFirstNode.getParents().getLast());
					firstHasChecked.addAll(checkingFirstNode.getParents());
					firstFrontier.add(checkingFirstNode.getParents().getLast());
				}
			}
			
			/*
			 *Second word 
			 */
			OWLGraphNode checkingSecondNode = secondFrontier.remove();
			System.out.println("Second word: " + checkingSecondNode.getValue());

			if(checkingSecondNode.getValue().equals(firstOWLGraphNode.getValue())){	//If the checking node is end node, return its path
				return checkingSecondNode.pathToAsOWL();
			}

			//If the other pather has found this node, return the path of the checkingnode plus 
			//the reversed path for the node in the other checking list
			for(OWLGraphNode curClass : firstHasChecked){
				if(curClass.getValue().equals(checkingSecondNode.getValue())){
					LinkedList<OWLClass> firstHolder = new LinkedList<OWLClass>(curClass.pathToAsOWL());
					LinkedList<OWLClass> returnList = new LinkedList<OWLClass>();

					Collections.reverse(firstHolder);//Reverses the path, so that we may add it onto the return list

					returnList.addAll(checkingSecondNode.pathToAsOWL());
					returnList.addAll(firstHolder);
					return returnList;
				}
			}

			//Creates parents, loops through, sets path length

			LinkedList<OWLClass> checkingSecondParents  = OWLOntologyUsage.B.getParents(checkingSecondNode.getValue());

			//Makes sure the parents are initialized
			for(OWLClass secondParent : checkingSecondParents){	
				Boolean hasAdded = false;
				for(OWLGraphNode checkedNode : secondHasChecked){
					if(checkedNode.getValue().equals(secondParent)){
						checkingSecondNode.getParents().add(checkedNode);
						hasAdded = true;
						break;
					}
				}
				if(!hasAdded){	//If the parent is not already initialized, init it
					checkingSecondNode.getParents().add(new OWLGraphNode(secondParent));

					//sets the path
					checkingSecondNode.getParents().getLast().pathTo.addAll(checkingSecondNode.pathTo());
					checkingSecondNode.getParents().getLast().pathTo.add(checkingSecondNode.getParents().getLast());
					secondHasChecked.addAll(checkingSecondNode.getParents());
					secondFrontier.add(checkingSecondNode.getParents().getLast());
				}
			}
		}
		return new LinkedList<OWLClass>();
	}
} 

