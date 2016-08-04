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
}
