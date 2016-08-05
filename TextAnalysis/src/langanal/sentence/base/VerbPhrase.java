package langanal.sentence.base;

import java.util.LinkedList;

import edu.stanford.nlp.ling.IndexedWord;

public class VerbPhrase {
	IndexedWord verb;
	LinkedList<IndexedWord> modifiers;
	
	VerbPhrase(IndexedWord verb, LinkedList<IndexedWord> modifiers){
		this.verb = verb;
		this.modifiers = modifiers;
	}
	
	public boolean addMod(IndexedWord modifier){
		if(!modifiers.contains(modifier)){
			modifiers.add(modifier);
			return true;
		}
		return false;
	}
	
	public String toString(){
		String returnString = "Verb: " + verb;
		for(IndexedWord iw : modifiers){
			returnString += '\n' + "    " + iw.toString();
		}
		returnString += '\n';
		return returnString;
	}
}
