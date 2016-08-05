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
	
	public int getIndex(){return (int)verb.pseudoPosition()-1;}

	public int[] getModIndex(){
		int[] ModIndex = new int[modifiers.size()];
		int i = 0;
		for (IndexedWord mod: modifiers){
			ModIndex[i] = (int)mod.pseudoPosition()-1;
		}
		return ModIndex;
	}
}
