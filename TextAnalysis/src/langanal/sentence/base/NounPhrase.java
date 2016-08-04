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
	
}
