package langanal.sentence.base;

import edu.stanford.nlp.ling.IndexedWord;

import java.util.LinkedList;

public class VerbPhrase {
	IndexedWord verb;
	LinkedList<IndexedWord> modifiers;

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
