package langanal.sentence.base;

import java.util.LinkedList;

import edu.stanford.nlp.ling.IndexedWord;

public class NounPhrase {
	IndexedWord noun;
	LinkedList<IndexedWord> modifiers;
	
	NounPhrase(IndexedWord noun, LinkedList<IndexedWord> modifiers){
		this.noun = noun;
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
		String returnString = "Noun: " + noun;
		for(IndexedWord iw : modifiers){
			returnString += '\n' + "    " + iw.toString();
		}
		returnString += '\n';
		return returnString;
	}
}
